package com.plagiarism.engine;

import com.plagiarism.models.ComparisonResult;
import com.plagiarism.models.Document;
import com.plagiarism.utils.TextPreprocessor;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for PlagiarismAnalyzer
 */
public class PlagiarismAnalyzerTest {

    private PlagiarismAnalyzer analyzer;

    @Before
    public void setUp() {
        analyzer = new PlagiarismAnalyzer();
    }

    @Test
    public void testAnalyzeIdenticalDocuments() {
        String content = "This is a test document for plagiarism detection. " +
                "It contains multiple sentences to verify the system works correctly.";

        Document doc1 = new Document("doc1", "Document 1", content, "");
        Document doc2 = new Document("doc2", "Document 2", content, "");
        doc1.tokenize();
        doc2.tokenize();

        ComparisonResult result = analyzer.analyze(doc1, doc2);

        assertNotNull("Result should not be null", result);
        assertTrue("Score should be high for identical documents", result.getFinalScore() > 0.95);
        assertEquals("Risk level should be CRITICAL", "CRITICAL", result.getRiskLevel());
    }

    @Test
    public void testAnalyzeCompleteDifferent() {
        String content1 = "The quick brown fox jumps over the lazy dog.";
        String content2 = "Mathematics is the study of numbers and equations.";

        Document doc1 = new Document("doc1", "Document 1", content1, "");
        Document doc2 = new Document("doc2", "Document 2", content2, "");
        doc1.tokenize();
        doc2.tokenize();

        ComparisonResult result = analyzer.analyze(doc1, doc2);

        assertNotNull("Result should not be null", result);
        assertTrue("Score should be low for different documents", result.getFinalScore() < 0.3);
        assertEquals("Risk level should be LOW", "LOW", result.getRiskLevel());
    }

    @Test
    public void testAnalyzePartialPlagiarism() {
        String original = "The artificial intelligence revolution is changing technology rapidly. " +
                "Machine learning algorithms enable computers to learn from data. " +
                "Deep neural networks process complex patterns effectively.";

        String paraphrased = "AI revolution is transforming tech quickly. " +
                "ML algorithms let computers learn from information. " +
                "Neural networks handle complicated patterns well.";

        Document doc1 = new Document("doc1", "Original", original, "");
        Document doc2 = new Document("doc2", "Paraphrased", paraphrased, "");
        doc1.tokenize();
        doc2.tokenize();

        ComparisonResult result = analyzer.analyze(doc1, doc2);

        assertNotNull("Result should not be null", result);
        assertTrue("Score should be moderate for paraphrased text",
                result.getFinalScore() >= 0.4 && result.getFinalScore() <= 0.8);
        assertTrue("Risk level should be at least MEDIUM",
                result.getRiskLevel().equals("MEDIUM") || result.getRiskLevel().equals("HIGH"));
    }

    @Test
    public void testResultMetrics() {
        String content1 = "Artificial intelligence and machine learning are transforming industries globally.";
        String content2 = "AI and ML technologies are revolutionizing business around the world.";

        Document doc1 = new Document("doc1", "Document 1", content1, "");
        Document doc2 = new Document("doc2", "Document 2", content2, "");
        doc1.tokenize();
        doc2.tokenize();

        ComparisonResult result = analyzer.analyze(doc1, doc2);

        // Verify all metrics are in valid range
        assertTrue("Cosine similarity should be 0-1",
                result.getCossineSimilarity() >= 0 && result.getCossineSimilarity() <= 1);
        assertTrue("Levenshtein similarity should be 0-1",
                result.getLevenshteinSimilarity() >= 0 && result.getLevenshteinSimilarity() <= 1);
        assertTrue("N-gram similarity should be 0-1",
                result.getNgramSimilarity() >= 0 && result.getNgramSimilarity() <= 1);
        assertTrue("Semantic similarity should be 0-1",
                result.getSemanticSimilarity() >= 0 && result.getSemanticSimilarity() <= 1);
        assertTrue("Final score should be 0-1",
                result.getFinalScore() >= 0 && result.getFinalScore() <= 1);
    }

