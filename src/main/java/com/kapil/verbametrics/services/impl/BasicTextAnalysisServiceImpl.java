package com.kapil.verbametrics.services.impl;

import com.kapil.verbametrics.dto.TextAnalysisRequest;
import com.kapil.verbametrics.dto.TextAnalysisResponse;
import com.kapil.verbametrics.services.BasicTextAnalysisService;
import com.kapil.verbametrics.services.engines.BasicTextAnalysisEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * Implementation of BasicTextAnalysisService using BasicTextAnalysisEngine.
 * Provides functionality to analyze text and return basic statistics.
 *
 * @author Kapil Garg
 */
@Service
public class BasicTextAnalysisServiceImpl implements BasicTextAnalysisService {

    private final BasicTextAnalysisEngine analysisEngine;

    @Autowired
    public BasicTextAnalysisServiceImpl(BasicTextAnalysisEngine analysisEngine) {
        this.analysisEngine = analysisEngine;
    }

    @Override
    public TextAnalysisResponse analyzeText(TextAnalysisRequest request) {
        Objects.requireNonNull(request, "Request cannot be null");
        return analysisEngine.analyze(request);
    }

}
