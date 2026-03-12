package com.plagiarism.engine;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for SimilarityCalculator
 */
public class SimilarityCalculatorTest {

    @Before
    public void setUp() {
        // Setup test data if needed
    }

    @Test
    public void testCosineSimilarityIdentical() {
        String text1 = "the quick brown fox jumps over the lazy dog";
        String text2 = "the quick brown fox jumps over the lazy dog";

        double similarity = SimilarityCalculator.calculateCosineSimilarity(text1, text2);
        assertEquals("Identical texts should have perfect similarity", 1.0, similarity, 0.01);
    }

    @Test
    public void testCosineSimilarityDifferent() {
        String text1 = "the quick brown fox";
        String text2 = "a completely different sentence";

        double similarity = SimilarityCalculator.calculateCosineSimilarity(text1, text2);
        assertTrue("Different texts should have low similarity", similarity < 0.5);
    }

    @Test
    public void testCosineSimilarityEmpty() {
        String text1 = "";
        String text2 = "some text";

        double similarity = SimilarityCalculator.calculateCosineSimilarity(text1, text2);
        assertEquals("Empty text should have zero similarity", 0.0, similarity, 0.01);
    }

    @Test
    public void testLevenshteinSimilarityIdentical() {
        String str1 = "hello";
        String str2 = "hello";

        double similarity = SimilarityCalculator.calculateLevenshteinSimilarity(str1, str2);
        assertEquals("Identical strings should have perfect similarity", 1.0, similarity, 0.01);
    }

    @Test
    public void testLevenshteinDistance() {
        String str1 = "kitten";
        String str2 = "sitting";

        int distance = SimilarityCalculator.calculateLevenshteinDistance(str1, str2);
        assertEquals("Distance between 'kitten' and 'sitting' should be 3", 3, distance);
    }

    @Test
    public void testLevenshteinSimilarityPartial() {
        String str1 = "hello";
        String str2 = "hallo";

        double similarity = SimilarityCalculator.calculateLevenshteinSimilarity(str1, str2);
        assertTrue("Similar strings should have high similarity", similarity > 0.7);
    }

    @Test
    public void testNgramSimilarityIdentical() {
        String text1 = "the quick brown fox";
        String text2 = "the quick brown fox";

        double similarity = SimilarityCalculator.calculateNgramSimilarity(text1, text2);
        assertEquals("Identical texts should have perfect n-gram similarity", 1.0, similarity, 0.01);
    }

    @Test
    public void testNgramSimilarityPartial() {
        String text1 = "the quick brown fox";
        String text2 = "the quick brown dog";

        double similarity = SimilarityCalculator.calculateNgramSimilarity(text1, text2);
        assertTrue("Similar texts should have moderate n-gram similarity", similarity > 0.5);
    }

    @Test
    public void testLCSSimilarityIdentical() {
        String str1 = "ABCDEFG";
        String str2 = "ABCDEFG";

        double similarity = SimilarityCalculator.calculateLCSSimilarity(str1, str2);
        assertEquals("Identical strings should have perfect LCS similarity", 1.0, similarity, 0.01);
    }

    @Test
    public void testLCSSimilarityPartial() {
        String str1 = "AGGTAB";
        String str2 = "GXTXAYB";

        double similarity = SimilarityCalculator.calculateLCSSimilarity(str1, str2);
        assertTrue("Partially matching strings should have moderate LCS similarity",
                similarity > 0.0 && similarity < 1.0);
    }

    @Test
    public void testLongestCommonSubsequence() {
        String str1 = "AGGTAB";
        String str2 = "GXTXAYB";

        int lcsLength = SimilarityCalculator.getLongestCommonSubsequence(str1, str2);
        assertEquals("LCS of 'AGGTAB' and 'GXTXAYB' should be 4", 4, lcsLength);
    }

    @Test
    public void testFindMatchingSequences() {
        String text1 = "the quick brown fox jumps";
        String text2 = "quick brown fox";

        var matches = SimilarityCalculator.findMatchingSequences(text1, text2, 5);
        assertTrue("Should find matching sequences", !matches.isEmpty());
    }

    @Test
    public void testSimilarityRange() {
        // All similarity metrics should return values between 0 and 1
        String text1 = "test text for similarity";
        String text2 = "another test case here";

        double cosine = SimilarityCalculator.calculateCosineSimilarity(text1, text2);
        double levenshtein = SimilarityCalculator.calculateLevenshteinSimilarity(text1, text2);
        double ngram = SimilarityCalculator.calculateNgramSimilarity(text1, text2);
        double lcs = SimilarityCalculator.calculateLCSSimilarity(text1, text2);

        assertTrue("Cosine similarity should be in range [0,1]", cosine >= 0 && cosine <= 1);
        assertTrue("Levenshtein similarity should be in range [0,1]", levenshtein >= 0 && levenshtein <= 1);
        assertTrue("N-gram similarity should be in range [0,1]", ngram >= 0 && ngram <= 1);
        assertTrue("LCS similarity should be in range [0,1]", lcs >= 0 && lcs <= 1);
    }
}