    @Test
    public void testAnalysisProducesDetectedSegments() {
        String original = "The quick brown fox jumps over the lazy dog. " +
                "This is a famous pangram used in typography. " +
                "It contains all letters of the English alphabet.";

        String plagiarized = "The quick brown fox jumps over the lazy dog. " +
                "This is a well-known pangram used in type design.";

        Document doc1 = new Document("doc1", "Original", original, "");
        Document doc2 = new Document("doc2", "Suspicious", plagiarized, "");
        doc1.tokenize();
        doc2.tokenize();

        ComparisonResult result = analyzer.analyze(doc1, doc2);

        assertNotNull("Result should have suspicious segments", result.getSuspiciousSegments());
        assertTrue("Should detect at least one suspicious segment", result.getSuspiciousSegments().size() > 0);
    }

    @Test
    public void testAnalysisTimeMeasurement() {
        String content1 = "This is a standard document for testing plagiarism detection systems.";
        String content2 = "This is a typical file for examining plagiarism analysis tools.";

        Document doc1 = new Document("doc1", "Document 1", content1, "");
        Document doc2 = new Document("doc2", "Document 2", content2, "");
        doc1.tokenize();
        doc2.tokenize();

        ComparisonResult result = analyzer.analyze(doc1, doc2);

        assertTrue("Analysis should complete within reasonable time", result.getAnalysisTimeMs() < 5000);
        assertTrue("Analysis time should be measured", result.getAnalysisTimeMs() >= 0);
    }

    @Test
    public void testWeightCustomization() {
        analyzer.setWeights(0.5, 0.2, 0.2, 0.1);

        String content1 = "Machine learning and artificial intelligence.";
        String content2 = "AI and machine learning technologies.";

        Document doc1 = new Document("doc1", "Doc1", content1, "");
        Document doc2 = new Document("doc2", "Doc2", content2, "");
        doc1.tokenize();
        doc2.tokenize();

        ComparisonResult result = analyzer.analyze(doc1, doc2);

        assertNotNull("Result should be generated with custom weights", result);
        assertTrue("Final score should reflect analysis", result.getFinalScore() >= 0 && result.getFinalScore() <= 1);
    }

    @Test
    public void testReportGeneration() {
        String content1 = "Testing report generation for plagiarism detection.";
        String content2 = "Examining report creation for plagiarism detector.";

        Document doc1 = new Document("doc1", "Original", content1, "");
        Document doc2 = new Document("doc2", "Tested", content2, "");
        doc1.tokenize();
        doc2.tokenize();

        ComparisonResult result = analyzer.analyze(doc1, doc2);
        String report = analyzer.generateDetailedReport(result);

        assertNotNull("Report should be generated", report);
        assertTrue("Report should contain score", report.contains("%"));
        assertTrue("Report should contain risk level", report.contains("RISK LEVEL"));
    }

    @Test
    public void testEmptyDocuments() {
        Document doc1 = new Document("doc1", "Empty1", "", "");
        Document doc2 = new Document("doc2", "Empty2", "", "");

        ComparisonResult result = analyzer.analyze(doc1, doc2);

        assertNotNull("Result should handle empty documents", result);
        // Score should be 0 or close to 0 for empty documents
        assertTrue("Empty documents should have minimal similarity", result.getFinalScore() < 0.1);
    }

    @Test
    public void testDocumentMetadata() {
        String content = "Sample content with multiple words for testing purposes.";
        Document doc = new Document("id123", "Test.txt", content, "/path/to/file");

        assertEquals("ID should match", "id123", doc.getId());
        assertEquals("Name should match", "Test.txt", doc.getName());
        assertEquals("Path should match", "/path/to/file", doc.getFilePath());
        assertTrue("Word count should be calculated", doc.getWordCount() > 0);
    }
}
