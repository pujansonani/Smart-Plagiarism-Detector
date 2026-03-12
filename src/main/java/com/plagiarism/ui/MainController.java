package com.plagiarism.ui;

import com.plagiarism.engine.PlagiarismAnalyzer;
import com.plagiarism.models.ComparisonResult;
import com.plagiarism.models.Document;
import com.plagiarism.utils.FileProcessor;
import com.plagiarism.utils.ReportGenerator;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Main controller for plagiarism detector GUI
 */
public class MainController {
    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    // UI Components
    public TextArea sourceDocumentText;
    public TextArea targetDocumentText;
    public Label sourceDocNameLabel;
    public Label targetDocNameLabel;
    public Button analyzeButton;
    public Button loadSourceButton;
    public Button loadTargetButton;
    public Label analysisResultsLabel;
    public VBox resultsPanel;
    public TabPane resultsTabPane;

    // Data
    private Document sourceDocument;
    private Document targetDocument;
    private ComparisonResult currentResult;
    private PlagiarismAnalyzer analyzer;

    private Stage stage;

    public MainController() {
        this.analyzer = new PlagiarismAnalyzer();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Initialize the controller
     */
    public void initialize() {
        logger.info("Initializing MainController");
        
        if (analyzeButton != null) {
            analyzeButton.setOnAction(event -> performAnalysis());
        }
        if (loadSourceButton != null) {
            loadSourceButton.setOnAction(event -> loadSourceDocument());
        }
        if (loadTargetButton != null) {
            loadTargetButton.setOnAction(event -> loadTargetDocument());
        }

        updateAnalyzeButtonState();
    }

    /**
     * Loads source document from file
     */
    private void loadSourceDocument() {
        File file = chooseFile();
        if (file != null) {
            new Thread(() -> {
                try {
                    sourceDocument = FileProcessor.loadDocument(file.getAbsolutePath());
                    Platform.runLater(() -> {
                        if (sourceDocumentText != null) {
                            sourceDocumentText.setText(sourceDocument.getContent());
                            sourceDocNameLabel.setText("📄 " + sourceDocument.getName() + 
                                    " (" + sourceDocument.getWordCount() + " words)");
                        }
                        updateAnalyzeButtonState();
                    });
                    logger.info("Source document loaded: {}", file.getName());
                } catch (IOException e) {
                    Platform.runLater(() -> showError("Error loading source document", e.getMessage()));
                    logger.error("Error loading source document", e);
                }
            }).start();
        }
    }

    /**
     * Loads target document from file
     */
    private void loadTargetDocument() {
        File file = chooseFile();
        if (file != null) {
            new Thread(() -> {
                try {
                    targetDocument = FileProcessor.loadDocument(file.getAbsolutePath());
                    Platform.runLater(() -> {
                        if (targetDocumentText != null) {
                            targetDocumentText.setText(targetDocument.getContent());
                            targetDocNameLabel.setText("📄 " + targetDocument.getName() + 
                                    " (" + targetDocument.getWordCount() + " words)");
                        }
                        updateAnalyzeButtonState();
                    });
                    logger.info("Target document loaded: {}", file.getName());
                } catch (IOException e) {
                    Platform.runLater(() -> showError("Error loading target document", e.getMessage()));
                    logger.error("Error loading target document", e);
                }
            }).start();
        }
    }

    /**
     * Performs plagiarism analysis
     */
    private void performAnalysis() {
        if (sourceDocument == null || targetDocument == null) {
            showError("Missing Documents", "Please load both source and target documents");
            return;
        }

        analyzeButton.setDisable(true);
        analyzeButton.setText("⏳ Analyzing...");

        new Thread(() -> {
            try {
                logger.info("Starting plagiarism analysis");
                currentResult = analyzer.analyze(sourceDocument, targetDocument);
                
                Platform.runLater(() -> {
                    displayResults(currentResult);
                    analyzeButton.setDisable(false);
                    analyzeButton.setText("🔍 Analyze Documents");
                    logger.info("Analysis completed. Final Score: {}", currentResult.getFinalScore());
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    showError("Analysis Error", e.getMessage());
                    analyzeButton.setDisable(false);
                    analyzeButton.setText("🔍 Analyze Documents");
                });
                logger.error("Analysis failed", e);
            }
        }).start();
    }

    /**
     * Displays analysis results
     */
    private void displayResults(ComparisonResult result) {
        // Update results label
        if (analysisResultsLabel != null) {
            String riskColor = getRiskColor(result.getRiskLevel());
            analysisResultsLabel.setText(String.format(
                    "Plagiarism Score: %.2f%% | Risk Level: %s | Time: %d ms",
                    result.getFinalScore() * 100,
                    result.getRiskLevel(),
                    result.getAnalysisTimeMs()
            ));
            analysisResultsLabel.setStyle("-fx-text-fill: " + riskColor + ";");
        }

        // Clear and populate results panel
        if (resultsPanel != null) {
            resultsPanel.getChildren().clear();

            // Summary section
            ScrollPane summaryPane = createSummaryPane(result);
            resultsPanel.getChildren().add(summaryPane);

            // Metrics section
            ScrollPane metricsPane = createMetricsPane(result);
            resultsPanel.getChildren().add(metricsPane);

            // Suspicious segments section
            if (!result.getSuspiciousSegments().isEmpty()) {
                ScrollPane segmentsPane = createSegmentsPane(result);
                resultsPanel.getChildren().add(segmentsPane);
            }

            // Action buttons
            HBox actionBox = createActionButtons(result);
            resultsPanel.getChildren().add(actionBox);
        }
    }

    /**
     * Creates summary pane
     */
    private ScrollPane createSummaryPane(ComparisonResult result) {
        VBox vbox = new VBox(10);
        vbox.setStyle("-fx-padding: 15;");

        Label titleLabel = new Label("📊 Analysis Summary");
        titleLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");
        vbox.getChildren().add(titleLabel);

        // Risk level
        Label riskLabel = new Label("Risk Level: " + result.getRiskLevel());
        riskLabel.setStyle("-fx-font-size: 14; -fx-text-fill: " + getRiskColor(result.getRiskLevel()) + ";");
        vbox.getChildren().add(riskLabel);

        // Final score
        Label scoreLabel = new Label(String.format("Final Plagiarism Score: %.2f%%", result.getFinalScore() * 100));
        scoreLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");
        vbox.getChildren().add(scoreLabel);

        // Analysis time
        Label timeLabel = new Label("Analysis Time: " + result.getAnalysisTimeMs() + " ms");
        vbox.getChildren().add(timeLabel);

        // Suspicious segments count
        Label segLabel = new Label("Suspicious Segments Found: " + result.getSuspiciousSegments().size());
        vbox.getChildren().add(segLabel);

        ScrollPane pane = new ScrollPane(vbox);
        pane.setStyle("-fx-border-color: #ddd; -fx-border-radius: 5;");
        pane.setPrefHeight(150);
        return pane;
    }

    /**
     * Creates metrics pane
     */
    private ScrollPane createMetricsPane(ComparisonResult result) {
        VBox vbox = new VBox(10);
        vbox.setStyle("-fx-padding: 15;");

        Label titleLabel = new Label("📈 Similarity Metrics");
        titleLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");
        vbox.getChildren().add(titleLabel);

        addMetricBar(vbox, "Cosine Similarity", result.getCossineSimilarity());
        addMetricBar(vbox, "Levenshtein Similarity", result.getLevenshteinSimilarity());
        addMetricBar(vbox, "N-gram Similarity", result.getNgramSimilarity());
        addMetricBar(vbox, "Semantic Similarity", result.getSemanticSimilarity());

        ScrollPane pane = new ScrollPane(vbox);
        pane.setStyle("-fx-border-color: #ddd; -fx-border-radius: 5;");
        pane.setPrefHeight(200);
        return pane;
    }

    /**
     * Creates suspicious segments pane
     */
    private ScrollPane createSegmentsPane(ComparisonResult result) {
        VBox vbox = new VBox(10);
        vbox.setStyle("-fx-padding: 15;");

        Label titleLabel = new Label("⚠️ Suspicious Segments");
        titleLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");
        vbox.getChildren().add(titleLabel);

        result.getSuspiciousSegments().stream()
                .limit(5) // Show top 5
                .forEach(segment -> {
                    VBox segmentBox = new VBox(5);
                    segmentBox.setStyle("-fx-border-color: #ffcccc; -fx-border-radius: 5; -fx-padding: 10;");

                    String segmentPreview = segment.getSegmentText().length() > 100 
                            ? segment.getSegmentText().substring(0, 100) + "..." 
                            : segment.getSegmentText();
                    Label segmentText = new Label(segmentPreview);
                    segmentText.setWrapText(true);
                    segmentBox.getChildren().add(segmentText);

                    Label similarity = new Label(String.format("Similarity: %.2f%%", segment.getSimilarityScore() * 100));
                    similarity.setStyle("-fx-font-size: 12; -fx-text-fill: #666;");
                    segmentBox.getChildren().add(similarity);

                    vbox.getChildren().add(segmentBox);
                });

        ScrollPane pane = new ScrollPane(vbox);
        pane.setStyle("-fx-border-color: #ddd; -fx-border-radius: 5;");
        pane.setPrefHeight(250);
        return pane;
    }

    /**
     * Creates action buttons
     */
    private HBox createActionButtons(ComparisonResult result) {
        HBox hbox = new HBox(10);
        hbox.setStyle("-fx-padding: 15; -fx-alignment: center;");

        Button htmlReportBtn = new Button("📄 Generate HTML Report");
        htmlReportBtn.setOnAction(e -> generateHtmlReport(result));
        hbox.getChildren().add(htmlReportBtn);

        Button textReportBtn = new Button("📝 Generate Text Report");
        textReportBtn.setOnAction(e -> generateTextReport(result));
        hbox.getChildren().add(textReportBtn);

        Button copyBtn = new Button("📋 Copy to Clipboard");
        copyBtn.setOnAction(e -> copyResultsToClipboard(result));
        hbox.getChildren().add(copyBtn);

        return hbox;
    }

    /**
     * Adds a metric visualization bar
     */
    private void addMetricBar(VBox vbox, String name, double score) {
        VBox metricBox = new VBox(5);

        HBox headerBox = new HBox();
        Label nameLabel = new Label(name);
        nameLabel.setStyle("-fx-font-weight: bold;");
        Label scoreLabel = new Label(String.format("%.2f%%", score * 100));
        scoreLabel.setStyle("-fx-text-fill: #667eea;");
        headerBox.getChildren().addAll(nameLabel, scoreLabel);

        ProgressBar progressBar = new ProgressBar(score);
        progressBar.setPrefWidth(Double.MAX_VALUE);
        progressBar.setStyle("-fx-accent: #667eea;");

        metricBox.getChildren().addAll(headerBox, progressBar);
        vbox.getChildren().add(metricBox);
    }

    /**
     * Generates HTML report
     */
    private void generateHtmlReport(ComparisonResult result) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save HTML Report");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("HTML Files", "*.html"));
        chooser.setInitialFileName("plagiarism_report.html");

        File file = chooser.showSaveDialog(stage);
        if (file != null) {
            new Thread(() -> {
                try {
                    ReportGenerator.saveReportAsHtml(result, file.getAbsolutePath());
                    Platform.runLater(() -> 
                        showInfo("Success", "HTML report saved successfully:\n" + file.getAbsolutePath())
                    );
                    logger.info("HTML report generated: {}", file.getAbsolutePath());
                } catch (IOException e) {
                    Platform.runLater(() -> 
                        showError("Error", "Failed to generate report: " + e.getMessage())
                    );
                    logger.error("Error generating HTML report", e);
                }
            }).start();
        }
    }

