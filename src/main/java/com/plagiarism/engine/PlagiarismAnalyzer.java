package com.plagiarism.engine;

import com.plagiarism.models.ComparisonResult;
import com.plagiarism.models.Document;
import com.plagiarism.models.SuspiciousSegment;
import com.plagiarism.utils.TextPreprocessor;

import java.util.*;

/**
 * Main plagiarism analysis engine coordinating all detection algorithms
 */
public class PlagiarismAnalyzer {

    private double cosineSimilarityWeight = 0.35;
    private double levenshteinWeight = 0.25;
    private double ngramWeight = 0.25;
    private double semanticWeight = 0.15;

    private static final int MIN_SEGMENT_LENGTH = 50; // Minimum text length for suspicion
    private static final int SLIDING_WINDOW_SIZE = 100; // Size of text chunks to analyze

    /**
     * Performs comprehensive plagiarism analysis between two documents
     */
    public ComparisonResult analyze(Document sourceDoc, Document targetDoc) {
        long startTime = System.currentTimeMillis();

        ComparisonResult result = new ComparisonResult(
                UUID.randomUUID().toString(),
                sourceDoc,
                targetDoc);

        // Handle empty documents
        String sourceText = sourceDoc.getContent();
        String targetText = targetDoc.getContent();
        if (sourceText == null || sourceText.trim().isEmpty() ||
                targetText == null || targetText.trim().isEmpty()) {
            result.calculateFinalScore();
            long endTime = System.currentTimeMillis();
            result.setAnalysisTimeMs(endTime - startTime);
            return result;
        }

        // Preprocess documents
        String sourceContent = TextPreprocessor.normalize(sourceText);
        String targetContent = TextPreprocessor.normalize(targetText);

        // Calculate similarity using multiple algorithms
        double cosineSim = SimilarityCalculator.calculateCosineSimilarity(sourceContent, targetContent);
        double levenshteinSim = SimilarityCalculator.calculateLevenshteinSimilarity(sourceContent, targetContent);
        double ngramSim = SimilarityCalculator.calculateNgramSimilarity(sourceContent, targetContent);
        double semanticSim = calculateSemanticSimilarity(sourceDoc, targetDoc);

        result.setCossineSimilarity(cosineSim);
        result.setLevenshteinSimilarity(levenshteinSim);
        result.setNgramSimilarity(ngramSim);
        result.setSemanticSimilarity(semanticSim);

        // Detect suspicious segments
        List<SuspiciousSegment> segments = detectSuspiciousSegments(sourceContent, targetContent);
        result.setSuspiciousSegments(segments);

        // Calculate final composite score
        result.calculateFinalScore();

        // Record analysis time
        long endTime = System.currentTimeMillis();
        result.setAnalysisTimeMs(endTime - startTime);

        return result;
    }

    /**
     * Detects suspicious/plagiarized segments using sliding window technique
     */
    private List<SuspiciousSegment> detectSuspiciousSegments(String sourceText, String targetText) {
        List<SuspiciousSegment> suspiciousSegments = new ArrayList<>();

        // Split into sentences for more precise detection
        List<String> sourceSentences = TextPreprocessor.extractSentences(sourceText);
        List<String> targetSentences = TextPreprocessor.extractSentences(targetText);

        for (int i = 0; i < sourceSentences.size(); i++) {
            String sourceSentence = sourceSentences.get(i);

            if (sourceSentence.length() < MIN_SEGMENT_LENGTH)
                continue;

            for (int j = 0; j < targetSentences.size(); j++) {
                String targetSentence = targetSentences.get(j);

                double similarity = SimilarityCalculator.calculateCosineSimilarity(sourceSentence, targetSentence);

                if (similarity >= 0.7) { // High confidence threshold
                    SuspiciousSegment segment = new SuspiciousSegment(
                            i,
                            i + sourceSentence.length(),
                            sourceSentence,
                            similarity,
                            "Segment " + j + " in target document");
                    segment.setMatchStartIndex(j);
                    segment.setMatchEndIndex(j + targetSentence.length());
                    suspiciousSegments.add(segment);
                }
            }
        }

        return suspiciousSegments;
    }

