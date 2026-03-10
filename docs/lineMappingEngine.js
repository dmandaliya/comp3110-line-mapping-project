/**
 * COMP 3110 Line Mapping Engine
 * JavaScript port of the Java line mapping algorithm
 * 
 * This implements the same algorithm as LineMappingTool.java:
 * 1. Normalize and tokenize lines
 * 2. Find LCS anchors
 * 3. Local matching around anchors
 * 4. Block relocation detection
 * 5. Gap interpolation
 * 6. Global fallback
 */

class LineMappingEngine {
    constructor() {
        this.WINDOW_SIZE = 60;
        this.LOCAL_SEARCH_WINDOW = 20;
        this.MIN_SIMILARITY = 0.30;
        this.STRICT_SIMILARITY = 0.55;
    }

    /**
     * Normalize a line for comparison
     */
    normalize(line) {
        if (!line) return '';
        let normalized = line.trim();
        if (!normalized) return '';
        // Remove trailing semicolons and braces
        normalized = normalized.replace(/[;{}]+$/, '');
        return normalized.trim();
    }

    /**
     * Tokenize a line into words
     */
    tokenize(line) {
        return line
            .split(/[^A-Za-z0-9_]+/)
            .filter(token => token.length > 0);
    }

    /**
     * Calculate Jaccard similarity based on character sets
     */
    surfaceSimilarity(str1, str2) {
        const set1 = new Set(str1.split(''));
        const set2 = new Set(str2.split(''));
        
        if (set1.size === 0 && set2.size === 0) return 1.0;
        
        const intersection = new Set([...set1].filter(x => set2.has(x)));
        const union = new Set([...set1, ...set2]);
        
        return intersection.size / union.size;
    }

    /**
     * Combined similarity score (token + surface)
     */
    similarity(line1, line2) {
        if (line1 === line2) return 1.0;

        const tokens1 = this.tokenize(line1);
        const tokens2 = this.tokenize(line2);

        if (tokens1.length === 0 || tokens2.length === 0) {
            return this.surfaceSimilarity(line1, line2);
        }

        // Count matching tokens
        let matches = 0;
        for (const token of tokens1) {
            if (tokens2.includes(token)) matches++;
        }

        const tokenSim = matches / Math.max(tokens1.length, tokens2.length);
        const surfSim = this.surfaceSimilarity(line1, line2);

        return 0.7 * tokenSim + 0.3 * surfSim;
    }

    /**
     * Find LCS-based anchors between two arrays
     */
    findLCSAnchors(oldLines, newLines) {
        const n = oldLines.length;
        const m = newLines.length;
        
        // Normalize all lines
        const normalizedOld = oldLines.map(line => this.normalize(line));
        const normalizedNew = newLines.map(line => this.normalize(line));

        // Build LCS DP table
        const dp = Array(n + 1).fill(null).map(() => Array(m + 1).fill(0));

        for (let i = n - 1; i >= 0; i--) {
            for (let j = m - 1; j >= 0; j--) {
                if (normalizedOld[i] && normalizedOld[i] === normalizedNew[j]) {
                    dp[i][j] = 1 + dp[i + 1][j + 1];
                } else {
                    dp[i][j] = Math.max(dp[i + 1][j], dp[i][j + 1]);
                }
            }
        }

        // Extract anchors
        const anchors = [];
        let i = 0, j = 0;
        while (i < n && j < m) {
            if (normalizedOld[i] && normalizedOld[i] === normalizedNew[j]) {
                anchors.push([i, j]);
                i++;
                j++;
            } else if (dp[i + 1][j] >= dp[i][j + 1]) {
                i++;
            } else {
                j++;
            }
        }

        return anchors;
    }

    /**
     * Local search for best match within a window
     */
    localSearch(oldLines, newLines, oldIdx, start, end, usedSet, minScore) {
        start = Math.max(start, 0);
        end = Math.min(end, newLines.length - 1);

        const targetLine = oldLines[oldIdx];
        let bestScore = minScore;
        let bestPos = -1;

        for (let j = start; j <= end; j++) {
            if (usedSet.has(j)) continue;
            
            const score = this.similarity(targetLine, newLines[j]);
            if (score > bestScore) {
                bestScore = score;
                bestPos = j;
            }
        }

        return bestPos;
    }

    /**
     * Detect and map relocated code blocks
     */
    blockRelocate(oldLines, newLines, mapping, usedSet) {
        const n = oldLines.length;
        const window = this.WINDOW_SIZE;

        for (let i = 0; i < n; ) {
            if (mapping[i] !== -1) {
                i++;
                continue;
            }

            // Find the extent of unmapped block
            let j = i;
            while (j < n && mapping[j] === -1) j++;
            const blockLen = j - i;

            if (blockLen < 3) {
                i = j;
                continue;
            }

            // Search for relocated block
            const searchStart = Math.max(0, i - window);
            const searchEnd = Math.min(newLines.length - 1, i + window);

            let bestAvg = 0.0;
            let bestPos = -1;
            let bestHits = 0;

            for (let pos = searchStart; pos <= searchEnd - blockLen + 1; pos++) {
                let hits = 0;
                let sum = 0.0;

                for (let k = 0; k < blockLen; k++) {
                    const score = this.similarity(oldLines[i + k], newLines[pos + k]);
                    if (score > 0.4) {
                        sum += score;
                        hits++;
                    }
                }

                if (hits === 0) continue;
                const avg = sum / blockLen;

                if (avg > 0.45 && hits >= Math.max(2, Math.floor(0.6 * blockLen))) {
                    if (avg > bestAvg) {
                        bestAvg = avg;
                        bestPos = pos;
                        bestHits = hits;
                    }
                }
            }

            // Apply block mapping if found
            if (bestPos !== -1 && bestHits >= Math.max(2, Math.floor(0.6 * blockLen))) {
                for (let k = 0; k < blockLen; k++) {
                    const pos = bestPos + k;
                    if (!usedSet.has(pos)) {
                        mapping[i + k] = pos;
                        usedSet.add(pos);
                    }
                }
            }

            i = j;
        }
    }

