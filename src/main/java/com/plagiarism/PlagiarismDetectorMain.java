package com.plagiarism;

import com.plagiarism.ui.MainController;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main JavaFX application for Smart Plagiarism Detector
 */
public class PlagiarismDetectorMain extends Application {
    private static final Logger logger = LoggerFactory.getLogger(PlagiarismDetectorMain.class);

    private MainController controller;

    @Override
    public void start(Stage primaryStage) {
        logger.info("Starting Plagiarism Detector Application");

        try {
            primaryStage.setTitle("🔍 Smart Plagiarism Detector v1.0.0");
            primaryStage.setWidth(1400);
            primaryStage.setHeight(900);

            // Create main layout
            BorderPane root = createMainLayout();

            // Create and configure scene
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

            primaryStage.setScene(scene);
            primaryStage.show();
            primaryStage.toFront();
            primaryStage.requestFocus();

            // Set stage reference for file dialogs
            controller.setStage(primaryStage);

            logger.info("Application started successfully");

        } catch (Exception e) {
            logger.error("Error starting application", e);
            showErrorDialog("Startup Error", "Failed to start application: " + e.getMessage());
        }
    }

    /**
     * Creates the main application layout
     */
    private BorderPane createMainLayout() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f5f5f5;");

        // Top bar
        root.setTop(createTopBar());

        // Center content
        root.setCenter(createCenterContent());

        // Bottom status bar
        root.setBottom(createStatusBar());