    /**
     * Calculates semantic similarity using writing style and structure
     */
    private double calculateSemanticSimilarity(Document doc1, Document doc2) {
        if (doc1.getContent() == null || doc1.getContent().trim().isEmpty() ||
                doc2.getContent() == null || doc2.getContent().trim().isEmpty()) {
            return 0.0;
        }

        Map<String, Double> features1 = TextPreprocessor.extractStyleFeatures(doc1.getContent());
        Map<String, Double> features2 = TextPreprocessor.extractStyleFeatures(doc2.getContent());

        double totalDifference = 0;
        double maxDifference = 0;

        for (String key : features1.keySet()) {
            if (features2.containsKey(key)) {
                double diff = Math.abs(features1.get(key) - features2.get(key));
                totalDifference += diff;
                maxDifference += 1.0;
            }
        }

        if (maxDifference == 0)
            return 0.0;

        return Math.max(0, 1.0 - (totalDifference / maxDifference));
    }

    /**
     * Batch process multiple documents against a reference document
     */
    public List<ComparisonResult> analyzeMultiple(Document referenceDoc, List<Document> documentsToAnalyze) {
        return documentsToAnalyze.stream()
                .map(doc -> this.analyze(referenceDoc, doc))
                .sorted((a, b) -> Double.compare(b.getFinalScore(), a.getFinalScore()))
                .toList();
    }

    /**
     * Performs cross-comparison among multiple documents (pairwise)
     */
    public List<ComparisonResult> performCrossComparison(List<Document> documents) {
        List<ComparisonResult> results = new ArrayList<>();

        for (int i = 0; i < documents.size(); i++) {
            for (int j = i + 1; j < documents.size(); j++) {
                results.add(this.analyze(documents.get(i), documents.get(j)));
            }
        }

        return results.stream()
                .sorted((a, b) -> Double.compare(b.getFinalScore(), a.getFinalScore()))
                .toList();
    }

    /**
     * Sets custom weights for similarity algorithms
     */
    public void setWeights(double cosine, double levenshtein, double ngram, double semantic) {
        double total = cosine + levenshtein + ngram + semantic;
        this.cosineSimilarityWeight = cosine / total;
        this.levenshteinWeight = levenshtein / total;
        this.ngramWeight = ngram / total;
        this.semanticWeight = semantic / total;
    }

    /**
     * Generates detailed analysis report
     */
    public String generateDetailedReport(ComparisonResult result) {
        StringBuilder report = new StringBuilder();

        report.append("========== PLAGIARISM DETECTION REPORT ==========\n");
        report.append(String.format("Analysis Date: %s\n", result.getAnalyzedDate()));
        report.append(String.format("Analysis Time: %d ms\n\n", result.getAnalysisTimeMs()));

        report.append("DOCUMENTS COMPARED:\n");
        report.append(String.format("  Source: %s (%d words)\n",
                result.getSourceDocument().getName(),
                result.getSourceDocument().getWordCount()));
        report.append(String.format("  Target: %s (%d words)\n\n",
                result.getTargetDocument().getName(),
                result.getTargetDocument().getWordCount()));

        report.append("SIMILARITY SCORES:\n");
        report.append(String.format("  Cosine Similarity: %.2f%%\n", result.getCossineSimilarity() * 100));
        report.append(String.format("  Levenshtein Similarity: %.2f%%\n", result.getLevenshteinSimilarity() * 100));
        report.append(String.format("  N-gram Similarity: %.2f%%\n", result.getNgramSimilarity() * 100));
        report.append(String.format("  Semantic Similarity: %.2f%%\n\n", result.getSemanticSimilarity() * 100));

        report.append(String.format("FINAL SCORE: %.2f%%\n", result.getFinalScore() * 100));
        report.append(String.format("RISK LEVEL: %s\n\n", result.getRiskLevel()));

        report.append(String.format("SUSPICIOUS SEGMENTS FOUND: %d\n", result.getSuspiciousSegments().size()));
        for (SuspiciousSegment segment : result.getSuspiciousSegments()) {
            report.append(String.format("  - \"%s\"\n    Similarity: %.2f%% | Source: %s\n",
                    segment.getSegmentText().substring(0, Math.min(50, segment.getSegmentText().length())),
                    segment.getSimilarityScore() * 100,
                    segment.getMatchedSource()));
        }

        return report.toString();
    }
}
