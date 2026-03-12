# 🔍 Smart Plagiarism Detector

A comprehensive, intelligent plagiarism detection system built in Java with advanced algorithms and a modern GUI. Perfect for academic applications and similarity analysis.

## 📋 Features

### Core Detection Algorithms
- **Cosine Similarity** - TF-IDF based vector space model (35% weight)
- **Levenshtein Distance** - Edit distance measurement (25% weight)
- **N-gram Analysis** - Bigrams and trigrams comparison (25% weight)
- **Semantic Analysis** - Writing style and structure features (15% weight)

### Advanced Capabilities
- **Multi-Algorithm Fusion** - Weighted combination for robust detection
- **Detailed Segment Detection** - Highlights suspicious sections with similarity scores
- **Writing Style Analysis** - Detects writing pattern similarities
- **Comprehensive Reporting** - HTML and text report generation
- **Professional GUI** - Modern JavaFX interface with real-time visualization
- **Batch Processing** - Cross-comparison of multiple documents

### Additional Features
- File processing (TXT, PDF, DOC, DOCX support ready)
- Text preprocessing (normalization, stop word removal, tokenization)
- Readability analysis
- Language detection
- Style feature extraction
- Customizable algorithm weights

## 🏗️ Project Architecture

```
PlagiarismDetector/
├── src/main/java/com/plagiarism/
│   ├── PlagiarismDetectorMain.java       # Main JavaFX application
│   ├── ui/
│   │   └── MainController.java           # GUI controller logic
│   ├── engine/
│   │   ├── PlagiarismAnalyzer.java       # Core analysis engine
│   │   └── SimilarityCalculator.java     # Algorithm implementations
│   ├── models/
│   │   ├── Document.java                 # Document model
│   │   ├── ComparisonResult.java         # Analysis result model
│   │   └── SuspiciousSegment.java        # Flagged text segments
│   ├── utils/
│   │   ├── TextPreprocessor.java         # Text processing utilities
│   │   ├── FileProcessor.java            # File I/O operations
│   │   └── ReportGenerator.java          # Report generation
│   └── ml/
│       └── FeatureExtractor.java         # ML feature extraction
├── src/test/java/com/plagiarism/
│   ├── engine/
│   │   ├── SimilarityCalculatorTest.java # Algorithm unit tests
│   │   └── PlagiarismAnalyzerTest.java   # Integration tests
│   └── utils/
│       └── TextPreprocessorTest.java     # Utility tests
├── pom.xml
└── README.md
```

## 🚀 Getting Started

### Prerequisites
- Java 11 or higher
- Maven 3.6+
- JavaFX 21.0.1 SDK

### Installation

1. **Clone or download the project**
   ```bash
   git clone <repository-url>
   cd PlagiarismDetector
   ```

2. **Build the project**
   ```bash
   mvn clean install
   ```

3. **Run the application**
   ```bash
   mvn exec:java@run
   ```
   Or run directly:
   ```bash
   java -jar target/plagiarism-detector-1.0.0.jar
   ```

## 💻 Usage

### GUI Application

1. **Load Documents**
   - Click "Load Source Document" and select the first document
   - Click "Load Target Document" and select the comparison document
   - Or paste text directly into the text areas

2. **Run Analysis**
   - Click "Analyze Documents"
   - Wait for analysis to complete
   - View results with similarity scores and suspicious segments

3. **Generate Reports**
   - Click "Generate HTML Report" for a beautifully formatted report
   - Click "Generate Text Report" for a plain text version
   - Use "Copy to Clipboard" to copy results

### Supported Report Features
- Summary with risk level assessment
- Document metadata
- Detailed algorithm scores with visual bars
- Flagged suspicious segments
- Conclusion and recommendations

## 🧪 Algorithm Details

### 1. Cosine Similarity (35% weight)
- Converts documents to TF-IDF vectors
- Calculates angle between vectors
- Range: 0.0 (completely different) to 1.0 (identical)
- Best for: Overall document similarity

### 2. Levenshtein Distance (25% weight)
- Counts minimum edit operations needed
- Normalized by maximum string length
- Range: 0.0 (completely different) to 1.0 (identical)
- Best for: Character-level plagiarism, paraphrasing detection

### 3. N-gram Analysis (25% weight)
- Extracts bigrams (2-char sequences) and trigrams (3-char)
- Calculates Jaccard similarity
- Combines both with 40/60 weight ratio
- Range: 0.0 to 1.0
- Best for: Sequence matching, structural similarity

### 4. Semantic Similarity (15% weight)
- Extracts writing style features:
  - Average word length
  - Average sentence length
  - Type-token ratio (vocabulary richness)
  - Punctuation density
- Measures style consistency
- Range: 0.0 to 1.0
- Best for: Author identification, writing style matching

