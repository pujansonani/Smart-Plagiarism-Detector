package com.plagiarism.ml;

import com.plagiarism.models.Document;
import com.plagiarism.utils.TextPreprocessor;

import java.util.*;

/**
 * Machine Learning feature extractor for advanced plagiarism detection
 * Extracts stylometric and structural features for ML models
 */
public class FeatureExtractor {

    /**
     * Extracts comprehensive feature vector from a document
     */
    public static double[] extractFeatures(Document document) {
        List<Double> features = new ArrayList<>();

        String content = document.getContent();
        
        // Text statistics features (5 features)
        features.add((double) content.length());
        features.add((double) document.getWordCount());
        features.add(getAverageWordLength(content));
        features.add(getSentenceCount(content));
        features.add(getAverageSentenceLength(content));

        // Vocabulary richness (3 features)
        features.add(getTypeTokenRatio(content));
        features.add(getLexicalDiversity(content));
        features.add(getHapaxLegomenaRatio(content));

        // Punctuation features (4 features)
        features.add(getPunctuationDensity(content));
        features.add(getPeriodFrequency(content));
        features.add(getCommaFrequency(content));
        features.add(getExclamationFrequency(content));

        // Grammatical features (3 features)
        features.add(getCapitalLetterRatio(content));
        features.add(getDigitRatio(content));
        features.add(getSpecialCharacterRatio(content));

        // Syntactic features (3 features)
        features.add(getAverageWordLengthVariance(content));
        features.add(getReadabilityScore(content));
        features.add(getComplexityScore(content));

        // N-gram frequencies (4 features)
        features.add(getBigramRichness(content));
        features.add(getTrigramRichness(content));
        features.add(getUniqueWordPercentage(content));
        features.add(getStopwordDensity(content));

        return features.stream().mapToDouble(Double::doubleValue).toArray();
    }

    /**
     * Computes similarity between two feature vectors using Euclidean distance
     */
    public static double computeFeatureVectorSimilarity(double[] features1, double[] features2) {
        if (features1.length != features2.length) {
            throw new IllegalArgumentException("Feature vectors must have same length");
        }

        double sumSquaredDiff = 0;
        for (int i = 0; i < features1.length; i++) {
            double diff = features1[i] - features2[i];
            sumSquaredDiff += diff * diff;
        }

        double distance = Math.sqrt(sumSquaredDiff);
        
        // Convert distance to similarity (normalize to 0-1 range)
        // Using formula: similarity = 1 / (1 + distance)
        return 1.0 / (1.0 + distance / 100.0); // Scale factor for practical range
    }

    /**
     * Computes cosine similarity between feature vectors
     */
    public static double computeCosineSimilarity(double[] features1, double[] features2) {
        double dotProduct = 0;
        double magnitude1 = 0;
        double magnitude2 = 0;

        for (int i = 0; i < features1.length; i++) {
            dotProduct += features1[i] * features2[i];
            magnitude1 += features1[i] * features1[i];
            magnitude2 += features2[i] * features2[i];
        }

        if (magnitude1 == 0 || magnitude2 == 0) {
            return 0;
        }

        return dotProduct / (Math.sqrt(magnitude1) * Math.sqrt(magnitude2));
    }

    // ==================== Feature Calculation Methods ====================

    private static double getAverageWordLength(String text) {
        String[] words = TextPreprocessor.tokenize(text).toArray(new String[0]);
        if (words.length == 0) return 0;
        return Arrays.stream(words).mapToInt(String::length).average().orElse(0);
    }

    private static double getSentenceCount(String text) {
        return (double) TextPreprocessor.extractSentences(text).size();
    }

    private static double getAverageSentenceLength(String text) {
        List<String> sentences = TextPreprocessor.extractSentences(text);
        if (sentences.isEmpty()) return 0;
        return (double) text.split("\\s+").length / sentences.size();
    }

    private static double getTypeTokenRatio(String text) {
        Set<String> uniqueWords = new HashSet<>(TextPreprocessor.tokenize(text));
        List<String> allWords = TextPreprocessor.tokenize(text);
        if (allWords.isEmpty()) return 0;
        return (double) uniqueWords.size() / allWords.size();
    }

    private static double getLexicalDiversity(String text) {
        // Simpson's Index of Diversity
        List<String> words = TextPreprocessor.tokenize(text);
        if (words.size() < 2) return 0;

        Map<String, Integer> frequency = new HashMap<>();
        for (String word : words) {
            frequency.put(word, frequency.getOrDefault(word, 0) + 1);
        }

        double sumProbSquared = 0;
        for (int count : frequency.values()) {
            double probability = (double) count / words.size();
            sumProbSquared += probability * probability;
        }

        return 1 - sumProbSquared;
    }

