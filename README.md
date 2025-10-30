# Verba Metrics GUI

![Java 21](https://img.shields.io/badge/Java-21-0EA5E9?logo=openjdk&logoColor=white)
![Spring Boot 3](https://img.shields.io/badge/Spring%20Boot-3.x-6DB33F?logo=springboot&logoColor=white)
![Java Swing](https://img.shields.io/badge/UI-Java%20Swing-4D97FF)
![Weka](https://img.shields.io/badge/ML-Weka-7952B3)
![Gradle](https://img.shields.io/badge/Build-Gradle-007396?logo=gradle&logoColor=white)
![Platforms](https://img.shields.io/badge/Platforms-Windows%20%7C%20macOS%20%7C%20Linux-8B5CF6)

Advanced desktop application for sentiment analysis, readability assessment, and machine learning model training, built with Spring Boot 3 and Java Swing.

This README gives you the essentials to install, build, run, and use the app. For deeper documentation (architecture, algorithms, user guide), see the GitHub Wiki:

Wiki: https://github.com/kappil-garg/verba-metrics-gui/wiki

---

## Features

- Sentiment analysis (lexical-based, negation handling, intensity modifiers)
- Readability metrics (Flesch Reading Ease, Flesch–Kincaid Grade Level)
- ML module (Weka RandomTree) for supervised text classification
- Model training, evaluation (accuracy/precision/recall/F1 via 5-fold CV), and prediction
- Spring Boot backend with a Java Swing desktop UI
- H2 in-memory persistence for model metadata

Planned: confusion matrix and confidence intervals in evaluation, additional ML algorithms (Naive Bayes, SMO/SVM, RandomForest), accessibility enhancements, batch processing/export.

## Requirements

- Java 21 (JDK 21)
- Windows, macOS, or Linux
- Internet access for the first build (to fetch dependencies)

Optional:
- GraalVM (if you want to experiment with native image builds)
- jpackage (ships with JDK) to create platform installers

## Quick Start (Run the packaged JAR)

1) Download the release artifact `verba-metrics-gui-<version>.jar` from the Releases page
2) Run:

```bash
java -jar verba-metrics-gui-<version>.jar
```

If double-clicking the JAR on Windows doesn’t start the app, create `run.bat` next to the JAR:

```bat
@echo off
java -jar "%~dp0verba-metrics-gui-<version>.jar"
```

## Build from Source

At the project root:

```bash
# Windows
./gradlew.bat clean bootJar

# macOS/Linux
./gradlew clean bootJar
```

Artifacts are created in `build/libs/`:
- `verba-metrics-gui-<version>.jar` (runnable fat/boot JAR)
- `verba-metrics-gui-<version>-plain.jar` (plain JAR; not for running)

Run the app:

```bash
java -jar build/libs/verba-metrics-gui-<version>.jar
```

## Create a Windows Installer (optional)

Using `jpackage` (part of JDK):

```bash
jpackage \
  --type exe \
  --name VerbaMetricsGUI \
  --input build/libs \
  --main-jar verba-metrics-gui-<version>.jar \
  --main-class com.kapil.verbametrics.VerbaMetricsGuiApplication \
  --app-version <version>
```

Add `--icon path\to\icon.ico` if you have an icon.

## Using the Application

1) Launch the app
2) Text Analysis tab:
   - Paste or load text
   - Click Analyze to see: basic stats, sentiment, and readability (FRE is reported on a 0–100 scale; out-of-range values are clamped)
3) ML Model tab:
   - Prepare a small sample dataset (see below)
   - Train a model (RandomTree)
   - Evaluate (5-fold CV metrics) and Predict on new inputs

## Example: Minimal ML Workflow

Training data format (example JSON lines mapped by the UI to a tabular set of numeric features plus label):

```json
{
  "text": "Great product and very durable",
  "features": [0.75, 0.10, 0.15],
  "label": "positive"
}
```

Steps:
- Provide a list of entries like above (the UI converts to a Weka dataset and strips the text column for RandomTree)
- Click Train → model is built and stored; evaluation shows accuracy/precision/recall/F1 via 5-fold cross-validation
- Click Predict → enter a new `text` and `features`; the app returns `prediction`, `probability`, and a calibrated `confidence`

## Configuration

Key settings (type-safe Spring Boot properties):

- Readability coefficients and thresholds: `ReadabilityAnalysisProperties`
- ML settings (limits, defaults, cache): `MLModelProperties`

You can override defaults via `application.properties` if needed.

## Troubleshooting

- App doesn’t start by double-clicking the JAR:
  - Use the command line: `java -jar verba-metrics-gui-<version>.jar`
  - Or create a `run.bat` as shown above
- Out-of-memory on very large ML datasets:
  - Increase heap: `java -Xmx2g -jar verba-metrics-gui-<version>.jar`
  - Or sample/stream data for training
- Model won’t load/predict:
  - Ensure you trained a model and the model ID is correct
  - Check logs in the console for details

## Development

Run tests and coverage:

```bash
# Windows
./gradlew.bat clean test jacocoTestReport

# macOS/Linux
./gradlew clean test jacocoTestReport
```

Open the JaCoCo HTML report under `build/reports/jacoco/test/html/index.html`.

## Contributing

Issues and PRs are welcome. Please:

- Provide a clear issue description with steps to reproduce, expected vs actual behavior, and environment details
- Avoid breaking changes (keep stable behaviors intact)
- Include tests for bug fixes and for changes affecting algorithms, metrics, or model I/O
- Follow the existing code style and structure; keep changes focused and small

---
