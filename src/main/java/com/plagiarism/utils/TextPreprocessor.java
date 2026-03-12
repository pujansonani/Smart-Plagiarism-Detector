package com.plagiarism.utils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Text preprocessing utilities for plagiarism detection
 */
public class TextPreprocessor {

    private static final Set<String> COMMON_STOPWORDS = new HashSet<>(Arrays.asList(
            "the", "a", "an", "and", "or", "but", "is", "are", "was", "were",
            "be", "been", "being", "have", "has", "had", "do", "does", "did",
            "can", "could", "would", "should", "may", "might", "must", "shall",
            "with", "from", "to", "of", "in", "on", "at", "by", "for", "as",
            "it", "this", "that", "these", "those", "i", "you", "he", "she",
            "we", "they", "what", "which", "who", "when", "where", "why", "how",
            "all", "each", "every", "both", "either", "neither", "such", "no",
            "not", "only", "own", "same", "so", "than", "too", "very"
    ));

    /**
     * Removes common stop words from text
     */
    public static String removeStopwords(String text) {
        String[] words = text.toLowerCase().split("[^a-z0-9]+");
        return Arrays.stream(words)
                .filter(word -> !word.isEmpty() && !COMMON_STOPWORDS.contains(word))
                .collect(Collectors.joining(" "));
    }

    /**
     * Normalizes text by converting to lowercase and removing punctuation
     */
    public static String normalize(String text) {
        return text.toLowerCase()
                .replaceAll("[^a-z0-9\\s]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    /**
     * Removes extra whitespace
     */
    public static String removeExtraWhitespace(String text) {
        return text.replaceAll("\\s+", " ").trim();
    }

    /**
     * Tokenizes text into words
     */
    public static List<String> tokenize(String text) {
        String normalized = normalize(text);
        String[] words = normalized.split("\\s+");
        return Arrays.asList(words);
    }

    /**
     * Extracts sentences from text
     */
    public static List<String> extractSentences(String text) {
        String[] sentences = text.split("[.!?]+");
        return Arrays.stream(sentences)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    /**
     * Extracts paragraphs from text
     */
    public static List<String> extractParagraphs(String text) {
        String[] paragraphs = text.split("\\n\\n+");
        return Arrays.stream(paragraphs)
                .map(String::trim)
                .filter(p -> !p.isEmpty())
                .collect(Collectors.toList());
    }

    /**
     * Lemmatizes common word forms (simple approach)
     */
    public static String lemmatize(String text) {
        return text
                .replaceAll("ing(?=\\s|$)", "")
                .replaceAll("ed(?=\\s|$)", "")
                .replaceAll("ies(?=\\s|$)", "ies");
    }

    /**
     * Extracts keywords using TF-IDF approach
     */
    public static List<String> extractKeywords(String text, int topN) {
        List<String> tokens = tokenize(removeStopwords(text));
        Map<String, Integer> frequency = new HashMap<>();

        for (String token : tokens) {
            frequency.put(token, frequency.getOrDefault(token, 0) + 1);
        }

        return frequency.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(topN)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * Calculates readability score (flesch kincaid grade)
     */
    public static double calculateReadabilityScore(String text) {
        String[] sentences = text.split("[.!?]+");
        String[] words = text.split("\\s+");
        String[] syllables = text.toLowerCase().replaceAll("[^aeiou]", "").split("");

        int sentenceCount = (int) Arrays.stream(sentences).filter(s -> !s.trim().isEmpty()).count();
        int wordCount = words.length;
        int syllableCount = syllables.length;

        if (sentenceCount == 0 || wordCount == 0) return 0.0;

        return 0.39 * (wordCount / sentenceCount) + 11.8 * (syllableCount / wordCount) - 15.59;
    }

    /**
     * Detects language (simple approach - looking for patterns)
     */
    public static String detectLanguage(String text) {
        // Simple heuristic: count common English words
        String[] commonEnglish = {"the", "is", "and", "to", "of", "a", "in", "that", "it", "for"};
        String normalized = normalize(text);
        long englishCount = Arrays.stream(commonEnglish)
                .filter(word -> normalized.contains(" " + word + " ") || 
                              normalized.startsWith(word + " ") ||
                              normalized.endsWith(" " + word))
                .count();

        return englishCount > 5 ? "ENGLISH" : "UNKNOWN";
    }

    /**
     * Compares writing style features
     */
    public static Map<String, Double> extractStyleFeatures(String text) {
        Map<String, Double> features = new HashMap<>();

        String[] words = text.split("\\s+");
        String[] sentences = text.split("[.!?]+");

        // Average word length
        double avgWordLength = Arrays.stream(words)
                .mapToInt(String::length)
                .average()
                .orElse(0.0);
        features.put("avgWordLength", avgWordLength);

        // Average sentence length
        double avgSentenceLength = (double) words.length / Math.max(1, sentences.length);
        features.put("avgSentenceLength", avgSentenceLength);

        // Vocabulary richness (type-token ratio)
        Set<String> uniqueWords = new HashSet<>(Arrays.asList(words));
        double typeTokenRatio = (double) uniqueWords.size() / Math.max(1, words.length);
        features.put("typeTokenRatio", typeTokenRatio);

        // Punctuation density
        long punctuationCount = text.replaceAll("[a-z0-9\\s]", "").length();
        features.put("punctuationDensity", (double) punctuationCount / Math.max(1, text.length()));

        return features;
    }
}
