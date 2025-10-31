package com.kapil.verbametrics.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

/**
 * Configuration properties for sentiment rule weights and dictionaries.
 * Provides configurable boosters/dampeners, contrastives, punctuation breaks and phrases with weights.
 *
 * @author Kapil Garg
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "sentiment.analysis.rules")
public class SentimentRuleProperties {

    private Map<String, Double> boosters = Map.ofEntries(
            Map.entry("extremely", 0.30),
            Map.entry("very", 0.29),
            Map.entry("really", 0.27),
            Map.entry("highly", 0.27),
            Map.entry("so", 0.25),
            Map.entry("totally", 0.25),
            Map.entry("completely", 0.25),
            Map.entry("utterly", 0.25),
            Map.entry("absolutely", 0.25),
            Map.entry("incredibly", 0.30),
            Map.entry("too", 0.20)
    );

    private Map<String, Double> dampeners = Map.ofEntries(
            Map.entry("slightly", -0.29),
            Map.entry("somewhat", -0.27),
            Map.entry("bit", -0.25),
            Map.entry("little", -0.25),
            Map.entry("mildly", -0.25),
            Map.entry("rather", -0.20),
            Map.entry("fairly", -0.20),
            Map.entry("kinda", -0.20),
            Map.entry("quite", -0.15),
            Map.entry("average", -0.10)
    );

    private List<String> punctuationBreaks = List.of(".", "!", "?", ",", ";", ":");

    private List<String> contrastives = List.of("but", "however", "though", "yet");

    private Map<String, Double> phrases = Map.ofEntries(
            Map.entry("waste of time", -1.5),
            Map.entry("poorly communicated", -1.0),
            Map.entry("customer support unresponsive", -1.2),
            Map.entry("fell apart", -1.2),
            Map.entry("not good", -0.8),
            Map.entry("worth recommending", 0.6),
            Map.entry("hard to believe", -0.8),
            Map.entry("total disappointment", -1.5),
            Map.entry("more bugs than it fixed", -1.3),
            Map.entry("performance has drastically worsened", -1.4)
    );

    private int negationWindow = 3;
    private int contrastiveWindow = 10;
    private double normalizationAlpha = 15.0;

    private List<String> negations = List.of(
            "not", "no", "never", "none", "nobody", "nothing", "neither", "nowhere", "hardly", "scarcely", "barely",
            "isnt", "isn't", "arent", "aren't", "wasnt", "wasn't", "werent", "weren't", "dont", "don't", "doesnt", "doesn't",
            "didnt", "didn't", "cant", "can't", "cannot", "couldnt", "couldn't", "wont", "won't", "wouldnt", "wouldn't",
            "shouldnt", "shouldn't", "hasnt", "hasn't", "havent", "haven't", "hadnt", "hadn't"
    );

}
