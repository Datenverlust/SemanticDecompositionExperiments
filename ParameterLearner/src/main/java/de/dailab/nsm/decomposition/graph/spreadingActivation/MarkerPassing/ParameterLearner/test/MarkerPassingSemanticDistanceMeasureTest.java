/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

package de.dailab.nsm.decomposition.graph.spreadingActivation.MarkerPassing.ParameterLearner.test;

import de.dailab.nsm.decomposition.WordType;
import de.dailab.nsm.decomposition.graph.entities.nodes.DoubleNodeWithMultipleThresholds;
import de.dailab.nsm.decomposition.graph.spreadingActivation.MarkerPassing.MarkerPassingConfig;
import de.dailab.nsm.decomposition.graph.spreadingActivation.MarkerPassing.ParameterLearner.MarkerPassingSemanticDistanceMeasure;

import de.dailab.nsm.decomposition.graph.spreadingActivation.MarkerPassing.ParameterLearner.SemanticDistanceTest;
import de.dailab.nsm.decomposition.graph.spreadingActivation.MarkerPassing.ParameterLearner.TestRunnable;
import de.dailab.nsm.semanticDistanceMeasures.DataExample;
import de.dailab.nsm.semanticDistanceMeasures.SynonymPair;

/**
 * Created by Johannes Fähndrich on 20.01.17 as part of his dissertation.
 */
public class MarkerPassingSemanticDistanceMeasureTest extends SemanticDistanceTest {



    MarkerPassingSemanticDistanceMeasure MarkerPassingSemanticDistanceMeasure = new MarkerPassingSemanticDistanceMeasure();



    @Override
    public void test() {
        for (DataExample p : testSynonymPairs) {
            try {
                SynonymPair pair = (SynonymPair)p;
                TestRunnable test = new TestRunnable() {
                    @Override
                    public void run() {
                        //spreadActivation(this.word1, this.wordType1, this.word2, this.wordType2, this.decompositionDepth);
                        pair.setResult(MarkerPassingSemanticDistanceMeasure.passMarker(pair.getWord(), WordType.NN, pair.getSynonym(), WordType.NN, MarkerPassingConfig.getDecompositionDepth(), MarkerPassingConfig.getStartActivation(), MarkerPassingConfig.getThreshold(), MarkerPassingConfig.getThreshold(), DoubleNodeWithMultipleThresholds.class));
                        System.out.println(pair.getWord() + ";" + pair.getSynonym() + ";" + pair.getResult() + ";" + pair.getResult());
                    }
                };
                test.setPair(pair);
                threadPoolExecutor.submit(test);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        threadPoolExecutor.shutdown();
        return;
    }


}
