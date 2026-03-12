package com.plagiarism.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the result of plagiarism comparison between two documents
 */
public class ComparisonResult {
    private String id;
    private Document sourceDocument;
    private Document targetDocument;
    private double overallSimilarity;
    private double cossineSimilarity;
    private double levenshteinSimilarity;
    private double ngramSimilarity;
    private double semanticSimilarity;
    private double finalScore;
    private List<SuspiciousSegment> suspiciousSegments;
    private LocalDateTime analyzedDate;
    private long analysisTimeMs;
    private String riskLevel; // LOW, MEDIUM, HIGH, CRITICAL

    public ComparisonResult(String id, Document sourceDocument, Document targetDocument) {
        this.id = id;
        this.sourceDocument = sourceDocument;
        this.targetDocument = targetDocument;
        this.suspiciousSegments = new ArrayList<>();
        this.analyzedDate = LocalDateTime.now();
        this.riskLevel = "LOW";
    }

    public void calculateFinalScore() {
        // Weighted average of all similarity metrics
        this.finalScore = (cossineSimilarity * 0.35 + 
                          levenshteinSimilarity * 0.25 + 
                          ngramSimilarity * 0.25 + 
                          semanticSimilarity * 0.15);
        
        // Determine risk level
        if (finalScore >= 0.8) {
            this.riskLevel = "CRITICAL";
        } else if (finalScore >= 0.6) {
            this.riskLevel = "HIGH";
        } else if (finalScore >= 0.4) {
            this.riskLevel = "MEDIUM";
        } else {
            this.riskLevel = "LOW";
        }
    }

    public void addSuspiciousSegment(SuspiciousSegment segment) {
        this.suspiciousSegments.add(segment);
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Document getSourceDocument() { return sourceDocument; }
    public void setSourceDocument(Document sourceDocument) { this.sourceDocument = sourceDocument; }

    public Document getTargetDocument() { return targetDocument; }
    public void setTargetDocument(Document targetDocument) { this.targetDocument = targetDocument; }

    public double getOverallSimilarity() { return overallSimilarity; }
    public void setOverallSimilarity(double overallSimilarity) { this.overallSimilarity = overallSimilarity; }

    public double getCossineSimilarity() { return cossineSimilarity; }
    public void setCossineSimilarity(double cossineSimilarity) { this.cossineSimilarity = cossineSimilarity; }

    public double getLevenshteinSimilarity() { return levenshteinSimilarity; }
    public void setLevenshteinSimilarity(double levenshteinSimilarity) { this.levenshteinSimilarity = levenshteinSimilarity; }

    public double getNgramSimilarity() { return ngramSimilarity; }
    public void setNgramSimilarity(double ngramSimilarity) { this.ngramSimilarity = ngramSimilarity; }

    public double getSemanticSimilarity() { return semanticSimilarity; }
    public void setSemanticSimilarity(double semanticSimilarity) { this.semanticSimilarity = semanticSimilarity; }

    public double getFinalScore() { return finalScore; }
    public void setFinalScore(double finalScore) { this.finalScore = finalScore; }

    public List<SuspiciousSegment> getSuspiciousSegments() { return suspiciousSegments; }
    public void setSuspiciousSegments(List<SuspiciousSegment> suspiciousSegments) { 
        this.suspiciousSegments = suspiciousSegments; 
    }

    public LocalDateTime getAnalyzedDate() { return analyzedDate; }
    public void setAnalyzedDate(LocalDateTime analyzedDate) { this.analyzedDate = analyzedDate; }

    public long getAnalysisTimeMs() { return analysisTimeMs; }
    public void setAnalysisTimeMs(long analysisTimeMs) { this.analysisTimeMs = analysisTimeMs; }

    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }

    @Override
    public String toString() {
        return "ComparisonResult{" +
                "id='" + id + '\'' +
                ", finalScore=" + String.format("%.2f%%", finalScore * 100) +
                ", riskLevel='" + riskLevel + '\'' +
                ", suspiciousSegments=" + suspiciousSegments.size() +
                '}';
    }
}
