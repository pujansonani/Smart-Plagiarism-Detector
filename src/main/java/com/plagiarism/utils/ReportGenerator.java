package com.plagiarism.utils;

import com.plagiarism.models.ComparisonResult;
import com.plagiarism.models.SuspiciousSegment;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Generates detailed plagiarism reports in HTML format
 */
public class ReportGenerator {

    private static final String HTML_TEMPLATE = """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Plagiarism Detection Report</title>
                <style>
                    * {
                        margin: 0;
                        padding: 0;
                        box-sizing: border-box;
                    }
                    
                    body {
                        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                        padding: 20px;
                        min-height: 100vh;
                    }
                    
                    .container {
                        max-width: 1200px;
                        margin: 0 auto;
                        background: white;
                        padding: 40px;
                        border-radius: 10px;
                        box-shadow: 0 10px 40px rgba(0,0,0,0.15);
                    }
                    
                    header {
                        text-align: center;
                        margin-bottom: 30px;
                        border-bottom: 3px solid #667eea;
                        padding-bottom: 20px;
                    }
                    
                    h1 {
                        color: #333;
                        margin-bottom: 10px;
                        font-size: 2.5em;
                    }
                    
                    .report-meta {
                        color: #666;
                        font-size: 0.95em;
                    }
                    
                    .summary-grid {
                        display: grid;
                        grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
                        gap: 20px;
                        margin: 30px 0;
                    }
                    
                    .summary-card {
                        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                        color: white;
                        padding: 20px;
                        border-radius: 8px;
                        box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
                    }
                    
                    .summary-card h3 {
                        font-size: 0.85em;
                        opacity: 0.9;
                        margin-bottom: 10px;
                        text-transform: uppercase;
                    }
                    
                    .summary-card .value {
                        font-size: 2em;
                        font-weight: bold;
                    }
                    
                    .risk-critical { background: linear-gradient(135deg, #f93b1d 0%, #ea1e63 100%); }
                    .risk-high { background: linear-gradient(135deg, #fa7921 0%, #ffa500 100%); }
                    .risk-medium { background: linear-gradient(135deg, #ffd89b 0%, #19547b 100%); }
                    .risk-low { background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); }
                    
                    section {
                        margin: 30px 0;
                    }
                    
                    h2 {
                        color: #333;
                        margin-bottom: 15px;
                        padding-bottom: 10px;
                        border-left: 4px solid #667eea;
                        padding-left: 12px;
                    }
                    
                    table {
                        width: 100%;
                        border-collapse: collapse;
                        margin: 15px 0;
                    }
                    
                    th {
                        background: #f0f2f5;
                        color: #333;
                        padding: 15px;
                        text-align: left;
                        font-weight: 600;
                        border-bottom: 2px solid #ddd;
                    }
                    
                    td {
                        padding: 12px 15px;
                        border-bottom: 1px solid #eee;
                    }
                    
                    tr:hover {
                        background: #f9f9f9;
                    }
                    
                    .similarity-bar {
                        width: 100%;
                        height: 20px;
                        background: #eee;
                        border-radius: 10px;
                        overflow: hidden;
                        margin: 5px 0;
                    }
                    
                    .similarity-fill {
                        height: 100%;
                        background: linear-gradient(90deg, #667eea, #764ba2);
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        color: white;
                        font-size: 0.8em;
                        font-weight: bold;
                    }
                    
                    .segment {
                        background: #f5f5f5;
                        padding: 15px;
                        margin: 10px 0;
                        border-left: 4px solid #667eea;
                        border-radius: 4px;
                    }
                    
                    .segment-text {
                        color: #333;
                        font-size: 0.95em;
                        font-style: italic;
                        margin: 10px 0;
                        padding: 10px;
                        background: white;
                        border-radius: 4px;
                    }
                    
                    .metadata {
                        color: #666;
                        font-size: 0.9em;
                        margin: 5px 0;
                    }
                    
                    footer {
                        margin-top: 40px;
                        padding-top: 20px;
                        border-top: 1px solid #ddd;
                        text-align: center;
                        color: #999;
                        font-size: 0.9em;
                    }
                    
                    .action-buttons {
                        margin: 20px 0;
                        display: flex;
                        gap: 10px;
                        justify-content: center;
                        flex-wrap: wrap;
                    }
                    
                    button {
                        padding: 10px 20px;
                        border: none;
                        border-radius: 5px;
                        cursor: pointer;
                        font-size: 0.95em;
                        background: #667eea;
                        color: white;
                        transition: all 0.3s ease;
                    }
                    
                    button:hover {
                        background: #764ba2;
                        transform: translateY(-2px);
                        box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <header>
                        <h1>📋 Plagiarism Detection Report</h1>
                        <p class="report-meta">%s</p>
                    </header>
                    
                    %s
                    
                    <footer>
                        <p>Generated by Smart Plagiarism Detector v1.0.0</p>
                        <p>© 2024 B.Tech Project</p>
                    </footer>
                </div>
                
                <script>
                    function print() {
                        window.print();
                    }
                </script>
            </body>
            </html>
            """;

