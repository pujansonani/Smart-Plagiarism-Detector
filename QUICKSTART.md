# 🚀 Quick Start Guide - Smart Plagiarism Detector

## Installation & Setup (5 minutes)

### Step 1: Prerequisites Check
Verify you have:
- Java 11+ (`java -version`)
- Maven 3.6+ (`mvn -version`)

### Step 2: Build Project
```bash
cd PlagiarismDetector
mvn clean install
```

### Step 3: Run Application
```bash
mvn exec:java@run
```

**Application window should open in ~10 seconds.**

---

## Basic Usage

### Load Documents
1. Click **"📂 Load Source Document"** → Select a text file
2. Click **"📂 Load Target Document"** → Select another text file
3. Or type/paste text directly in the text areas

### Run Analysis
1. Click **"🔍 Analyze Documents"** button (becomes active when both documents loaded)
2. Wait for progress indicator to complete
3. Results appear on the right panel

### View Results
- **Plagiarism Score**: Overall similarity percentage
- **Risk Level**: CRITICAL, HIGH, MEDIUM, or LOW
- **Metrics Breakdown**: Individual algorithm scores
- **Suspicious Segments**: Highlighted problematic sections

### Generate Report
- **📄 HTML Report**: Beautiful formatted document (recommended)
- **📝 Text Report**: Plain text version
- **📋 Copy**: Copy results to clipboard

---

## Understanding Results

### Risk Levels
```
CRITICAL (≥80%)   →  Action required, likely plagiarism
HIGH (60-80%)     →  Significant concerns, verify manually
MEDIUM (40-60%)   →  Moderate similarity, review suggested
LOW (<40%)        →  Likely original content
```

### Algorithm Scores Explained

| Algorithm | What It Detects | Confidence |
|-----------|-----------------|------------|
| **Cosine** | Overall text similarity | Direct copy |
| **Levenshtein** | Character-level changes | Paraphrasing |
| **N-gram** | Sequence matches | Structure changes |
| **Semantic** | Writing style match | Author similarity |

### Example Interpretation
```
Final Score: 65% (RISK: HIGH)
├─ Cosine: 70%     ├─ Levenshtein: 60%
├─ N-gram: 68%     └─ Semantic: 55%

Interpretation: Document is likely paraphrased plagiarism
Action: Manual review recommended
```

---

## Sample Test Data

### If you don't have documents, try these:

**Document 1 (Original):**
```
Machine learning is revolutionizing artificial intelligence. 
Deep neural networks process complex patterns. 
Data scientists develop predictive models to solve real-world problems.
```

**Document 2 (High Plagiarism - ~75% match):**
```
AI is transforming machine learning technology.
Neural networks handle complicated patterns effectively.
ML engineers create forecast systems for practical applications.
```

**Document 3 (Medium Plagiarism - ~40% match):**
```
Technology advances rapidly with new innovations.
Computers solve complex mathematical equations.
Scientists develop new solutions for practical needs.
```

**Document 4 (Low Plagiarism - ~5% match):**
```
The weather is sunny today with clear skies.
The garden contains colorful flowers and green trees.
The beach is a nice place to visit during summer.
```

---

## Advanced Features

### 1. Batch Processing (Multiple Documents)
Load multiple files from a directory for comparison:
- Use File menu → Open Directory
- Automatically compares all documents pairwise
- Generates summary report

### 2. Custom Algorithm Weights
Edit `config.properties` to adjust weights:
```properties
algorithm.cosine.weight=0.40
algorithm.levenshtein.weight=0.30
algorithm.ngram.weight=0.20
algorithm.semantic.weight=0.10
```

### 3. Report Customization
- HTML reports have visual graphs and colors
- Text reports are plain and portable
- JSON export available for integration

---

## Troubleshooting

### Issue: Application won't start
**Solution:**
- Ensure Java 11+ installed: `java -version`
- Clear Maven cache: `mvn clean`
- Rebuild: `mvn clean install`

