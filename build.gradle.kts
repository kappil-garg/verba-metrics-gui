plugins {
    java
    id("org.springframework.boot") version "3.5.5"
    id("io.spring.dependency-management") version "1.1.7"
    jacoco
}

group = "com.kapil"
version = "1.0.0"
description = "Verba Metrics GUI"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
}

val apachePoiVersion = "5.4.0"
val commonsIoVersion = "2.17.0"
val commonsLang3Version = "3.18.0"
val commonsCompressVersion = "1.27.1"

// ML/DS Dependencies Versions
val wekaVersion = "3.8.6"
val smileVersion = "3.0.1"
val dl4jVersion = "1.0.0-M2"
val nd4jVersion = "1.0.0-M2"
val opennlpVersion = "2.3.0"
val jfreechartVersion = "1.5.3"
val apacheCommonsMathVersion = "3.6.1"

configurations.compileOnly {
    extendsFrom(configurations.annotationProcessor.get())
}

dependencies {

    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-json")

    implementation("com.h2database:h2")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    implementation("org.apache.poi:poi:$apachePoiVersion")
    implementation("org.apache.poi:poi-ooxml:$apachePoiVersion")

    implementation("org.projectlombok:lombok:1.18.32")
    implementation("commons-io:commons-io:$commonsIoVersion")
    implementation("org.apache.commons:commons-lang3:$commonsLang3Version")
    implementation("org.apache.commons:commons-compress:$commonsCompressVersion")

    // Machine Learning and Data Science Dependencies
    implementation("org.jfree:jfreechart:$jfreechartVersion")
    implementation("org.nd4j:nd4j-native-platform:$nd4jVersion")
    implementation("com.github.haifengl:smile-core:$smileVersion")
    implementation("nz.ac.waikato.cms.weka:weka-stable:$wekaVersion")
    implementation("org.apache.opennlp:opennlp-tools:$opennlpVersion")
    implementation("org.deeplearning4j:deeplearning4j-core:$dl4jVersion")
    implementation("org.apache.commons:commons-math3:$apacheCommonsMathVersion")

    implementation("org.slf4j:slf4j-api")
    implementation("ch.qos.logback:logback-classic")

    annotationProcessor("org.projectlombok:lombok:1.18.32")
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    testImplementation("org.mockito:mockito-core:5.8.0")
    testImplementation("org.projectlombok:lombok:1.18.32")
    testImplementation("net.bytebuddy:byte-buddy-agent:1.14.11")
    testImplementation("org.springframework.boot:spring-boot-starter-test")

}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
    jvmArgs = listOf(
        "--add-opens", "java.base/java.lang=ALL-UNNAMED",
        "--add-opens", "java.base/java.util=ALL-UNNAMED",
        "--add-opens", "java.base/java.lang.reflect=ALL-UNNAMED",
        "--add-opens", "java.base/java.text=ALL-UNNAMED",
        "--add-opens", "java.desktop/java.awt.font=ALL-UNNAMED"
    )
}

jacoco {
    toolVersion = "0.8.12"
}
