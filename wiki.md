### Wiki Home (Overview)
- Verba Metrics GUI is a desktop application for advanced text analysis: sentiment analysis, readability assessment, and supervised ML model training.
- Built with Spring Boot 3 (services) and Java Swing (UI). Uses Weka for ML and H2 in-memory persistence for model metadata.

## Quick Links
- Installation and Setup
- User Guide
- Architecture
- Text Analysis
- Readability
- Machine Learning
- Data and Persistence
- Configuration
- Evaluation and Results
- Troubleshooting
- Roadmap
- Contributing

---

### Installation and Setup
## Requirements
- Java 21 (JDK 21)
- Windows/macOS/Linux
- Internet access for first build

## Install Options
- Run from released JAR:
  - Command: `java -jar verba-metrics-gui-<version>.jar`
- Build from source:
  - Windows: `./gradlew.bat clean bootJar`
  - macOS/Linux: `./gradlew clean bootJar`
  - Run: `java -jar build/libs/verba-metrics-gui-<version>.jar`

## Optional
- Windows launcher `run.bat`:
  - `@echo off`
  - `java -jar "%~dp0verba-metrics-gui-<version>.jar"`
- Create Windows installer (jpackage):
  - `jpackage --type exe --name VerbaMetricsGUI --input build/libs --main-jar verba-metrics-gui-<version>.jar --main-class com.kapil.verbametrics.VerbaMetricsGuiApplication --app-version <version>`

---

### User Guide
## Application Layout
- Main window with tabbed panels:
  - Text Analysis: input area, analyze button, results pane
  - ML Model: training data input, train/evaluate, predict
  - Menus/toolbars: file actions, help, future toggles (theme)

## Text Analysis Workflow
- Paste or load text file
- Click Analyze to view:
  - Basic stats: words, sentences, characters, paragraphs
  - Sentiment: label, score, confidence (lexical-based with negation and intensity modifiers)
  - Readability: Flesch Reading Ease (0–100, clamped) and Flesch–Kincaid Grade Level

## ML Workflow
- Prepare training items with numeric features + label
- Train (RandomTree), view 5-fold CV metrics (accuracy/precision/recall/F1)
- Predict on new inputs; see label, probability, and calibrated confidence

## Example training item (JSON concept)
```json
{
  "text": "Great product and very durable",
  "features": [0.75, 0.10, 0.15],
  "label": "positive"
}
```

---

### Architecture
## Layers
- Service Layer: business logic for text analysis, readability, ML
- Engine Layer: core algorithm execution (stateless, testable)
- Calculator Layer: pure computations (sentence length, syllables, metrics)
- Manager Layer: caching, file I/O, resource orchestration
- Configuration Layer: type-safe Spring Boot properties

## Key Modules
- Sentiment: lexical lists, negation/intensity handling, confidence levels
- Readability: Flesch Reading Ease, Flesch–Kincaid, syllable counting
- ML: Weka RandomTree training/evaluation/prediction
- Persistence: H2 in-memory for model metadata (JPA)

---

### Text Analysis
## Basic Statistics
- Word, sentence, paragraph, and character counts
- Derived metrics: average sentence length, average chars/word

## Sentiment Analysis
- Lexicon-based with:
  - Negation handling
  - Intensity modifiers
  - Confidence scoring (low/medium/high thresholds)
- Output: label, score, confidence

---

### Readability
## Metrics Implemented
- Flesch Reading Ease (0–100, clamped for display and validation)
- Flesch–Kincaid Grade Level

## Classifications
- Reading level thresholds (elementary, middle school, high school, college)
- Complexity from FRE (very easy → very difficult)

## Notes
- FRE can mathematically go <0 or >100; clamped to 0–100 for consistency and UI thresholds

---

### Machine Learning
## Algorithms
- Implemented: Weka RandomTree
- Planned: Naive Bayes, SMO/SVM, RandomForest

## Training
- Converts input to Weka Instances (numeric features; text column excluded)
- Parameterization includes adaptive defaults by dataset size
- Stores model file and class values for later prediction

## Evaluation
- 5-fold cross-validation: accuracy, precision, recall, F1
- Planned: confusion matrix, confidence intervals, significance testing

## Prediction
- Returns: prediction label, prediction index, per-class probabilities, top-class probability, calibrated confidence, timestamp

---

### Data and Persistence
## H2 In-Memory Database
- JPA entity `MLModelEntity` stores metadata:
  - modelId, type, name, version
  - timestamps, status, accuracy
  - parameters and performance metrics maps
- Hybrid strategy: model binaries saved to filesystem; metadata in H2

## File Management
- Model files are stored and loaded by model ID
- Cache manager provides bounded, time-aware caching of loaded models

---

### Configuration
## Properties
- Readability coefficients/thresholds: `ReadabilityAnalysisProperties`
- ML limits/defaults/cache/performance thresholds: `MLModelProperties`
- Override defaults via `application.properties` or profiles (`application-prod.properties`, etc.)

---

### Evaluation and Results
## Included
- Example benchmark with long/complex sentence:
  - Shows stats, POSITIVE sentiment with confidence, FRE near lower bound (clamped to 0), FK grade level ≈ graduate
- JaCoCo coverage report available via Gradle

## Reproducibility
- Provide sample input files; list exact steps and expected outputs
- Capture screenshots for report and wiki

---

### Troubleshooting
## App doesn’t start (double-click)
- Use terminal: `java -jar verba-metrics-gui-<version>.jar`

## Large datasets OOM
- Increase heap: `java -Xmx2g -jar verba-metrics-gui-<version>.jar`
- Consider sampling/streaming for training

## Prediction fails
- Ensure a trained model exists and correct model ID
- Check console logs for stack traces

---

### Roadmap
## Short Term
- Confusion matrix and basic evaluation reporting
- Export results to CSV/JSON
- UI quality-of-life: keyboard shortcuts, HiDPI scaling, theme toggle

## Long Term
- Additional ML algorithms (Naive Bayes, SMO/SVM, RandomForest)
- Multi-language support (readability/sentiment)
- Cloud/web API layer and collaborative features
- Advanced UI visualizations and accessibility improvements

---

### Contributing
## Guidelines
- Open issues with clear steps, expected vs actual, and environment details
- Avoid breaking changes; use deprecations if necessary
- Include tests for algorithmic or I/O-affecting changes
- Follow existing code style and keep changes scoped
- Update docs (README or wiki) if user-visible behavior changes

---

### Release Notes
## v1.0.0
- Initial public release
- Desktop app with text analysis, readability, and ML (RandomTree)
- 5-fold CV metrics; prediction with probabilities and confidence
- Readability FRE clamped for consistent reporting
- H2 + JPA metadata persistence, model file management and caching


