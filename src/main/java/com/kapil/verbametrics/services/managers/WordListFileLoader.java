package com.kapil.verbametrics.services.managers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Loads word lists from files, supporting classpath resources.
 * Provides functionality to read and return a list of words from a specified file.
 *
 * @author Kapil Garg
 */
@Component
public class WordListFileLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(WordListFileLoader.class);

    /**
     * Loads words from a file path.
     *
     * @param filePath the path to the word list file
     * @return list of words from the file
     */
    public List<String> loadWordsFromFile(String filePath) {
        try {
            if (filePath == null || filePath.trim().isEmpty()) {
                LOGGER.warn("Word list file path is null/blank");
                return List.of();
            }
            String path = filePath;
            if (path.startsWith("classpath:")) {
                path = path.substring("classpath:".length());
                if (path.startsWith("/")) {
                    path = path.substring(1);
                }
            }
            ClassPathResource resource = new ClassPathResource(path);
            if (!resource.exists()) {
                LOGGER.warn("Word list file not found: {}", filePath);
                return List.of();
            }
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
                return reader.lines()
                        .map(String::trim)
                        .filter(line -> !line.isEmpty() && !line.startsWith("#"))
                        .toList();
            }
        } catch (IOException e) {
            LOGGER.error("Failed to load words from file: {}", filePath, e);
            return List.of();
        }
    }

}
