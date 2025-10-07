package com.kapil.verbametrics.services.impl;

import com.kapil.verbametrics.services.SyllableCounterService;
import com.kapil.verbametrics.services.engines.SyllableCountingEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * Implementation of SyllableCounterService using a syllable counting engine.
 * Provides functionality to count syllables in words and calculate average syllables per word.
 *
 * @author Kapil Garg
 */
@Service
public class SyllableCounterServiceImpl implements SyllableCounterService {

    private final SyllableCountingEngine countingEngine;

    @Autowired
    public SyllableCounterServiceImpl(SyllableCountingEngine countingEngine) {
        this.countingEngine = countingEngine;
    }

    @Override
    public int countSyllables(String word) {
        return countingEngine.countSyllables(word);
    }

    @Override
    public int countSyllables(String[] words) {
        return countingEngine.countSyllables(words);
    }

    @Override
    public double calculateAverageSyllablesPerWord(String[] words) {
        return countingEngine.calculateAverageSyllablesPerWord(words);
    }

}