    /**
     * Generates text report
     */
    private void generateTextReport(ComparisonResult result) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save Text Report");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        chooser.setInitialFileName("plagiarism_report.txt");

        File file = chooser.showSaveDialog(stage);
        if (file != null) {
            new Thread(() -> {
                try {
                    ReportGenerator.saveReportAsText(result, file.getAbsolutePath());
                    Platform.runLater(() -> 
                        showInfo("Success", "Text report saved successfully:\n" + file.getAbsolutePath())
                    );
                    logger.info("Text report generated: {}", file.getAbsolutePath());
                } catch (IOException e) {
                    Platform.runLater(() -> 
                        showError("Error", "Failed to generate report: " + e.getMessage())
                    );
                    logger.error("Error generating text report", e);
                }
            }).start();
        }
    }

    /**
     * Copies results to clipboard
     */
    private void copyResultsToClipboard(ComparisonResult result) {
        String content = analyzer.generateDetailedReport(result);
        javafx.scene.input.Clipboard clipboard = javafx.scene.input.Clipboard.getSystemClipboard();
        javafx.scene.input.ClipboardContent clipboardContent = new javafx.scene.input.ClipboardContent();
        clipboardContent.putString(content);
        clipboard.setContent(clipboardContent);
        
        showInfo("Success", "Results copied to clipboard!");
    }

    // ==================== Helper Methods ====================

    private File chooseFile() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select Document");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text Files", "*.txt"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        return chooser.showOpenDialog(stage);
    }

    private void updateAnalyzeButtonState() {
        if (analyzeButton != null) {
            analyzeButton.setDisable(sourceDocument == null || targetDocument == null);
        }
    }

    private String getRiskColor(String riskLevel) {
        return switch (riskLevel) {
            case "CRITICAL" -> "#f93b1d";
            case "HIGH" -> "#fa7921";
            case "MEDIUM" -> "#ffa500";
            default -> "#4facfe";
        };
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