### Final Score Calculation
```
Final Score = (Cosine × 0.35) + (Levenshtein × 0.25) + 
              (N-gram × 0.25) + (Semantic × 0.15)
```

### Risk Levels
- **CRITICAL** (≥80%): Likely plagiarism, immediate action needed
- **HIGH** (60-80%): Strong plagiarism indicators detected
- **MEDIUM** (40-60%): Moderate similarity, review recommended
- **LOW** (<40%): Likely original or minimal overlap

## 📊 Sample Results

```
Plagiarism Score:        78.45%
Risk Level:              HIGH
Analysis Time:           245 ms
Suspicious Segments:     12

Algorithm Breakdown:
├─ Cosine Similarity:     82.3%
├─ Levenshtein:          75.1%
├─ N-gram:               71.8%
└─ Semantic:             68.5%
```

## 🔧 Text Preprocessing

The system includes advanced preprocessing:
- **Normalization**: Lowercase conversion, punctuation removal
- **Stop Word Removal**: 60+ common English stop words filtered
- **Tokenization**: Word and sentence extraction
- **Lemmatization**: Basic word form normalization

## 📝 API Usage

```java
// Create analyzer
PlagiarismAnalyzer analyzer = new PlagiarismAnalyzer();

// Create documents
Document doc1 = new Document("id1", "Essay 1", content1, path1);
Document doc2 = new Document("id2", "Essay 2", content2, path2);

// Tokenize documents
doc1.tokenize();
doc2.tokenize();

// Perform analysis
ComparisonResult result = analyzer.analyze(doc1, doc2);

// Access results
double plagiarismScore = result.getFinalScore();
String riskLevel = result.getRiskLevel();
List<SuspiciousSegment> segments = result.getSuspiciousSegments();

// Generate report
ReportGenerator.saveReportAsHtml(result, "report.html");
```

## 🧬 Advanced Features

### Custom Algorithm Weights
```java
analyzer.setWeights(
    0.40,  // cosine weight
    0.30,  // levenshtein weight
    0.20,  // ngram weight
    0.10   // semantic weight
);
```

### Batch Processing
```java
// Compare multiple documents
Document reference = FileProcessor.loadDocument("ref.txt");
List<Document> candidates = FileProcessor.loadDocumentsFromDirectory("documents/");
List<ComparisonResult> results = analyzer.analyzeMultiple(reference, candidates);
```

### Cross-Comparison
```java
List<Document> documents = ...;
List<ComparisonResult> allResults = analyzer.performCrossComparison(documents);
```

## 🧪 Testing

Run unit tests:
```bash
mvn test
```

Test Coverage:
- Similarity algorithms (12+ test cases)
- Text preprocessing (8+ test cases)
- Document model validation
- Report generation

## 📈 Performance

Typical Performance:
- **Single document comparison**: <500ms
- **Feature extraction**: <100ms per document
- **Report generation**: <200ms
- **Memory usage**: ~50-100MB for standard documents

## 🎓 Academic Integrity Features

- Detects common plagiarism techniques:
  - Direct copying
  - Paraphrasing with minimal changes
  - Structure/order modification
  - Writing style similarity
- Provides educator tools for fairness
- Supports genuine citation analysis

## 📚 Technologies Used

- **Core**: Java 11+
- **GUI**: JavaFX 21.0.1
- **Build**: Maven 3.6+
- **Testing**: JUnit 4
- **Utilities**: Apache Commons, GSON, Logback
- **Logging**: SLF4J + Logback

## 🔐 Privacy & Security

- Local processing (no cloud dependencies)
- No data transmission
- Secure file handling
- No document storage without explicit user action

## 📄 License

MIT License - See LICENSE file for details

## 👤 Author

**B.Tech Final Year Student**
- Smart Plagiarism Detection System v1.0.0
- Academic Project 2024

## 🤝 Contributing

Improvements welcome! Potential enhancements:
- ML-based detection (SVM, Neural Networks)
- Source database integration
- Multi-language support
- Advanced visualization
- Cloud integration (optional)

## 📞 Support

For issues or questions:
1. Check console logs (Run → Show Console Output)
2. Verify document format (TXT recommended)
3. Ensure Java version compatibility
4. Check available system memory

## 🎯 Future Enhancements

- [ ] Ensemble learning models
- [ ] Deep learning integration
- [ ] Real-time collaborative detection
- [ ] Cloud-based analysis
- [ ] Mobile app support
- [ ] Source citation detection
- [ ] Multi-language support
- [ ] Advanced visualization dashboard

## 📊 Academic Use Cases

- **Plagiarism Detection**: Student assignments, research papers
- **Similarity Analysis**: Document clustering, content recommendation
- **Citation Analysis**: Source tracking, proper attribution
- **Content Verification**: Academic integrity enforcement
- **Research Support**: Literature review assistance

---

**Version**: 1.0.0  
**Last Updated**: 2024  
**Status**: Production Ready ✅
