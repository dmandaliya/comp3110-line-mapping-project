import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

/**
 * Simple line mapping tool for COMP-3110 project.
 * 
 * Given two text files (e.g., two versions of ArrayReference.java),
 * it computes which lines of the old file map to which lines in the new file.
 *
 * Usage:
 *   java LineMappingTool oldFile.java newFile.java
 *
 * (You can later extend this to write CSV instead of just printing.)
 */
public class LineMappingTool {

    public enum Status {
        UNCHANGED,
        MODIFIED,
        INSERTED,
        DELETED
    }

    public static class MappingEntry {
        public final int oldLine;   // 1-based, -1 if inserted
        public final String oldText;
        public final int newLine;   // 1-based, -1 if deleted
        public final String newText;
        public final Status status;

        public MappingEntry(int oldLine, String oldText,
                            int newLine, String newText,
                            Status status) {
            this.oldLine = oldLine;
            this.oldText = oldText;
            this.newLine = newLine;
            this.newText = newText;
            this.status = status;
        }

        @Override
        public String toString() {
            return String.format(
                "old:%3d -> new:%3d  [%s]\n  OLD: %s\n  NEW: %s\n",
                oldLine, newLine, status, 
                oldText == null ? "" : oldText,
                newText == null ? "" : newText
            );
        }
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.err.println("Usage: java LineMappingTool <oldFile> <newFile>");
            System.exit(1);
        }

        Path oldPath = Paths.get(args[0]);
        Path newPath = Paths.get(args[1]);

        List<String> oldLines = Files.readAllLines(oldPath);
        List<String> newLines = Files.readAllLines(newPath);

        List<MappingEntry> mappings = mapFiles(oldLines, newLines);

        // For now, just print; later you can write to CSV.
        for (MappingEntry entry : mappings) {
            System.out.println(entry);
        }
    }

    /**
     * Main mapping method.
     * 
     * Strategy:
     * 1. Compute LCS (Longest Common Subsequence) of lines to find UNCHANGED lines.
     * 2. Walk through gaps between unchanged matches:
     *    - If both sides have same number of lines in a gap -> treat as MODIFIED pairs.
     *    - Extra old lines -> DELETED.
     *    - Extra new lines -> INSERTED.
     */
    public static List<MappingEntry> mapFiles(List<String> oldLines, List<String> newLines) {
        // Convert to 0-based lists
        int n = oldLines.size();
        int m = newLines.size();

        // LCS DP table (n+1 x m+1)
        int[][] dp = new int[n + 1][m + 1];
        for (int i = n - 1; i >= 0; i--) {
            for (int j = m - 1; j >= 0; j--) {
                if (oldLines.get(i).equals(newLines.get(j))) {
                    dp[i][j] = dp[i + 1][j + 1] + 1;
                } else {
                    dp[i][j] = Math.max(dp[i + 1][j], dp[i][j + 1]);
                }
            }
        }

        // Backtrack to get list of unchanged matched positions
        List<int[]> lcsPairs = new ArrayList<>(); // each int[]{iOld, jNew}
        int i = 0, j = 0;
        while (i < n && j < m) {
            if (oldLines.get(i).equals(newLines.get(j))) {
                lcsPairs.add(new int[]{i, j});
                i++;
                j++;
            } else if (dp[i + 1][j] >= dp[i][j + 1]) {
                i++;
            } else {
                j++;
            }
        }

        // Walk through both files using the LCS matches as anchors
        List<MappingEntry> result = new ArrayList<>();
        int currOld = 0;
        int currNew = 0;
        int lcsIndex = 0;

        while (lcsIndex <= lcsPairs.size()) {
            int nextOld = n;
            int nextNew = m;
            if (lcsIndex < lcsPairs.size()) {
                nextOld = lcsPairs.get(lcsIndex)[0];
                nextNew = lcsPairs.get(lcsIndex)[1];
            }

            // Handle gap between (currOld, currNew) and (nextOld, nextNew)
            handleGap(oldLines, newLines, currOld, nextOld, currNew, nextNew, result);

            // Then handle the LCS match itself (UNCHANGED)
            if (lcsIndex < lcsPairs.size()) {
                int matchOld = nextOld;
                int matchNew = nextNew;
                String line = oldLines.get(matchOld);
                result.add(new MappingEntry(
                    matchOld + 1, line,
                    matchNew + 1, line,
                    Status.UNCHANGED
                ));
                currOld = matchOld + 1;
                currNew = matchNew + 1;
            }

            lcsIndex++;
        }

        return result;
    }

    /**
     * Handle a "gap" of unmatched lines between two LCS matches.
     *
     * Example:
     * oldLines[currOld .. nextOld-1]
     * newLines[currNew .. nextNew-1]
     *
     * If they have the same length -> treat as MODIFIED pairs.
     * If not -> pair as many as possible as MODIFIED, then mark extra as INSERTED/DELETED.
     */
    private static void handleGap(List<String> oldLines,
                                  List<String> newLines,
                                  int currOld, int nextOld,
                                  int currNew, int nextNew,
                                  List<MappingEntry> result) {

        int oldGapSize = nextOld - currOld;
        int newGapSize = nextNew - currNew;
        int minSize = Math.min(oldGapSize, newGapSize);

        // First, treat minSize lines as MODIFIED (line-to-line mapping)
        for (int k = 0; k < minSize; k++) {
            int oIndex = currOld + k;
            int nIndex = currNew + k;
            String oLine = oldLines.get(oIndex);
            String nLine = newLines.get(nIndex);
            if (oLine.equals(nLine)) {
                // Just in case: they are actually the same, but not in LCS (rare)
                result.add(new MappingEntry(
                    oIndex + 1, oLine,
                    nIndex + 1, nLine,
                    Status.UNCHANGED
                ));
            } else {
                result.add(new MappingEntry(
                    oIndex + 1, oLine,
                    nIndex + 1, nLine,
                    Status.MODIFIED
                ));
            }
        }

        // If there are extra old lines -> DELETED
        for (int k = minSize; k < oldGapSize; k++) {
            int oIndex = currOld + k;
            String oLine = oldLines.get(oIndex);
            result.add(new MappingEntry(
                oIndex + 1, oLine,
                -1, null,
                Status.DELETED
            ));
        }

        // If there are extra new lines -> INSERTED
        for (int k = minSize; k < newGapSize; k++) {
            int nIndex = currNew + k;
            String nLine = newLines.get(nIndex);
            result.add(new MappingEntry(
                -1, null,
                nIndex + 1, nLine,
                Status.INSERTED
            ));
        }
    }
}
