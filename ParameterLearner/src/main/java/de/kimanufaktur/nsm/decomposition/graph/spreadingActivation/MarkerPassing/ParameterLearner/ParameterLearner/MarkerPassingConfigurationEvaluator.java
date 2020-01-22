/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

package de.kimanufaktur.nsm.decomposition.graph.spreadingActivation.MarkerPassing.ParameterLearner.ParameterLearner;


import de.kimanufaktur.nsm.decomposition.graph.Evaluation;
import de.kimanufaktur.nsm.decomposition.graph.spreadingActivation.MarkerPassing.MarkerPassingConfig;
import de.kimanufaktur.nsm.decomposition.graph.spreadingActivation.MarkerPassing.MarkerPassingSemanticDistanceMeasure;
import de.kimanufaktur.nsm.graph.entities.nodes.DoubleNodeWithMultipleThresholds;
import de.kimanufaktur.nsm.semanticDistanceMeasures.DataExample;
import de.kimanufaktur.nsm.semanticDistanceMeasures.SimilarityPair;
import de.kimanufaktur.nsm.semanticDistanceMeasures.data.Rubenstein1965Dataset;
import org.uncommons.watchmaker.framework.FitnessEvaluator;

import java.util.List;

/**
 * Created by faehndrich on 03.04.16.
 */

public class MarkerPassingConfigurationEvaluator implements FitnessEvaluator<MarkerPassingConfig>
{

    /**
     * Assigns one "fitness point" for every character in the
     * candidate String that matches the corresponding position in
     * the target string.
     */
    @Override
    public double getFitness(MarkerPassingConfig candidate,
                             List<? extends MarkerPassingConfig> population)
    {
        return evaluateIndividual(candidate);
    }

    public double evaluateIndividual(MarkerPassingConfig candidate) {
        MarkerPassingSemanticDistanceMeasure semanticDistanceMarkerPassing = new MarkerPassingSemanticDistanceMeasure();
        double pearson = 0.0d;
        double cumulativeResultError = 0;
        List<DataExample> trainingSynonymPairs = de.kimanufaktur.nsm.decomposition.graph.spreadingActivation.MarkerPassing.ParameterLearner.NfoldCrossvalidation.getInstance(MarkerPassingConfig.getFolds(), new Rubenstein1965Dataset()).getTrainingPairs();
        synchronized (trainingSynonymPairs){
        for(DataExample training : trainingSynonymPairs){
            training.setResult(semanticDistanceMarkerPassing.passMarker(((SimilarityPair) training).getString1(), MarkerPassingConfig.getWordType(), ((SimilarityPair) training).getString2(), MarkerPassingConfig.getWordType(), MarkerPassingConfig.getDecompositionDepth(), MarkerPassingConfig.getStartActivation(), MarkerPassingConfig.getThreshold(), MarkerPassingConfig.getThreshold(), DoubleNodeWithMultipleThresholds.class));
        }
        Evaluation.normalize(trainingSynonymPairs);
        for (DataExample pair : trainingSynonymPairs) {
            cumulativeResultError = Math.abs(pair.getTrueResult() - pair.getResult());
        }
            pearson = Evaluation.PearsonCorrelation(trainingSynonymPairs);
            if(Double.isNaN(pearson)){
                pearson = 0;
            }
        }
        return pearson;
    }






    public boolean isNatural()
    {
        return true;
    }
}