    /**
     * Interpolate small gaps between mapped lines
     */
    interpolate(mapping) {
        const n = mapping.length;

        for (let i = 0; i < n; ) {
            if (mapping[i] !== -1) {
                i++;
                continue;
            }

            const start = i - 1;
            while (i < n && mapping[i] === -1) i++;
            const end = i;

            if (start < 0 || end >= n) continue;

            const left = mapping[start];
            const right = mapping[end];

            if (left === -1 || right === -1) continue;

            const gapLen = end - start - 1;
            const delta = right - left;

            // Interpolate small gaps with reasonable deltas
            if (gapLen <= 3 && Math.abs(delta) <= 6 && delta > gapLen) {
                const step = delta / (end - start);
                for (let k = start + 1; k < end; k++) {
                    mapping[k] = Math.round(left + step * (k - start));
                }
            }
        }
    }

    /**
     * Main line mapping computation
     */
    compute(oldLines, newLines) {
        const n = oldLines.length;
        const mapping = Array(n).fill(-1);
        const usedSet = new Set();

        // Step 1: Find LCS anchors
        const anchors = this.findLCSAnchors(oldLines, newLines);
        for (const [oldIdx, newIdx] of anchors) {
            if (!usedSet.has(newIdx)) {
                mapping[oldIdx] = newIdx;
                usedSet.add(newIdx);
            }
        }

        // Step 2: Local matching around anchors
        for (let i = 0; i < n; i++) {
            if (mapping[i] !== -1) continue;

            // Find left and right boundaries
            let left = -1, right = -1;

            for (let k = i - 1; k >= 0; k--) {
                if (mapping[k] !== -1) {
                    left = mapping[k];
                    break;
                }
            }

            for (let k = i + 1; k < n; k++) {
                if (mapping[k] !== -1) {
                    right = mapping[k];
                    break;
                }
            }

            const start = (left === -1) ? 0 : left - this.LOCAL_SEARCH_WINDOW;
            const end = (right === -1) ? newLines.length - 1 : right + this.LOCAL_SEARCH_WINDOW;

            const pos = this.localSearch(oldLines, newLines, i, start, end, usedSet, this.MIN_SIMILARITY);
            if (pos !== -1) {
                mapping[i] = pos;
                usedSet.add(pos);
            }
        }

        // Step 3: Block relocation
        this.blockRelocate(oldLines, newLines, mapping, usedSet);

        // Step 4: Gap interpolation
        this.interpolate(mapping);

        // Step 5: Strict global fallback
        for (let i = 0; i < n; i++) {
            if (mapping[i] !== -1) continue;

            const raw = oldLines[i].trim();
            if (!raw || /^[;{}]+$/.test(raw)) continue;

            const pos = this.localSearch(oldLines, newLines, i, 0, newLines.length - 1, usedSet, this.STRICT_SIMILARITY);
            if (pos !== -1) {
                mapping[i] = pos;
                usedSet.add(pos);
            }
        }

        // Convert to 1-indexed results
        return mapping.map((newIdx, oldIdx) => ({
            oldLine: oldIdx + 1,
            newLine: newIdx === -1 ? -1 : newIdx + 1
        }));
    }

    /**
     * Analyze the mapping to categorize line changes
     */
    analyzeMapping(mappings, oldLines, newLines) {
        const stats = {
            unchanged: 0,
            changed: 0,
            deleted: 0,
            inserted: 0
        };

        const usedNewLines = new Set();
        const lineCategories = [];

        // Categorize old lines
        for (const mapping of mappings) {
            const oldIdx = mapping.oldLine - 1;
            const newIdx = mapping.newLine - 1;

            let category;
            
            if (newIdx === -2) { // -1 in 0-indexed
                category = 'deleted';
                stats.deleted++;
            } else {
                usedNewLines.add(newIdx);
                const oldNormalized = this.normalize(oldLines[oldIdx]);
                const newNormalized = this.normalize(newLines[newIdx]);
                
                if (oldNormalized === newNormalized) {
                    category = 'unchanged';
                    stats.unchanged++;
                } else {
                    category = 'changed';
                    stats.changed++;
                }
            }

            lineCategories.push({
                oldLine: mapping.oldLine,
                newLine: mapping.newLine,
                category: category
            });
        }

        // Count inserted lines (new lines not mapped from old)
        for (let i = 0; i < newLines.length; i++) {
            if (!usedNewLines.has(i)) {
                stats.inserted++;
            }
        }

        return {
            stats,
            lineCategories,
            usedNewLines
        };
    }
}

// Export for use in app.js
if (typeof module !== 'undefined' && module.exports) {
    module.exports = LineMappingEngine;
}
