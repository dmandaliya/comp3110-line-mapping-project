import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * COMP-3110 Line Mapping Project
 *
 * Generic strategy (no per-file hacks):
 *   1. Preprocess lines: normalize, tokenize, classify "noise".
 *      - Noise = blank / brace-only / punctuation-only lines.
 *      - Comment lines are kept (NOT treated as noise).
 *
 *   2. Find strong "unchanged" anchors via LCS on normalized text
 *      for non-noise lines.
 *
 *   3. For unmapped lines, search for the best match in a local
 *      window using a hybrid similarity:
 *         - token-based Dice coefficient
 *         - character-level LCS ratio
 *         - special treatment for exact-normalized equality
 *      Reuse of new lines is allowed but softly penalized.
 *
 *   4. Global "rescue" pass for still-unmapped lines:
 *      - search all new lines, stricter threshold
 *
 *   5. Fill small unmapped gaps between two anchors by interpolation
 *      with similarity checks. Allows "many old -> one new" collapses
 *      when that is consistently the best match.
 *
 * Output (for each original line i, 1-based):
 *      i \t newLineIndexOrMinusOne
 */
public class LineMappingTool {

    /* ========================  ENTRY POINT  ======================== */

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.err.println("Usage: java LineMappingTool <origFile> <newFile>");
            return;
        }

        List<String> origLines = readAllLines(args[0]);
        List<String> newLines  = readAllLines(args[1]);

        int[] mapping = computeMapping(origLines, newLines);

        for (int i = 0; i < mapping.length; i++) {
            int orig1 = i + 1;
            int new1  = (mapping[i] < 0) ? -1 : mapping[i] + 1;
            System.out.println(orig1 + "\t" + new1);
        }
    }

    private static List<String> readAllLines(String path) throws IOException {
        List<String> result = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(Paths.get(path), StandardCharsets.UTF_8)) {
            String line;
            while ((line = br.readLine()) != null) {
                result.add(line);
            }
        }
        return result;
    }

    /* ========================  CORE ALGORITHM  ======================== */

    public static int[] computeMapping(List<String> origLines, List<String> newLines) {
        int n = origLines.size();
        int m = newLines.size();

        int[] map = new int[n];
        Arrays.fill(map, -1);

        LineInfo[] orig = buildLineInfos(origLines);
        LineInfo[] news = buildLineInfos(newLines);

        // 1) LCS anchors on normalized (non-noise) lines
        int[] lcsMap = lcsAnchors(orig, news);
        boolean[] newUsed = new boolean[m];
        int[] usedCount = new int[m];

        for (int i = 0; i < n; i++) {
            if (lcsMap[i] != -1) {
                map[i] = lcsMap[i];
                newUsed[lcsMap[i]] = true;
                usedCount[lcsMap[i]]++;
            }
        }

        // Precompute nearest mapped neighbors
        int[] leftMapped  = nearestMappedLeft(map);
        int[] rightMapped = nearestMappedRight(map);

        // 2) Local window heuristic for unmapped, non-noise lines
        for (int i = 0; i < n; i++) {
            if (map[i] != -1) continue;
            if (orig[i].isNoise) continue;

            int windowStart = 0;
            int windowEnd   = m - 1;

            int left = leftMapped[i];
            int right = rightMapped[i];

            // Narrow window based on mapped neighbors if available
            if (left != -1) {
                int leftNew = map[left];
                windowStart = Math.max(windowStart, leftNew - 50);
            }
            if (right != -1) {
                int rightNew = map[right];
                windowEnd = Math.min(windowEnd, rightNew + 50);
            }

            if (windowStart > windowEnd) {
                windowStart = 0;
                windowEnd   = m - 1;
            }

            int bestJ = -1;
            double bestScore = 0.0;

            for (int j = windowStart; j <= windowEnd; j++) {
                if (j < 0 || j >= m) continue;
                if (news[j].isNoise) continue; // skip brace-only / blank

                double score = similarity(orig[i], news[j]);
                if (score <= 0.0) continue;

                boolean exactSameNorm = !orig[i].norm.isEmpty()
                        && orig[i].norm.equals(news[j].norm);

                double penalized = score;
                if (!exactSameNorm && usedCount[j] > 0) {
                    // softly penalize heavily reused lines, but never forbid
                    penalized -= 0.03 * Math.min(usedCount[j], 5);
                    if (penalized < 0.0) penalized = 0.0;
                }

                if (penalized > bestScore) {
                    bestScore = penalized;
                    bestJ = j;
                }
            }

            if (bestJ != -1) {
                boolean exactSameNorm = !orig[i].norm.isEmpty()
                        && orig[i].norm.equals(news[bestJ].norm);

                // main heuristic thresholds
                if (exactSameNorm || bestScore >= 0.60) {
                    map[i] = bestJ;
                    newUsed[bestJ] = true;
                    usedCount[bestJ]++;
                }
            }
        }

        // 3) Global rescue pass for still-unmapped, non-noise lines
        for (int i = 0; i < n; i++) {
            if (map[i] != -1) continue;
            if (orig[i].isNoise) continue;

            int bestJ = -1;
            double bestScore = 0.0;

            for (int j = 0; j < m; j++) {
                if (news[j].isNoise) continue;

                double score = similarity(orig[i], news[j]);
                if (score <= 0.0) continue;

                boolean exactSameNorm = !orig[i].norm.isEmpty()
                        && orig[i].norm.equals(news[j].norm);

                double penalized = score;
                if (!exactSameNorm && usedCount[j] > 0) {
                    penalized -= 0.03 * Math.min(usedCount[j], 5);
                    if (penalized < 0.0) penalized = 0.0;
                }

                if (penalized > bestScore) {
                    bestScore = penalized;
                    bestJ = j;
                }
            }

            if (bestJ != -1) {
                boolean exactSameNorm = !orig[i].norm.isEmpty()
                        && orig[i].norm.equals(news[bestJ].norm);

                if (exactSameNorm || bestScore >= 0.70) {
                    map[i] = bestJ;
                    newUsed[bestJ] = true;
                    usedCount[bestJ]++;
                }
            }
        }

        // 4) Fill small gaps between two mapped anchors by interpolation
        fillSmallGaps(orig, news, map, usedCount);

        return map;
    }

    /* ========================  LINE INFO  ======================== */

    private static class LineInfo {
        final int index;
        final String raw;
        final String norm;
        final String[] tokens;
        final boolean isNoise;

        LineInfo(int index, String raw, String norm, String[] tokens, boolean isNoise) {
            this.index = index;
            this.raw = raw;
            this.norm = norm;
            this.tokens = tokens;
            this.isNoise = isNoise;
        }
    }

    private static LineInfo[] buildLineInfos(List<String> lines) {
        LineInfo[] arr = new LineInfo[lines.size()];
        for (int i = 0; i < lines.size(); i++) {
            String raw = lines.get(i);
            String norm = normalize(raw);
            boolean isNoise = isNoiseLine(norm);
            String[] tokens = tokenize(norm);
            arr[i] = new LineInfo(i, raw, norm, tokens, isNoise);
        }
        return arr;
    }

    private static String normalize(String s) {
        if (s == null) return "";
        String line = s;

        // Strip line comments
        int idx = line.indexOf("//");
        if (idx >= 0) {
            line = line.substring(0, idx);
        }

        // Remove simple /* ... */ segments on the same line
        while (true) {
            int start = line.indexOf("/*");
            if (start < 0) break;
            int end = line.indexOf("*/", start + 2);
            if (end < 0) {
                line = line.substring(0, start);
                break;
            } else {
                line = line.substring(0, start) + " " + line.substring(end + 2);
            }
        }

        String trimmed = line.trim();
        trimmed = trimmed.replaceAll("\\s+", " ");
        return trimmed;
    }

    /**
     * Noise = blank or brace/punctuation-only lines.
     * Comments are NOT treated as noise; they are allowed as anchors/matches.
     */
    private static boolean isNoiseLine(String norm) {
        String t = norm.trim();
        if (t.isEmpty()) return true;
        if (t.matches("[{};]+")) return true;
        return false;
    }

    private static String[] tokenize(String s) {
        if (s.isEmpty()) return new String[0];
        String[] parts = s.split("[^A-Za-z0-9_]+");
        List<String> toks = new ArrayList<>();
        for (String p : parts) {
            if (!p.isEmpty()) toks.add(p);
        }
        return toks.toArray(new String[0]);
    }

    /* ========================  LCS ANCHORS  ======================== */

    private static int[] lcsAnchors(LineInfo[] orig, LineInfo[] news) {
        int n = orig.length;
        int m = news.length;
        int[][] dp = new int[n + 1][m + 1];

        for (int i = 1; i <= n; i++) {
            String a = orig[i - 1].isNoise ? "" : orig[i - 1].norm;
            for (int j = 1; j <= m; j++) {
                String b = news[j - 1].isNoise ? "" : news[j - 1].norm;
                if (!a.isEmpty() && a.equals(b)) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                }
            }
        }

        int[] map = new int[n];
        Arrays.fill(map, -1);

        int i = n, j = m;
        while (i > 0 && j > 0) {
            String a = orig[i - 1].isNoise ? "" : orig[i - 1].norm;
            String b = news[j - 1].isNoise ? "" : news[j - 1].norm;
            if (!a.isEmpty() && a.equals(b)) {
                map[i - 1] = j - 1;
                i--;
                j--;
            } else if (dp[i - 1][j] >= dp[i][j - 1]) {
                i--;
            } else {
                j--;
            }
        }

        return map;
    }

    /* ========================  NEIGHBOR HELPERS  ======================== */

    private static int[] nearestMappedLeft(int[] map) {
        int n = map.length;
        int[] res = new int[n];
        int last = -1;
        for (int i = 0; i < n; i++) {
            res[i] = last;
            if (map[i] != -1) last = i;
        }
        return res;
    }

    private static int[] nearestMappedRight(int[] map) {
        int n = map.length;
        int[] res = new int[n];
        int last = -1;
        for (int i = n - 1; i >= 0; i--) {
            res[i] = last;
            if (map[i] != -1) last = i;
        }
        return res;
    }

    /* ========================  GAP FILLING  ======================== */

    private static void fillSmallGaps(LineInfo[] orig, LineInfo[] news,
                                      int[] map, int[] usedCount) {
        int n = map.length;
        int m = news.length;

        int i = 0;
        while (i < n) {
            if (map[i] != -1) {
                i++;
                continue;
            }

            int start = i;
            int end = i;
            while (end + 1 < n && map[end + 1] == -1) {
                end++;
            }
            int blockLen = end - start + 1;

            // Only try to interpolate small blocks
            if (blockLen <= 6) {
                int left = start - 1;
                int right = end + 1;
                if (left >= 0 && right < n && map[left] != -1 && map[right] != -1) {
                    int leftNew = map[left];
                    int rightNew = map[right];
                    if (rightNew > leftNew + 1) {
                        int available = rightNew - leftNew - 1;
                        if (available > 0) {
                            for (int k = start; k <= end; k++) {
                                int pos = k - start;
                                int candidate = leftNew + 1 + Math.min(pos, available - 1);
                                if (candidate < 0 || candidate >= m) continue;

                                if (news[candidate].isNoise && orig[k].isNoise) {
                                    // For noise-on-noise, require exact norm match if present
                                    if (!orig[k].norm.isEmpty()
                                            && orig[k].norm.equals(news[candidate].norm)) {
                                        map[k] = candidate;
                                        usedCount[candidate]++;
                                    }
                                    continue;
                                }

                                if (news[candidate].isNoise) continue;

                                double score = similarity(orig[k], news[candidate]);
                                if (score <= 0.0) continue;

                                boolean exactSameNorm = !orig[k].norm.isEmpty()
                                        && orig[k].norm.equals(news[candidate].norm);

                                double penalized = score;
                                if (!exactSameNorm && usedCount[candidate] > 0) {
                                    penalized -= 0.03 * Math.min(usedCount[candidate], 5);
                                    if (penalized < 0.0) penalized = 0.0;
                                }

                                if (exactSameNorm || penalized >= 0.50) {
                                    map[k] = candidate;
                                    usedCount[candidate]++;
                                }
                            }
                        }
                    }
                }
            }

            i = end + 1;
        }
    }

    /* ========================  SIMILARITY  ======================== */

    private static double similarity(LineInfo a, LineInfo b) {
        if (a.norm.isEmpty() && b.norm.isEmpty()) return 1.0;
        if (a.norm.isEmpty() || b.norm.isEmpty()) return 0.0;

        if (a.norm.equals(b.norm)) {
            return 1.0;
        }

        double tokenSim = tokenDice(a.tokens, b.tokens);
        double charSim  = charLCSRatio(a.norm, b.norm);

        double base = 0.7 * tokenSim + 0.3 * charSim;

        // Extra attention to RHS of assignments (often preserved)
        if (a.norm.contains("=") && b.norm.contains("=")) {
            String rhsA = rhs(a.norm);
            String rhsB = rhs(b.norm);
            if (!rhsA.isEmpty() && !rhsB.isEmpty()) {
                String[] rtA = tokenize(rhsA);
                String[] rtB = tokenize(rhsB);
                double rhsToken = tokenDice(rtA, rtB);
                double rhsChar  = charLCSRatio(rhsA, rhsB);
                double rhsScore = 0.7 * rhsToken + 0.3 * rhsChar;
                base = 0.5 * base + 0.5 * rhsScore;
            }
        }

        return base;
    }

    private static String rhs(String s) {
        int idx = s.lastIndexOf('=');
        if (idx < 0 || idx == s.length() - 1) return "";
        return s.substring(idx + 1).trim();
    }

    private static double tokenDice(String[] a, String[] b) {
        if (a.length == 0 && b.length == 0) return 1.0;
        if (a.length == 0 || b.length == 0) return 0.0;

        Map<String, Integer> fa = new HashMap<>();
        Map<String, Integer> fb = new HashMap<>();

        for (String t : a) fa.put(t, fa.getOrDefault(t, 0) + 1);
        for (String t : b) fb.put(t, fb.getOrDefault(t, 0) + 1);

        int inter = 0;
        int sumA = 0, sumB = 0;
        for (int v : fa.values()) sumA += v;
        for (int v : fb.values()) sumB += v;

        for (Map.Entry<String, Integer> e : fa.entrySet()) {
            int va = e.getValue();
            int vb = fb.getOrDefault(e.getKey(), 0);
            inter += Math.min(va, vb);
        }

        return (2.0 * inter) / (sumA + sumB);
    }

    private static double charLCSRatio(String a, String b) {
        if (a.isEmpty() && b.isEmpty()) return 1.0;
        if (a.isEmpty() || b.isEmpty()) return 0.0;

        int maxLen = 120;
        if (a.length() > maxLen) a = a.substring(0, maxLen);
        if (b.length() > maxLen) b = b.substring(0, maxLen);

        int la = a.length();
        int lb = b.length();
        int[][] dp = new int[la + 1][lb + 1];

        for (int i = 1; i <= la; i++) {
            char ca = a.charAt(i - 1);
            for (int j = 1; j <= lb; j++) {
                char cb = b.charAt(j - 1);
                if (ca == cb) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                }
            }
        }

        int lcs = dp[la][lb];
        int denom = Math.max(la, lb);
        return (double) lcs / denom;
    }
}
