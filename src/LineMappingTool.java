import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * COMP-3110 Line Mapping Tool
 *
 * Light comments: explains structure without excessive detail.
 * Strategy:
 *  - Step 1: Preprocess lines + normalize
 *  - Step 2: Identify strong "unchanged" anchors using LCS
 *  - Step 3: Local matching around anchors using similarity scoring
 *  - Step 4: Block relocation for large moved sections
 *  - Step 5: Small-gap interpolation
 *  - Step 6: Strict global fallback for remaining lines
 */
public class LineMappingTool {

    /** Simple record for output pair. */
    public record Mapping(int oldLine, int newLine) {}

    /** Read file into list of strings. */
    private static List<String> read(String p) throws IOException {
        return Files.readAllLines(Paths.get(p), StandardCharsets.UTF_8);
    }

    /** Normalize line for matching consistency. */
    private static String norm(String s) {
        if (s == null) return "";
        String t = s.strip();
        if (t.isEmpty()) return "";
        t = t.replaceAll("[;{}]+$", "");
        return t.strip();
    }

    /** Tokenizer: split on non-alphanumeric. */
    private static List<String> tokens(String s) {
        return Arrays.stream(s.split("[^A-Za-z0-9_]+"))
                .filter(x -> !x.isEmpty())
                .toList();
    }

    /** Jaccard similarity of characters. */
    private static double surface(String a, String b) {
        Set<Character> A = new HashSet<>();
        Set<Character> B = new HashSet<>();
        for (char c : a.toCharArray()) A.add(c);
        for (char c : b.toCharArray()) B.add(c);
        if (A.isEmpty() && B.isEmpty()) return 1.0;
        Set<Character> inter = new HashSet<>(A);
        inter.retainAll(B);
        Set<Character> uni = new HashSet<>(A);
        uni.addAll(B);
        return (double) inter.size() / (double) uni.size();
    }

    /** Combined token + surface score. */
    private static double score(String a, String b) {
        if (a.equals(b)) return 1.0;

        List<String> ta = tokens(a);
        List<String> tb = tokens(b);

        if (ta.isEmpty() || tb.isEmpty()) {
            return surface(a, b);
        }

        int matches = 0;
        for (String x : ta) {
            if (tb.contains(x)) matches++;
        }

        double tokenSim = (double) matches / Math.max(ta.size(), tb.size());
        double surf = surface(a, b);

        return 0.7 * tokenSim + 0.3 * surf;
    }

    /** LCS anchors based on normalized lines. */
    private static List<int[]> lcsAnchors(List<String> oldLines, List<String> newLines) {
        int n = oldLines.size(), m = newLines.size();
        int[][] dp = new int[n + 1][m + 1];

        String[] A = new String[n];
        String[] B = new String[m];
        for (int i = 0; i < n; i++) A[i] = norm(oldLines.get(i));
        for (int j = 0; j < m; j++) B[j] = norm(newLines.get(j));

        for (int i = n - 1; i >= 0; i--) {
            for (int j = m - 1; j >= 0; j--) {
                if (!A[i].isEmpty() && A[i].equals(B[j])) {
                    dp[i][j] = 1 + dp[i + 1][j + 1];
                } else {
                    dp[i][j] = Math.max(dp[i + 1][j], dp[i][j + 1]);
                }
            }
        }

        List<int[]> anchors = new ArrayList<>();
        int i = 0, j = 0;
        while (i < n && j < m) {
            if (!A[i].isEmpty() && A[i].equals(B[j])) {
                anchors.add(new int[]{i, j});
                i++; j++;
            } else if (dp[i + 1][j] >= dp[i][j + 1]) {
                i++;
            } else {
                j++;
            }
        }
        return anchors;
    }

    /** Local search for best similarity match. */
    private static int localSearch(List<String> oldLines, List<String> newLines,
                                   int idx, int start, int end,
                                   Set<Integer> used, double minScore) {

        start = Math.max(start, 0);
        end   = Math.min(end, newLines.size() - 1);

        String target = oldLines.get(idx);
        double best = minScore;
        int bestPos = -1;

        for (int j = start; j <= end; j++) {
            if (used.contains(j)) continue;
            double s = score(target, newLines.get(j));
            if (s > best) {
                best = s;
                bestPos = j;
            }
        }

        return bestPos;
    }