    /**
     * Generates HTML report for a comparison result
     */
    public static String generateHtmlReport(ComparisonResult result) {
        StringBuilder content = new StringBuilder();

        // Meta information
        String metaInfo = String.format("Analysis Date: %s | Analysis Time: %d ms",
                result.getAnalyzedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                result.getAnalysisTimeMs());

        // Summary section
        content.append("<section class=\"summary\">\n");
        content.append("    <h2>📊 Summary</h2>\n");
        content.append("    <div class=\"summary-grid\">\n");
        
        // Risk level card
        String riskClass = "risk-" + result.getRiskLevel().toLowerCase();
        content.append(String.format("        <div class=\"summary-card %s\">\n", riskClass));
        content.append("            <h3>Risk Level</h3>\n");
        content.append(String.format("            <div class=\"value\">%s</div>\n", result.getRiskLevel()));
        content.append("        </div>\n");

        // Final score card
        content.append("        <div class=\"summary-card\">\n");
        content.append("            <h3>Final Plagiarism Score</h3>\n");
        content.append(String.format("            <div class=\"value\">%.2f%%</div>\n", result.getFinalScore() * 100));
        content.append("        </div>\n");

        // Suspicious segments count
        content.append("        <div class=\"summary-card\">\n");
        content.append("            <h3>Suspicious Segments</h3>\n");
        content.append(String.format("            <div class=\"value\">%d</div>\n", result.getSuspiciousSegments().size()));
        content.append("        </div>\n");

        // Analysis time
        content.append("        <div class=\"summary-card\">\n");
        content.append("            <h3>Analysis Duration</h3>\n");
        content.append(String.format("            <div class=\"value\">%d ms</div>\n", result.getAnalysisTimeMs()));
        content.append("        </div>\n");
        
        content.append("    </div>\n");
        content.append("</section>\n");

        // Documents analyzed section
        content.append("<section class=\"documents\">\n");
        content.append("    <h2>📄 Documents Analyzed</h2>\n");
        content.append("    <table>\n");
        content.append("        <tr>\n");
        content.append("            <th>Document Type</th>\n");
        content.append("            <th>Name</th>\n");
        content.append("            <th>Word Count</th>\n");
        content.append("            <th>Upload Date</th>\n");
        content.append("        </tr>\n");
        
        content.append("        <tr>\n");
        content.append("            <td>Source</td>\n");
        content.append(String.format("            <td>%s</td>\n", result.getSourceDocument().getName()));
        content.append(String.format("            <td>%d</td>\n", result.getSourceDocument().getWordCount()));
        content.append(String.format("            <td>%s</td>\n", 
                result.getSourceDocument().getUploadedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
        content.append("        </tr>\n");
        
        content.append("        <tr>\n");
        content.append("            <td>Target</td>\n");
        content.append(String.format("            <td>%s</td>\n", result.getTargetDocument().getName()));
        content.append(String.format("            <td>%d</td>\n", result.getTargetDocument().getWordCount()));
        content.append(String.format("            <td>%s</td>\n", 
                result.getTargetDocument().getUploadedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
        content.append("        </tr>\n");
        
        content.append("    </table>\n");
        content.append("</section>\n");

        // Similarity metrics section
        content.append("<section class=\"metrics\">\n");
        content.append("    <h2>📈 Similarity Metrics</h2>\n");
        content.append("    <table>\n");
        content.append("        <tr>\n");
        content.append("            <th>Algorithm</th>\n");
        content.append("            <th>Similarity Score</th>\n");
        content.append("            <th>Visual</th>\n");
        content.append("        </tr>\n");
        
        addMetricRow(content, "Cosine Similarity", result.getCossineSimilarity());
        addMetricRow(content, "Levenshtein Similarity", result.getLevenshteinSimilarity());
        addMetricRow(content, "N-gram Similarity", result.getNgramSimilarity());
        addMetricRow(content, "Semantic Similarity", result.getSemanticSimilarity());
        addMetricRow(content, "FINAL SCORE (Weighted)", result.getFinalScore());
        
        content.append("    </table>\n");
        content.append("</section>\n");

        // Suspicious segments section
        if (!result.getSuspiciousSegments().isEmpty()) {
            content.append("<section class=\"segments\">\n");
            content.append(String.format("    <h2>⚠️ Suspicious Segments (%d found)</h2>\n", 
                    result.getSuspiciousSegments().size()));
            
            for (int i = 0; i < result.getSuspiciousSegments().size(); i++) {
                SuspiciousSegment segment = result.getSuspiciousSegments().get(i);
                content.append("    <div class=\"segment\">\n");
                content.append(String.format("        <p><strong>Segment #%d</strong></p>\n", i + 1));
                content.append(String.format("        <div class=\"segment-text\">%s</div>\n", 
                        escapeHtml(segment.getSegmentText().substring(0, 
                                Math.min(100, segment.getSegmentText().length()))) + "..."));
                content.append(String.format("        <div class=\"metadata\">Similarity: %.2f%%</div>\n", 
                        segment.getSimilarityScore() * 100));
                content.append(String.format("        <div class=\"metadata\">Match Found In: %s</div>\n", 
                        segment.getMatchedSource()));
                content.append("    </div>\n");
            }
            
            content.append("</section>\n");
        }

        // Conclusion section
        content.append("<section class=\"conclusion\">\n");
        content.append("    <h2>📌 Conclusion</h2>\n");
        if (result.getFinalScore() >= 0.8) {
            content.append("    <p style=\"color: #f93b1d; font-size: 1.1em;\">🚨 <strong>CRITICAL:</strong> Very high probability of plagiarism detected.</p>\n");
        } else if (result.getFinalScore() >= 0.6) {
            content.append("    <p style=\"color: #fa7921; font-size: 1.1em;\">⚠️ <strong>HIGH:</strong> Significant plagiarism indicators found. Further investigation recommended.</p>\n");
        } else if (result.getFinalScore() >= 0.4) {
            content.append("    <p style=\"color: #ffa500; font-size: 1.1em;\">📊 <strong>MEDIUM:</strong> Moderate similarity detected. Manual review suggested.</p>\n");
        } else {
            content.append("    <p style=\"color: #4facfe; font-size: 1.1em;\">✅ <strong>LOW:</strong> Document appears to be original.</p>\n");
        }
        content.append("</section>\n");

        return String.format(HTML_TEMPLATE, metaInfo, content.toString());
    }

    /**
     * Saves report to HTML file
     */
    public static void saveReportAsHtml(ComparisonResult result, String outputPath) throws IOException {
        String htmlContent = generateHtmlReport(result);
        Path path = Paths.get(outputPath);
        Files.createDirectories(path.getParent());
        Files.writeString(path, htmlContent);
    }

    /**
     * Saves report as plain text
     */
    public static void saveReportAsText(ComparisonResult result, String outputPath) throws IOException {
        StringBuilder report = new StringBuilder();

        report.append("================= PLAGIARISM DETECTION REPORT =================\n\n");
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
        report.append(String.format("  Cosine Similarity:      %.2f%%\n", result.getCossineSimilarity() * 100));
        report.append(String.format("  Levenshtein Similarity: %.2f%%\n", result.getLevenshteinSimilarity() * 100));
        report.append(String.format("  N-gram Similarity:      %.2f%%\n", result.getNgramSimilarity() * 100));
        report.append(String.format("  Semantic Similarity:    %.2f%%\n\n", result.getSemanticSimilarity() * 100));

        report.append(String.format("FINAL PLAGIARISM SCORE: %.2f%%\n", result.getFinalScore() * 100));
        report.append(String.format("RISK LEVEL: %s\n\n", result.getRiskLevel()));

        report.append(String.format("SUSPICIOUS SEGMENTS DETECTED: %d\n\n", result.getSuspiciousSegments().size()));
        for (int i = 0; i < result.getSuspiciousSegments().size(); i++) {
            SuspiciousSegment segment = result.getSuspiciousSegments().get(i);
            report.append(String.format("Segment #%d:\n", i + 1));
            report.append(String.format("  Text: \"%s\"\n", 
                    segment.getSegmentText().substring(0, Math.min(80, segment.getSegmentText().length()))));
            report.append(String.format("  Similarity: %.2f%%\n", segment.getSimilarityScore() * 100));
            report.append(String.format("  Match: %s\n\n", segment.getMatchedSource()));
        }

        report.append("===============================================================\n");

        Path path = Paths.get(outputPath);
        Files.createDirectories(path.getParent());
        Files.writeString(path, report.toString());
    }

    // ==================== Helper Methods ====================

    private static void addMetricRow(StringBuilder sb, String algorithmName, double similarity) {
        sb.append("        <tr>\n");
        sb.append(String.format("            <td><strong>%s</strong></td>\n", algorithmName));
        sb.append(String.format("            <td>%.2f%%</td>\n", similarity * 100));
        sb.append("            <td>\n");
        sb.append("                <div class=\"similarity-bar\">\n");
        sb.append(String.format("                    <div class=\"similarity-fill\" style=\"width: %.2f%%;\">%.2f%%</div>\n", 
                similarity * 100, similarity * 100));
        sb.append("                </div>\n");
        sb.append("            </td>\n");
        sb.append("        </tr>\n");
    }

    private static String escapeHtml(String text) {
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