    private static double getHapaxLegomenaRatio(String text) {
        // Ratio of words appearing exactly once
        List<String> words = TextPreprocessor.tokenize(text);
        if (words.isEmpty()) return 0;

        Map<String, Integer> frequency = new HashMap<>();
        for (String word : words) {
            frequency.put(word, frequency.getOrDefault(word, 0) + 1);
        }

        long hapaxCount = frequency.values().stream().filter(count -> count == 1).count();
        return (double) hapaxCount / words.size();
    }

    private static double getPunctuationDensity(String text) {
        long punctuationCount = text.replaceAll("[a-z0-9\\s]", "").length();
        return (double) punctuationCount / Math.max(1, text.length());
    }

    private static double getPeriodFrequency(String text) {
        return (double) text.replaceAll("[^.]", "").length() / Math.max(1, text.length());
    }

    private static double getCommaFrequency(String text) {
        return (double) text.replaceAll("[^,]", "").length() / Math.max(1, text.length());
    }

    private static double getExclamationFrequency(String text) {
        return (double) text.replaceAll("[^!]", "").length() / Math.max(1, text.length());
    }

    private static double getCapitalLetterRatio(String text) {
        long capitalCount = text.replaceAll("[^A-Z]", "").length();
        long letterCount = text.replaceAll("[^a-zA-Z]", "").length();
        return letterCount == 0 ? 0 : (double) capitalCount / letterCount;
    }

    private static double getDigitRatio(String text) {
        long digitCount = text.replaceAll("[^0-9]", "").length();
        return (double) digitCount / Math.max(1, text.length());
    }

    private static double getSpecialCharacterRatio(String text) {
        long specialCount = text.replaceAll("[a-z0-9A-Z\\s]", "").length();
        return (double) specialCount / Math.max(1, text.length());
    }

    private static double getAverageWordLengthVariance(String text) {
        List<String> words = TextPreprocessor.tokenize(text);
        if (words.size() < 2) return 0;

        double average = words.stream().mapToInt(String::length).average().orElse(0);
        double variance = words.stream()
                .mapToDouble(w -> Math.pow(w.length() - average, 2))
                .average()
                .orElse(0);

        return Math.sqrt(variance);
    }

    private static double getReadabilityScore(String text) {
        // Flesch Reading Ease approximation
        String[] sentences = text.split("[.!?]+");
        String[] words = text.split("\\s+");
        int syllables = text.replaceAll("[^aeiouAEIOU]", "").length();

        int sentenceCount = (int) Arrays.stream(sentences).filter(s -> !s.trim().isEmpty()).count();
        int wordCount = words.length;

        if (sentenceCount == 0 || wordCount == 0) return 0;

        return 206.835 - (1.015 * (wordCount / sentenceCount)) - (84.6 * (syllables / wordCount));
    }

    private static double getComplexityScore(String text) {
        List<String> words = TextPreprocessor.tokenize(text);
        if (words.isEmpty()) return 0;

        long complexWords = words.stream()
                .filter(w -> w.length() > 7)
                .count();

        return (double) complexWords / words.size();
    }

    private static double getBigramRichness(String text) {
        String cleaned = text.toLowerCase().replaceAll("[^a-z0-9\\s]", "");
        int bigramCount = Math.max(0, cleaned.length() - 1);
        
        Set<String> uniqueBigrams = new HashSet<>();
        for (int i = 0; i + 1 < cleaned.length(); i++) {
            uniqueBigrams.add(cleaned.substring(i, i + 2));
        }

        return bigramCount == 0 ? 0 : (double) uniqueBigrams.size() / bigramCount;
    }

    private static double getTrigramRichness(String text) {
        String cleaned = text.toLowerCase().replaceAll("[^a-z0-9\\s]", "");
        int trigramCount = Math.max(0, cleaned.length() - 2);
        
        Set<String> uniqueTrigrams = new HashSet<>();
        for (int i = 0; i + 2 < cleaned.length(); i++) {
            uniqueTrigrams.add(cleaned.substring(i, i + 3));
        }

        return trigramCount == 0 ? 0 : (double) uniqueTrigrams.size() / trigramCount;
    }

    private static double getUniqueWordPercentage(String text) {
        List<String> words = TextPreprocessor.tokenize(text);
        if (words.isEmpty()) return 0;

        Set<String> uniqueWords = new HashSet<>(words);
        return (double) uniqueWords.size() / words.size();
    }

    private static double getStopwordDensity(String text) {
        List<String> words = TextPreprocessor.tokenize(text);
        String preprocessed = TextPreprocessor.removeStopwords(text);
        List<String> nonStopwords = TextPreprocessor.tokenize(preprocessed);

        if (words.isEmpty()) return 0;
        return (double) (words.size() - nonStopwords.size()) / words.size();
    }
}
