package com.plagiarism.web;

import com.plagiarism.engine.PlagiarismAnalyzer;
import com.plagiarism.models.ComparisonResult;
import com.plagiarism.models.Document;
import com.plagiarism.models.SuspiciousSegment;
import com.plagiarism.utils.ReportGenerator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * REST controller for plagiarism detection API
 */
@RestController
@RequestMapping("/api")
public class PlagiarismController {

    private final PlagiarismAnalyzer analyzer = new PlagiarismAnalyzer();

    /**
     * Analyze two texts for plagiarism
     */
    @PostMapping("/analyze")
    public ResponseEntity<Map<String, Object>> analyzeTexts(@RequestBody Map<String, String> request) {
        String sourceText = request.get("sourceText");
        String targetText = request.get("targetText");
        String sourceName = request.getOrDefault("sourceName", "Source Document");
        String targetName = request.getOrDefault("targetName", "Target Document");

        if (sourceText == null || sourceText.trim().isEmpty() ||
                targetText == null || targetText.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Both source and target texts are required"));
        }

        Document sourceDoc = new Document(UUID.randomUUID().toString(), sourceName, sourceText, "");
        Document targetDoc = new Document(UUID.randomUUID().toString(), targetName, targetText, "");
        sourceDoc.tokenize();
        targetDoc.tokenize();

        ComparisonResult result = analyzer.analyze(sourceDoc, targetDoc);
        return ResponseEntity.ok(buildResultMap(result));
    }

    /**
     * Analyze two uploaded files for plagiarism
     */
    @PostMapping("/analyze-files")
    public ResponseEntity<Map<String, Object>> analyzeFiles(
            @RequestParam("sourceFile") MultipartFile sourceFile,
            @RequestParam("targetFile") MultipartFile targetFile) throws IOException {

        if (sourceFile.isEmpty() || targetFile.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Both files are required"));
        }

        String sourceText = new String(sourceFile.getBytes(), StandardCharsets.UTF_8);
        String targetText = new String(targetFile.getBytes(), StandardCharsets.UTF_8);

        Document sourceDoc = new Document(UUID.randomUUID().toString(),
                sourceFile.getOriginalFilename(), sourceText, "");
        Document targetDoc = new Document(UUID.randomUUID().toString(),
                targetFile.getOriginalFilename(), targetText, "");
        sourceDoc.tokenize();
        targetDoc.tokenize();

        ComparisonResult result = analyzer.analyze(sourceDoc, targetDoc);
        return ResponseEntity.ok(buildResultMap(result));
    }

    /**
     * Generate HTML report
     */
    @PostMapping("/report")
    public ResponseEntity<String> generateReport(@RequestBody Map<String, String> request) {
        String sourceText = request.get("sourceText");
        String targetText = request.get("targetText");

        Document sourceDoc = new Document(UUID.randomUUID().toString(), "Source", sourceText, "");
        Document targetDoc = new Document(UUID.randomUUID().toString(), "Target", targetText, "");
        sourceDoc.tokenize();
        targetDoc.tokenize();

        ComparisonResult result = analyzer.analyze(sourceDoc, targetDoc);
        String html = ReportGenerator.generateHtmlReport(result);
        return ResponseEntity.ok(html);
    }

    private Map<String, Object> buildResultMap(ComparisonResult result) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("finalScore", result.getFinalScore());
        response.put("riskLevel", result.getRiskLevel());
        response.put("cosineSimilarity", result.getCossineSimilarity());
        response.put("levenshteinSimilarity", result.getLevenshteinSimilarity());
        response.put("ngramSimilarity", result.getNgramSimilarity());
        response.put("semanticSimilarity", result.getSemanticSimilarity());
        response.put("analysisTimeMs", result.getAnalysisTimeMs());
        response.put("sourceWordCount", result.getSourceDocument().getWordCount());
        response.put("targetWordCount", result.getTargetDocument().getWordCount());

        List<Map<String, Object>> segments = new ArrayList<>();
        for (SuspiciousSegment seg : result.getSuspiciousSegments()) {
            Map<String, Object> segMap = new LinkedHashMap<>();
            segMap.put("text", seg.getSegmentText().substring(0, Math.min(150, seg.getSegmentText().length())));
            segMap.put("similarity", seg.getSimilarityScore());
            segMap.put("source", seg.getMatchedSource());
            segments.add(segMap);
        }
        response.put("suspiciousSegments", segments);

        return response;
    }
}