### Issue: Memory error during analysis
**Solution:**
- Increase JVM heap size: `mvn exec:java@run -Dexec.args="-Xmx1024m"`

### Issue: No results appearing
**Check:**
- Both documents are loaded
- Documents have text content (>100 characters)
- Check console for error messages

### Issue: Reports not saving
**Solution:**
- Create `/reports` directory
- Check file write permissions
- Verify disk space available

---

## Performance Tips

- **Large documents**: Analysis may take several seconds
- **Multiple documents**: Process one at a time
- **Memory**: Close other applications for faster analysis
- **Reports**: HTML reports take longer to generate than text

**Typical Times:**
- Single analysis: 200-500ms
- HTML report: 100-300ms
- Text report: 50-100ms

---

## Output Files

Files and their locations:

```
Project Root/
├── reports/               # Generated reports
│   ├── plagiarism_report_001.html
│   └── plagiarism_report_001.txt
├── logs/                  # Application logs
│   ├── plagiarism-detector.log
│   └── plagiarism-detector-error.log
└── temp/                  # Temporary files
    └── cache/
```

---

## Common Use Cases

### 1. Check Student Assignment (Academic)
```
Source: Student submission
Target: Suspected source (webpage, textbook)
→ If score >70%, likely plagiarism
→ Generate report for documentation
```

### 2. Compare Textbook Content
```
Source: Academic paper
Target: Textbook chapter
→ Measures content reuse
→ Helps with citation verification
```

### 3. Verify Original Authorship
```
Source: Author's portfolio
Target: Anonymous submission
→ Writing style analysis reveals patterns
→ Semantic similarity confirms style match
```

---

## Advanced Configuration

### Edit `config.properties` for:

**Sensitivity Settings:**
```properties
plagiarism.threshold.critical=0.85    # More strict
plagiarism.threshold.high=0.65
```

**Performance:**
```properties
performance.max.analysis.time=3000    # Timeout in ms
performance.thread.pool.size=8        # Parallel processing
```

**Reporting:**
```properties
gui.display.max.segments=10           # More details
report.formats=HTML,TEXT,JSON         # Output formats
```

---

## System Requirements

**Minimum:**
- Java 11+
- 2GB RAM
- 500MB disk space
- Windows/macOS/Linux

**Recommended:**
- Java 17+
- 4GB RAM
- 1GB disk space
- Modern OS

---

## Support & Resources

### Debug Mode
Enable detailed logging:
```bash
mvn exec:java@run -Dlogging.level=DEBUG
```

### Console Output
Check for detailed error messages in console:
- Algorithm execution logs
- File processing details
- Performance metrics

### Report Analysis Help
Look for sections in HTML report:
- **Summary**: Quick overview
- **Metrics**: Detailed algorithm scores
- **Segments**: Specific problem areas
- **Conclusion**: Assessment and recommendation

---

## Next Steps

### Learning Path
1. ✅ Understand what each algorithm does
2. ✅ Try with sample documents
3. ✅ Examine HTML reports
4. ✅ Test with your content
5. ✅ Read full documentation

### Project Enhancement
Consider extending with:
- Database integration for source checking
- Cloud deployment
- Mobile app version
- Machine learning models
- Real-time collaboration

---

## Legal & Academic Integrity

**This tool is designed for:**
- Educational purposes
- Academic integrity verification
- Content comparison
- Plagiarism detection

**Responsible Use:**
- Always seek consent before analyzing documents
- Use results fairly and ethically
- Consider context and original sources
- Support academic learning, not punishment

---

## Version Info

- **Current Version**: 1.0.0
- **Release Date**: 2024
- **Status**: Production Ready
- **License**: MIT

---

## Getting Help

1. **Check README.md** for detailed documentation
2. **Review source code comments** for technical details
3. **Run tests** to verify functionality: `mvn test`
4. **Check logs** in `/logs` directory for errors

---

### Ready to start? Load your first document! 🚀

For more information, see the full README.md documentation.
