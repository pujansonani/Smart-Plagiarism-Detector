package com.plagiarism.models;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a document to be analyzed for plagiarism
 */
public class Document {
    private String id;
    private String name;
    private String content;
    private String filePath;
    private LocalDateTime uploadedDate;
    private long wordCount;
    private List<String> tokens;

    public Document(String id, String name, String content, String filePath) {
        this.id = id;
        this.name = name;
        this.content = content;
        this.filePath = filePath;
        this.uploadedDate = LocalDateTime.now();
        this.wordCount = countWords(content);
        this.tokens = new ArrayList<>();
    }

    private long countWords(String text) {
        if (text == null || text.isEmpty()) return 0;
        return text.trim().split("\\s+").length;
    }

    public void tokenize() {
        this.tokens = tokenizeContent();
    }

    private List<String> tokenizeContent() {
        List<String> tokenList = new ArrayList<>();
        String[] words = content.toLowerCase().split("[^a-z0-9]+");
        for (String word : words) {
            if (!word.isEmpty() && word.length() > 2) {
                tokenList.add(word);
            }
        }
        return tokenList;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getContent() { return content; }
    public void setContent(String content) { 
        this.content = content;
        this.wordCount = countWords(content);
    }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public LocalDateTime getUploadedDate() { return uploadedDate; }
    public void setUploadedDate(LocalDateTime uploadedDate) { this.uploadedDate = uploadedDate; }

    public long getWordCount() { return wordCount; }

    public List<String> getTokens() { return tokens; }
    public void setTokens(List<String> tokens) { this.tokens = tokens; }

    @Override
    public String toString() {
        return "Document{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", wordCount=" + wordCount +
                '}';
    }
}
