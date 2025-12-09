import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Evaluator for COMP-3110 line mapping project.
 *
 * Compares:
 *   1) Your tool's output  (oldLine newLine, one per line)
 *   2) Professor's XML file (with <VERSION NUMBER="2"> and <LOCATION ORIG= NEW=>)
 *
 * and classifies each ORIG line as:
 *   - CORRECT  : tool mapping matches one of the gold NEW values (supports ALT)
 *   - CHANGE   : tool mapping is present but maps to the wrong NEW
 *   - SPURIOUS : gold says the line is deleted (NEW = -1), but tool mapped it
 *   - ELIM     : gold says the line should map (NEW != -1), but tool says -1 or missing
 *
 * Usage:
 *   java -cp src Evaluator out/ArrayReference_deep.out data/ArrayReference.xml
 */
public class Evaluator {

    /** Holds gold mappings: ORIG line -> list of acceptable NEW lines (may include -1). */
    public static class GoldMapping {
        Map<Integer, List<Integer>> goldNews = new HashMap<>();
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.err.println("Usage: java Evaluator <toolOutput> <xmlFile>");
            System.exit(1);
        }

        Path toolOutPath = Paths.get(args[0]);
        Path xmlPath = Paths.get(args[1]);

        Map<Integer, Integer> toolMapping = readToolOutput(toolOutPath);
        GoldMapping gold = readGoldXML(xmlPath);

        evaluate(toolMapping, gold);
    }

    /**
     * Reads your tool's output:
     *   <oldLine> <newLine>
     */
    private static Map<Integer, Integer> readToolOutput(Path p) throws IOException {
        Map<Integer, Integer> map = new HashMap<>();
        try (BufferedReader br = Files.newBufferedReader(p)) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split("\\s+");
                if (parts.length < 2) continue;
                int oldLine = Integer.parseInt(parts[0]);
                int newLine = Integer.parseInt(parts[1]);
                map.put(oldLine, newLine);
            }
        }
        return map;
    }

    /**
     * Reads the professor's XML file and extracts VERSION NUMBER="2"
     * into a mapping ORIG -> list of acceptable NEW values.
     * If the same ORIG appears multiple times, we treat all of its NEW values
     * as valid (this supports ALT mappings).
     */
    private static GoldMapping readGoldXML(Path xmlPath) throws Exception {
        GoldMapping gm = new GoldMapping();

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        try (var in = Files.newInputStream(xmlPath)) {
            Document doc = db.parse(in);
            doc.getDocumentElement().normalize();

            NodeList versionNodes = doc.getElementsByTagName("VERSION");
            Element version2 = null;
            for (int i = 0; i < versionNodes.getLength(); i++) {
                Element v = (Element) versionNodes.item(i);
                String num = v.getAttribute("NUMBER");
                if ("2".equals(num)) {
                    version2 = v;
                    break;
                }
            }

            if (version2 == null) {
                throw new IllegalStateException("No <VERSION NUMBER=\"2\"> in " + xmlPath);
            }

            NodeList locs = version2.getElementsByTagName("LOCATION");
            for (int i = 0; i < locs.getLength(); i++) {
                Element loc = (Element) locs.item(i);
                int orig = Integer.parseInt(loc.getAttribute("ORIG").trim());
                int neu = Integer.parseInt(loc.getAttribute("NEW").trim());
                gm.goldNews.computeIfAbsent(orig, k -> new ArrayList<>()).add(neu);
            }
        }

        return gm;
    }

    /**
     * Compare your mapping vs gold and print classification for each ORIG.
     */
    private static void evaluate(Map<Integer, Integer> tool, GoldMapping gold) {
        int correct = 0;
        int change  = 0;
        int spurious = 0;
        int elim    = 0;

        System.out.println("orig\tgoldNew(s)\ttoolNew\tclass");

        // Sort by ORIG line for nicer output
        for (Map.Entry<Integer, List<Integer>> e
                : new TreeMap<>(gold.goldNews).entrySet()) {

            int orig = e.getKey();
            List<Integer> goldNews = e.getValue();

            boolean hasTool = tool.containsKey(orig);
            int toolNew = hasTool ? tool.get(orig) : Integer.MIN_VALUE;

            String clazz;

            if (!hasTool) {
                // Your tool gave no mapping for this ORIG line at all
                clazz = "ELIM";
                elim++;
                toolNew = -9999; // just for printing
            } else if (goldNews.contains(toolNew)) {
                // Matches at least one acceptable NEW (supports ALT)
                clazz = "CORRECT";
                correct++;
            } else if (goldNews.contains(-1) && toolNew != -1) {
                // Gold says it's deleted, but tool mapped it somewhere
                clazz = "SPURIOUS";
                spurious++;
            } else if (!goldNews.contains(-1) && toolNew == -1) {
                // Gold says it should map, but tool says deleted
                clazz = "ELIM";
                elim++;
            } else {
                // Tool mapped it, but to the wrong NEW line
                clazz = "CHANGE";
                change++;
            }

            System.out.printf(
                "%d\t%s\t%d\t%s%n",
                orig,
                goldNews.toString(),
                toolNew,
                clazz
            );
        }

        int total = correct + change + spurious + elim;
        System.out.println();
        System.out.println("Summary:");
        System.out.println("  CORRECT : " + correct);
        System.out.println("  CHANGE  : " + change);
        System.out.println("  SPURIOUS: " + spurious);
        System.out.println("  ELIM    : " + elim);
        System.out.println("  TOTAL   : " + total);
        if (total > 0) {
            double acc = (correct * 100.0) / total;
            System.out.printf("  ACCURACY: %.2f%%%n", acc);
        }
    }
}
