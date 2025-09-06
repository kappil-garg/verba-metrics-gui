package com.kapil.verbametrics.config;

import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

/**
 * Configuration properties for sentiment analysis words.
 *
 * @author Kapil Garg
 */
@Setter
@Configuration
@ConfigurationProperties(prefix = "sentiment")
public class SentimentWordProperties {

    private String positiveWords;
    private String negativeWords;

    public List<String> getPositiveWords() {
        return positiveWords != null ? Arrays.asList(positiveWords.split(",")) : List.of();
    }

    public List<String> getNegativeWords() {
        return negativeWords != null ? Arrays.asList(negativeWords.split(",")) : List.of();
    }

}
