package com.plagiarism.utils;

import com.plagiarism.models.Document;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * File processing utilities for handling document files
 */
public class FileProcessor {

    // Supported file extensions
    private static final Set<String> SUPPORTED_EXTENSIONS = new HashSet<>(Arrays.asList(
            "txt", "pdf", "doc", "docx"
    ));

    /**
     * Loads a document from file
     */
    public static Document loadDocument(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        
        if (!Files.exists(path)) {
            throw new IOException("File not found: " + filePath);
        }

        String content = Files.readString(path, StandardCharsets.UTF_8);
        String fileName = path.getFileName().toString();
        String id = UUID.randomUUID().toString();

        Document doc = new Document(id, fileName, content, filePath);
        doc.tokenize();

        return doc;
    }

    /**
     * Saves a document to file
     */
    public static void saveDocument(Document document, String outputPath) throws IOException {
        Path path = Paths.get(outputPath);
        
        // Create directories if they don't exist
        Files.createDirectories(path.getParent());
        
        Files.writeString(path, document.getContent(), StandardCharsets.UTF_8);
    }

    /**
     * Loads multiple documents from a directory
     */
    public static List<Document> loadDocumentsFromDirectory(String directoryPath) throws IOException {
        List<Document> documents = new ArrayList<>();
        Path dirPath = Paths.get(directoryPath);

        if (!Files.isDirectory(dirPath)) {
            throw new IOException("Path is not a directory: " + directoryPath);
        }

        Files.walk(dirPath, 1)
                .filter(Files::isRegularFile)
                .filter(path -> isSupportedFile(path.toString()))
                .forEach(path -> {
                    try {
                        documents.add(loadDocument(path.toString()));
                    } catch (IOException e) {
                        System.err.println("Error loading file " + path + ": " + e.getMessage());
                    }
                });

        return documents;
    }

    /**
     * Checks if file format is supported
     */
    public static boolean isSupportedFile(String filePath) {
        String extension = getFileExtension(filePath).toLowerCase();
        return SUPPORTED_EXTENSIONS.contains(extension);
    }

    /**
     * Extracts file extension
     */
    public static String getFileExtension(String filePath) {
        int lastDot = filePath.lastIndexOf('.');
        return lastDot > 0 ? filePath.substring(lastDot + 1) : "";
    }

    /**
     * Gets file size in KB
     */
    public static long getFileSizeKB(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        return Files.size(path) / 1024;
    }

    /**
     * Creates a document from raw text string
     */
    public static Document createDocumentFromText(String name, String content) {
        Document doc = new Document(UUID.randomUUID().toString(), name, content, "");
        doc.tokenize();
        return doc;
    }

    /**
     * Exports document to plain text
     */
    public static void exportAsText(Document document, String outputPath) throws IOException {
        saveDocument(document, outputPath);
    }

    /**
     * Validates document content
     */
    public static boolean validateDocument(Document document) {
        return document != null &&
                document.getContent() != null &&
                !document.getContent().trim().isEmpty() &&
                document.getContent().length() >= 100; // Minimum 100 characters
    }

    /**
     * Removes duplicate documents by comparing content hash
     */
    public static List<Document> removeDuplicates(List<Document> documents) {
        Map<String, Document> uniqueDocs = new LinkedHashMap<>();

        for (Document doc : documents) {
            String hash = String.valueOf(doc.getContent().hashCode());
            if (!uniqueDocs.containsKey(hash)) {
                uniqueDocs.put(hash, doc);
            }
        }

        return new ArrayList<>(uniqueDocs.values());
    }

    /**
     * Gets directory contents summary
     */
    public static Map<String, Integer> getDirectorySummary(String directoryPath) throws IOException {
        Map<String, Integer> summary = new HashMap<>();

        Files.walk(Paths.get(directoryPath), 1)
                .filter(Files::isRegularFile)
                .forEach(path -> {
                    String extension = getFileExtension(path.toString()).toLowerCase();
                    summary.put(extension, summary.getOrDefault(extension, 0) + 1);
                });

        return summary;
    }
}
