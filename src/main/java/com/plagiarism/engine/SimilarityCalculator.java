package com.plagiarism.engine;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Advanced similarity calculation using multiple algorithms
 */
public class SimilarityCalculator {

    /**
     * Calculates cosine similarity between two documents using TF-IDF vectors
     */
    public static double calculateCosineSimilarity(String text1, String text2) {
        if (text1.isEmpty() || text2.isEmpty()) return 0.0;

        Map<String, Integer> vector1 = getTermFrequency(text1);
        Map<String, Integer> vector2 = getTermFrequency(text2);

        return getCosineSimilarity(vector1, vector2);
    }

    /**
     * Calculates Levenshtein distance-based similarity
     * Returns value between 0 and 1, where 1 is identical
     */
    public static double calculateLevenshteinSimilarity(String str1, String str2) {
        int maxLength = Math.max(str1.length(), str2.length());
        if (maxLength == 0) return 1.0;

        int distance = calculateLevenshteinDistance(str1, str2);
        return 1.0 - (distance / (double) maxLength);
    }

    /**
     * Calculates edit distance using Wagner-Fischer algorithm
     */
    public static int calculateLevenshteinDistance(String str1, String str2) {
        int[][] dp = new int[str1.length() + 1][str2.length() + 1];

        for (int i = 0; i <= str1.length(); i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= str2.length(); j++) {
            dp[0][j] = j;
        }

        for (int i = 1; i <= str1.length(); i++) {
            for (int j = 1; j <= str2.length(); j++) {
                int cost = str1.charAt(i - 1) == str2.charAt(j - 1) ? 0 : 1;
                dp[i][j] = Math.min(Math.min(
                        dp[i - 1][j] + 1,      // deletion
                        dp[i][j - 1] + 1),    // insertion
                        dp[i - 1][j - 1] + cost); // substitution
            }
        }

        return dp[str1.length()][str2.length()];
    }

    /**
     * Calculates N-gram similarity (using bigrams and trigrams)
     */
    public static double calculateNgramSimilarity(String text1, String text2) {
        if (text1.isEmpty() || text2.isEmpty()) return 0.0;

        Set<String> bigrams1 = getNgrams(text1, 2);
        Set<String> bigrams2 = getNgrams(text2, 2);

        Set<String> trigrams1 = getNgrams(text1, 3);
        Set<String> trigrams2 = getNgrams(text2, 3);

        // Jaccard similarity for bigrams and trigrams
        double bigramSimilarity = getJaccardSimilarity(bigrams1, bigrams2);
        double trigramSimilarity = getJaccardSimilarity(trigrams1, trigrams2);

        // Weighted average
        return (bigramSimilarity * 0.4 + trigramSimilarity * 0.6);
    }

    /**
     * Calculates Longest Common Subsequence ratio
     */
    public static double calculateLCSSimilarity(String str1, String str2) {
        if (str1.isEmpty() && str2.isEmpty()) return 1.0;
        if (str1.isEmpty() || str2.isEmpty()) return 0.0;

        int lcsLength = getLongestCommonSubsequence(str1, str2);
        int maxLength = Math.max(str1.length(), str2.length());

        return (double) lcsLength / maxLength;
    }

    /**
     * Calculates longest common subsequence using dynamic programming
     */
    public static int getLongestCommonSubsequence(String str1, String str2) {
        int[][] dp = new int[str1.length() + 1][str2.length() + 1];

        for (int i = 1; i <= str1.length(); i++) {
            for (int j = 1; j <= str2.length(); j++) {
                if (str1.charAt(i - 1) == str2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                }
            }
        }

        return dp[str1.length()][str2.length()];
    }

    /**
     * Sequence matching using longest common substring approach
     */
    public static List<int[]> findMatchingSequences(String text1, String text2, int minLength) {
        List<int[]> matches = new ArrayList<>();
        
        if (text1.length() < minLength || text2.length() < minLength) {
            return matches;
        }

        for (int i = 0; i <= text1.length() - minLength; i++) {
            String substring = text1.substring(i, i + minLength);
            int index = text2.indexOf(substring);
            if (index != -1) {
                matches.add(new int[]{i, index, minLength});
            }
        }

        return matches;
    }

    // ==================== Helper Methods ====================

    private static Map<String, Integer> getTermFrequency(String text) {
        String[] words = text.toLowerCase().split("[^a-z0-9]+");
        Map<String, Integer> frequency = new HashMap<>();

        for (String word : words) {
            if (!word.isEmpty() && word.length() > 2) {
                frequency.put(word, frequency.getOrDefault(word, 0) + 1);
            }
        }

        return frequency;
    }

    private static double getCosineSimilarity(Map<String, Integer> vector1, Map<String, Integer> vector2) {
        Set<String> allTerms = new HashSet<>();
        allTerms.addAll(vector1.keySet());
        allTerms.addAll(vector2.keySet());

        double dotProduct = 0.0;
        double magnitude1 = 0.0;
        double magnitude2 = 0.0;

        for (String term : allTerms) {
            int freq1 = vector1.getOrDefault(term, 0);
            int freq2 = vector2.getOrDefault(term, 0);

            dotProduct += freq1 * freq2;
            magnitude1 += freq1 * freq1;
            magnitude2 += freq2 * freq2;
        }

        magnitude1 = Math.sqrt(magnitude1);
        magnitude2 = Math.sqrt(magnitude2);

        if (magnitude1 == 0 || magnitude2 == 0) {
            return 0.0;
        }

        return dotProduct / (magnitude1 * magnitude2);
    }

    private static Set<String> getNgrams(String text, int n) {
        Set<String> ngrams = new HashSet<>();
        String cleaned = text.toLowerCase().replaceAll("[^a-z0-9\\s]", "");

        for (int i = 0; i <= cleaned.length() - n; i++) {
            ngrams.add(cleaned.substring(i, i + n));
        }

        return ngrams;
    }

    private static double getJaccardSimilarity(Set<String> set1, Set<String> set2) {
        if (set1.isEmpty() && set2.isEmpty()) return 1.0;
        if (set1.isEmpty() || set2.isEmpty()) return 0.0;

        Set<String> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);

        Set<String> union = new HashSet<>(set1);
        union.addAll(set2);

        return (double) intersection.size() / union.size();
    }
}
