/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

package de.kimanufaktur.nsm.decomposition.graph.spreadingActivation.MarkerPassing.ParameterLearner.ParameterLearner;

import de.kimanufaktur.nsm.decomposition.Concept;
import de.kimanufaktur.nsm.decomposition.Decomposition;
import de.kimanufaktur.nsm.decomposition.WordType;
import de.kimanufaktur.nsm.decomposition.graph.conceptCache.GraphUtil;
import org.jgrapht.Graph;

/**
 * Created by faehndrich on 24.02.16.
 */
public class SingleWordDecompositionTest {


    public static void main(String[] args) {
        int decompositionDepth= 2;
        WordType pos = WordType.NN;
        String word2Decompose;
        if (args.length > 0) {
            word2Decompose = args[0];
        } else {
            word2Decompose = "noon";
            pos = WordType.NN;
        }
        Decomposition decomposition = new Decomposition();
        decomposition.init();
        Concept concept = decomposition.multiThreadedDecompose(word2Decompose, pos, decompositionDepth);
        Graph g = GraphUtil.createJGraph(concept);
        GraphUtil.saveGraph(g,concept,decompositionDepth);

        word2Decompose = "midday";
        Concept concept2 = decomposition.multiThreadedDecompose(word2Decompose, pos, decompositionDepth);
        Graph g2 = GraphUtil.createJGraph(concept2);
        GraphUtil.saveGraph(g2,concept2,decompositionDepth);

        Graph merger = GraphUtil.mergeGraph(g,g2);
        Concept mergerConcept = new Concept("merger_midday_noon");
        GraphUtil.saveGraph(merger,mergerConcept,decompositionDepth);
    }



}
