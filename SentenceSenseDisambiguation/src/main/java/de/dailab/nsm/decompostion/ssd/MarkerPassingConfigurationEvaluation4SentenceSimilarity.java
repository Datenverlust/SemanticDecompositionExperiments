/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

package de.dailab.nsm.decompostion.ssd;

import de.dailab.nsm.decomposition.graph.Evaluation;
import de.dailab.nsm.decomposition.graph.spreadingActivation.MarkerPassing.MarkerPassingConfig;
import de.dailab.nsm.decomposition.graph.spreadingActivation.MarkerPassing.ParameterLearner.MarkerPassingConfigurationEvaluator;
import de.dailab.nsm.decomposition.graph.spreadingActivation.MarkerPassing.ParameterLearner.NfoldCrossvalidation;
import de.dailab.nsm.semanticDistanceMeasures.DataExample;
import de.dailab.nsm.semanticDistanceMeasures.SynonymPair;
import de.dailab.nsm.semanticDistanceMeasures.data.Rubenstein1965Dataset;

import java.util.List;


/**
 * Created by Johannes Fähndrich on 02.03.17 as part of his dissertation.
 */
public class MarkerPassingConfigurationEvaluation4SentenceSimilarity extends MarkerPassingConfigurationEvaluator {


    /**
     * /**
     * Assigns one "fitness point" for every character in the
     * candidate String that matches the corresponding position in
     * the target string.
     */
    @Override
    public double getFitness(MarkerPassingConfig candidate,
                             List<? extends MarkerPassingConfig> population) {
        return evaluateIndividual(candidate);
    }
    @Override
    public double evaluateIndividual(MarkerPassingConfig candidate) {
        //TODO: unclear which data set to use here. The unclear which concrete DataExample implementation (subclass) to use

//        SentenceSemanticSimilarityMeasure sentenceSemanticSimilarityMeasure = new SentenceSemanticSimilarityMeasure();
//
//
//        double pearson = 0.0d;
//        double cumulativeResultError = 0;
//        //List<SynonymPair> trainingSynonymPairs = NfoldCrossvalidation.getInstance(MarkerPassingConfig.getFolds()).getTrainingPairs();
//        List<DataExample> trainingSynonymPairs = NfoldCrossvalidation.getInstance(candidate.getFolds(), new Rubenstein1965Dataset() ).getTrainingPairs();
//
//        synchronized (trainingSynonymPairs) {
//            for (DataExample training : trainingSynonymPairs) {
//
//                training.setResult(sentenceSemanticSimilarityMeasure.compare(training(), training.getSynonym()));
//            }
//            Evaluation.normalize(trainingSynonymPairs);
//            for (DataExample pair : trainingSynonymPairs) {
//                cumulativeResultError = Math.abs(pair.getDistance() - pair.getResult());
//            }
//            pearson = Evaluation.PearsonCorrelation(trainingSynonymPairs);
//            if (Double.isNaN(pearson)) {
//                pearson = 0;
//            }
//        }
//        return pearson;
        return Double.NaN;
    }
}