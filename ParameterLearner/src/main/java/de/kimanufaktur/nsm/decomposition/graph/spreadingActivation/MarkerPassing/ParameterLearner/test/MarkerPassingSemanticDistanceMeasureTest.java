/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

package de.kimanufaktur.nsm.decomposition.graph.spreadingActivation.MarkerPassing.ParameterLearner.test;


import de.kimanufaktur.nsm.decomposition.WordType;
import de.kimanufaktur.nsm.decomposition.graph.spreadingActivation.MarkerPassing.MarkerPassingConfig;
import de.kimanufaktur.nsm.decomposition.graph.spreadingActivation.MarkerPassing.MarkerPassingSemanticDistanceMeasure;
import de.kimanufaktur.nsm.decomposition.graph.spreadingActivation.MarkerPassing.ParameterLearner.ParameterLearner.SemanticDistanceTest;
import de.kimanufaktur.nsm.decomposition.graph.spreadingActivation.MarkerPassing.ParameterLearner.TestRunnable;
import de.kimanufaktur.nsm.graph.entities.nodes.DoubleNodeWithMultipleThresholds;
import de.kimanufaktur.nsm.semanticDistanceMeasures.DataExample;
import de.kimanufaktur.nsm.semanticDistanceMeasures.SimilarityPair;

/**
 * Created by Johannes Fähndrich on 20.01.17 as part of his dissertation.
 */
public class MarkerPassingSemanticDistanceMeasureTest extends SemanticDistanceTest {



    de.kimanufaktur.nsm.decomposition.graph.spreadingActivation.MarkerPassing.MarkerPassingSemanticDistanceMeasure MarkerPassingSemanticDistanceMeasure = new MarkerPassingSemanticDistanceMeasure();



    @Override
    public void test() {
        for (DataExample p : testSynonymPairs) {
            try {
                SimilarityPair pair = (SimilarityPair) p;
                TestRunnable test = new TestRunnable() {
                    @Override
                    public void run() {
                        //spreadActivation(this.word1, this.wordType1, this.word2, this.wordType2, this.decompositionDepth);
                        pair.setResult(MarkerPassingSemanticDistanceMeasure.passMarker(pair.getString1(), WordType.NN, pair.getString2(), WordType.NN, MarkerPassingConfig.getDecompositionDepth(), MarkerPassingConfig.getStartActivation(), MarkerPassingConfig.getThreshold(), MarkerPassingConfig.getThreshold(), DoubleNodeWithMultipleThresholds.class));
                        System.out.println(pair.getString1() + ";" + pair.getString2() + ";" + pair.getResult() + ";" + pair.getResult());
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