    /** Try to detect moved blocks. */
    private static void blockRelocate(List<String> oldLines, List<String> newLines,
                                      int[] map, Set<Integer> used) {

        int n = oldLines.size();
        int window = 60;

        for (int i = 0; i < n; ) {
            if (map[i] != -1) { i++; continue; }

            int j = i;
            while (j < n && map[j] == -1) j++;
            int len = j - i;

            if (len < 3) {
                i = j;
                continue;
            }

            int sStart = Math.max(0, i - window);
            int sEnd   = Math.min(newLines.size() - 1, i + window);

            double bestAvg = 0.0;
            int bestPos = -1;
            int bestHits = 0;

            for (int pos = sStart; pos <= sEnd - len + 1; pos++) {
                int hits = 0;
                double sum = 0.0;

                for (int k = 0; k < len; k++) {
                    double sc = score(oldLines.get(i + k),
                                      newLines.get(pos + k));
                    if (sc > 0.4) {
                        sum += sc;
                        hits++;
                    }
                }

                if (hits == 0) continue;
                double avg = sum / len;

                if (avg > 0.45 && hits >= Math.max(2, (int)(0.6 * len))) {
                    if (avg > bestAvg) {
                        bestAvg = avg;
                        bestPos = pos;
                        bestHits = hits;
                    }
                }
            }

            if (bestPos != -1 && bestHits >= Math.max(2, (int)(0.6 * len))) {
                for (int k = 0; k < len; k++) {
                    int p = bestPos + k;
                    if (!used.contains(p)) {
                        map[i + k] = p;
                        used.add(p);
                    }
                }
            }

            i = j;
        }
    }

    /** Interpolate small gaps between anchors. */
    private static void interpolate(int[] map) {
        int n = map.length;

        for (int i = 0; i < n; ) {
            if (map[i] != -1) { i++; continue; }

            int start = i - 1;
            while (i < n && map[i] == -1) i++;
            int end = i;

            if (start < 0 || end >= n) continue;

            int left = map[start];
            int right = map[end];

            if (left == -1 || right == -1) continue;

            int gapLen = end - start - 1;
            int delta = right - left;

            if (gapLen <= 3 && Math.abs(delta) <= 6 && delta > gapLen) {
                double step = (double) delta / (double) (end - start);
                for (int k = start + 1; k < end; k++) {
                    int approx = (int) Math.round(left + step * (k - start));
                    map[k] = approx;
                }
            }
        }
    }

    /** Main mapping pipeline. */
    public static List<Mapping> compute(List<String> oldLines, List<String> newLines) {

        int n = oldLines.size();
        int[] map = new int[n];
        Arrays.fill(map, -1);
        Set<Integer> used = new HashSet<>();

        // Step 1: Anchors via LCS
        for (int[] a : lcsAnchors(oldLines, newLines)) {
            int oi = a[0], ni = a[1];
            if (!used.contains(ni)) {
                map[oi] = ni;
                used.add(ni);
            }
        }

        // Step 2: Local matching around anchors
        for (int i = 0; i < n; i++) {
            if (map[i] != -1) continue;

            int left = -1, right = -1;

            for (int k = i - 1; k >= 0; k--) {
                if (map[k] != -1) { left = map[k]; break; }
            }
            for (int k = i + 1; k < n; k++) {
                if (map[k] != -1) { right = map[k]; break; }
            }

            int start = (left == -1) ? 0 : left - 20;
            int end   = (right == -1) ? newLines.size() - 1 : right + 20;

            int pos = localSearch(oldLines, newLines, i, start, end, used, 0.30);
            if (pos != -1) {
                map[i] = pos;
                used.add(pos);
            }
        }

        // Step 3: Block relocation
        blockRelocate(oldLines, newLines, map, used);

        // Step 4: Gap interpolation
        interpolate(map);

        // Step 5: Strict global fallback
        for (int i = 0; i < n; i++) {
            if (map[i] != -1) continue;

            String raw = oldLines.get(i).strip();
            if (raw.isEmpty() || raw.matches("[;{}]+")) continue;

            int pos = localSearch(oldLines, newLines, i,
                    0, newLines.size() - 1, used, 0.55);

            if (pos != -1) {
                map[i] = pos;
                used.add(pos);
            }
        }

        // Output
        List<Mapping> out = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            out.add(new Mapping(i + 1, map[i] == -1 ? -1 : map[i] + 1));
        }
        return out;
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.err.println("Usage: java LineMappingTool oldFile newFile");
            return;
        }
        List<String> oldLines = read(args[0]);
        List<String> newLines = read(args[1]);

        for (Mapping m : compute(oldLines, newLines)) {
            System.out.println(m.oldLine() + "\t" + m.newLine());
        }
    }
}
