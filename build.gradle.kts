plugins {
    java
    id("org.springframework.boot") version "3.5.5"
    id("io.spring.dependency-management") version "1.1.7"
    jacoco
}

group = "com.kapil"
version = providers.gradleProperty("version").get()
description = "Verba Metrics GUI"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
}

// ML/DS Dependencies Versions
val wekaVersion = "3.8.6"
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

    implementation("org.projectlombok:lombok:1.18.32")

    // Machine Learning and Data Science Dependencies
    implementation("nz.ac.waikato.cms.weka:weka-stable:$wekaVersion")
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

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }
}