        return root;
    }

    /**
     * Creates top navigation bar
     */
    private VBox createTopBar() {
        VBox topBar = new VBox();
        topBar.setStyle("-fx-background-color: linear-gradient(to right, #667eea, #764ba2); -fx-padding: 15;");
        topBar.setSpacing(10);

        // Title
        Label titleLabel = new Label("🔍 Smart Plagiarism Detector");
        titleLabel.setStyle("-fx-font-size: 24; -fx-font-weight: bold; -fx-text-fill: white;");

        Label subtitle = new Label("Advanced plagiarism detection using multiple algorithms");
        subtitle.setStyle("-fx-font-size: 12; -fx-text-fill: #e0e0e0;");

        topBar.getChildren().addAll(titleLabel, subtitle);
        return topBar;
    }

    /**
     * Creates center content area
     */
    private SplitPane createCenterContent() {
        SplitPane splitPane = new SplitPane();
        splitPane.setDividerPositions(0.5);

        // Left panel - Document input
        VBox leftPanel = createDocumentPanel();
        
        // Right panel - Results
        VBox rightPanel = createResultsPanel();

        splitPane.getItems().addAll(leftPanel, rightPanel);
        SplitPane.setResizableWithParent(leftPanel, true);
        SplitPane.setResizableWithParent(rightPanel, true);

        return splitPane;
    }

    /**
     * Creates document input panel
     */
    private VBox createDocumentPanel() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(15));
        panel.setStyle("-fx-border-color: #ddd; -fx-border-width: 0 1 0 0;");

        // Controller initialization
        controller = new MainController();

        // Source document section
        VBox sourceSection = new VBox(10);
        sourceSection.setStyle("-fx-border-color: #ddd; -fx-border-radius: 5; -fx-padding: 10;");

        Label sourceLabel = new Label("📄 Source Document");
        sourceLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");

        controller.sourceDocNameLabel = new Label("No document loaded");
        controller.sourceDocNameLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 11;");

        controller.loadSourceButton = new Button("📂 Load Source Document");
        controller.loadSourceButton.setPrefWidth(Double.MAX_VALUE);
        controller.loadSourceButton.setStyle("-fx-font-size: 12; -fx-padding: 10;");

        controller.sourceDocumentText = new TextArea();
        controller.sourceDocumentText.setWrapText(true);
        controller.sourceDocumentText.setPrefRowCount(8);
        controller.sourceDocumentText.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 11;");
        controller.sourceDocumentText.setEditable(true);

        sourceSection.getChildren().addAll(sourceLabel, controller.sourceDocNameLabel, 
                controller.loadSourceButton, controller.sourceDocumentText);
        VBox.setVgrow(controller.sourceDocumentText, Priority.ALWAYS);

        // Target document section
        VBox targetSection = new VBox(10);
        targetSection.setStyle("-fx-border-color: #ddd; -fx-border-radius: 5; -fx-padding: 10;");

        Label targetLabel = new Label("📄 Target Document");
        targetLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");

        controller.targetDocNameLabel = new Label("No document loaded");
        controller.targetDocNameLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 11;");

        controller.loadTargetButton = new Button("📂 Load Target Document");
        controller.loadTargetButton.setPrefWidth(Double.MAX_VALUE);
        controller.loadTargetButton.setStyle("-fx-font-size: 12; -fx-padding: 10;");

        controller.targetDocumentText = new TextArea();
        controller.targetDocumentText.setWrapText(true);
        controller.targetDocumentText.setPrefRowCount(8);
        controller.targetDocumentText.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 11;");
        controller.targetDocumentText.setEditable(true);

        targetSection.getChildren().addAll(targetLabel, controller.targetDocNameLabel, 
                controller.loadTargetButton, controller.targetDocumentText);
        VBox.setVgrow(controller.targetDocumentText, Priority.ALWAYS);

        // Analyze button
        controller.analyzeButton = new Button("🔍 Analyze Documents");
        controller.analyzeButton.setPrefWidth(Double.MAX_VALUE);
        controller.analyzeButton.setStyle("-fx-font-size: 14; -fx-padding: 12; -fx-font-weight: bold; " +
                "-fx-background-color: linear-gradient(to right, #667eea, #764ba2); -fx-text-fill: white;");
        controller.analyzeButton.setDisable(true);

        panel.getChildren().addAll(sourceSection, targetSection, controller.analyzeButton);
        VBox.setVgrow(sourceSection, Priority.ALWAYS);
        VBox.setVgrow(targetSection, Priority.ALWAYS);

        controller.initialize();

        return panel;
    }

    /**
     * Creates results display panel
     */
    private VBox createResultsPanel() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(15));

        // Results label
        controller.analysisResultsLabel = new Label("🔄 Results will appear here after analysis");
        controller.analysisResultsLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-padding: 10;");

        // Results scroll area
        controller.resultsPanel = new VBox(10);
        controller.resultsPanel.setPadding(new Insets(10));

        ScrollPane scrollPane = new ScrollPane(controller.resultsPanel);
        scrollPane.setStyle("-fx-border-color: #ddd;");
        scrollPane.setFitToWidth(true);

        // Info panel
        VBox infoPanel = new VBox(10);
        infoPanel.setStyle("-fx-border-color: #ddd; -fx-border-radius: 5; -fx-padding: 10; -fx-background-color: #f9f9f9;");
        infoPanel.setPrefHeight(150);

        Label infoTitle = new Label("ℹ️ About This Tool");
        infoTitle.setStyle("-fx-font-size: 12; -fx-font-weight: bold;");

        TextArea infoText = new TextArea(
                "Smart Plagiarism Detector uses multiple advanced algorithms:\n\n" +
                "• Cosine Similarity - Vector-based text comparison\n" +
                "• Levenshtein Distance - Edit-distance based matching\n" +
                "• N-gram Analysis - Sequence pattern matching\n" +
                "• Semantic Analysis - Writing style comparison\n\n" +
                "Final Score: Weighted average of all algorithms\n" +
                "Risk Level: CRITICAL (≥80%), HIGH (≥60%), MEDIUM (≥40%), LOW (<40%)"
        );
        infoText.setEditable(false);
        infoText.setWrapText(true);
        infoText.setPrefRowCount(10);
        infoText.setStyle("-fx-font-size: 10; -fx-font-family: 'Segoe UI';");

        infoPanel.getChildren().addAll(infoTitle, infoText);

        panel.getChildren().addAll(controller.analysisResultsLabel, scrollPane, infoPanel);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        return panel;
    }

    /**
     * Creates status bar
     */
    private HBox createStatusBar() {
        HBox statusBar = new HBox();
        statusBar.setStyle("-fx-background-color: #e0e0e0; -fx-padding: 10;");
        statusBar.setSpacing(20);

        Label statusLabel = new Label("✅ Ready");
        Label versionLabel = new Label("v1.0.0 | B.Tech Final Year Project");
        versionLabel.setStyle("-fx-text-fill: #666;");

        statusBar.getChildren().addAll(statusLabel, versionLabel);
        HBox.setHgrow(versionLabel, Priority.ALWAYS);

        return statusBar;
    }

    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
