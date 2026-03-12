package com.plagiarism.models;

/**
 * Represents a segment of text flagged as suspicious/plagiarized
 */
public class SuspiciousSegment {
    private int startIndex;
    private int endIndex;
    private String segmentText;
    private double similarityScore;
    private String matchedSource;
    private int matchStartIndex;
    private int matchEndIndex;

    public SuspiciousSegment(int startIndex, int endIndex, String segmentText, 
                            double similarityScore, String matchedSource) {
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.segmentText = segmentText;
        this.similarityScore = similarityScore;
        this.matchedSource = matchedSource;
    }

    // Getters and setters
    public int getStartIndex() { return startIndex; }
    public void setStartIndex(int startIndex) { this.startIndex = startIndex; }

    public int getEndIndex() { return endIndex; }
    public void setEndIndex(int endIndex) { this.endIndex = endIndex; }

    public String getSegmentText() { return segmentText; }
    public void setSegmentText(String segmentText) { this.segmentText = segmentText; }

    public double getSimilarityScore() { return similarityScore; }
    public void setSimilarityScore(double similarityScore) { this.similarityScore = similarityScore; }

    public String getMatchedSource() { return matchedSource; }
    public void setMatchedSource(String matchedSource) { this.matchedSource = matchedSource; }

    public int getMatchStartIndex() { return matchStartIndex; }
    public void setMatchStartIndex(int matchStartIndex) { this.matchStartIndex = matchStartIndex; }

    public int getMatchEndIndex() { return matchEndIndex; }
    public void setMatchEndIndex(int matchEndIndex) { this.matchEndIndex = matchEndIndex; }

    @Override
    public String toString() {
        return "SuspiciousSegment{" +
                "startIndex=" + startIndex +
                ", endIndex=" + endIndex +
                ", similarity=" + String.format("%.2f%%", similarityScore * 100) +
                ", source='" + matchedSource + '\'' +
                '}';
    }
}
