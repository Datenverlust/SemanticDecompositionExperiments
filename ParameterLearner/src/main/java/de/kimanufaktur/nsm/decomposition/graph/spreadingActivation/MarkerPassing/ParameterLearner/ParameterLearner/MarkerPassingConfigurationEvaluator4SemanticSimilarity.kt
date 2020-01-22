/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

package de.kimanufaktur.nsm.decomposition.graph.spreadingActivation.MarkerPassing.ParameterLearner

import de.kimanufaktur.nsm.decomposition.graph.spreadingActivation.MarkerPassing.MarkerPassingConfig
import org.uncommons.watchmaker.framework.FitnessEvaluator

/**
 * Created by faehndrich on 03.04.16.
 */

class MarkerPassingConfigurationEvaluator4SemanticSimilarity : FitnessEvaluator<MarkerPassingConfig> {

    /**
     * Assigns one "fitness point" for every character in the
     * candidate String that matches the corresponding position in
     * the target string.
     */
    override fun getFitness(candidate: MarkerPassingConfig,
                            population: List<MarkerPassingConfig>): Double {
        return evaluateIndividual(candidate)
    }

    fun evaluateIndividual(candidate: MarkerPassingConfig): Double {
        return java.lang.Double.NaN
        //        MarkerPassingSemanticDistanceMeasure semanticDistanceMarkerPassing = new MarkerPassingSemanticDistanceMeasure();
        //        double pearson = 0.0d;
        //        double cumulativeResultError = 0;
        //        //TODO: constructor does not exist . where to get the training data set for creating folds??????
        //        List<SynonymPair> trainingSynonymPairs = NfoldCrossvalidation.getInstance(MarkerPassingConfig.getFolds(), ).getTrainingPairs();
        //        synchronized (trainingSynonymPairs){
        //        for(SynonymPair training : trainingSynonymPairs){
        //            training.setResult(semanticDistanceMarkerPassing.passMarker(training.getWord(), MarkerPassingConfig.getWordType(), training.getSynonym(), MarkerPassingConfig.getWordType(), MarkerPassingConfig.getDecompositionDepth(), MarkerPassingConfig.getStartActivation(), MarkerPassingConfig.getThreshold(), MarkerPassingConfig.getThreshold(), DoubleNodeWithMultipleThresholds.class));
        //        }
        //        Evaluation.normalize(trainingSynonymPairs);
        //        for (SynonymPair pair : trainingSynonymPairs) {
        //            cumulativeResultError = Math.abs(pair.getResult()() - pair.getResult());
        //        }
        //            pearson = Evaluation.PearsonCorrelation(trainingSynonymPairs);
        //            if(Double.isNaN(pearson)){
        //                pearson = 0;
        //            }
        //        }
        //        return pearson;
    }

    override fun isNatural(): Boolean {
        return true
    }
}