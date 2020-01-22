/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

package de.kimanufaktur.nsm.wsd;

import de.kimanufaktur.markerpassing.Node;
import de.kimanufaktur.nsm.decomposition.Concept;
import de.kimanufaktur.nsm.decomposition.Decomposition;
import de.kimanufaktur.nsm.decomposition.graph.conceptCache.GraphUtil;
import de.kimanufaktur.nsm.decomposition.graph.spreadingActivation.MarkerPassing.TypedMarkerPassing;
import de.kimanufaktur.nsm.graph.spreadingActivation.MarkerPassing.TypedMarkerPassingConfig;
import org.jgrapht.Graph;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by root on 11.02.16.
 */
public class TypedMarkerPassingTest {

    Decomposition decomposition = new Decomposition();
    int decompositionDepth = 2;
    ArrayList<Concept> startConcepts = new ArrayList<>();
    public static void main(String[] args) {
        TypedMarkerPassingTest test = new TypedMarkerPassingTest();
        test.test();
    }

    private void test() {
        decomposition.init();

        SensevalData testData = SensevalData.get(); // This object holds the senseval data set

        //SVSentence sentence = testData.getRandomSentence();
        SVSentence sentence = testData.getSentence(96);

        // Create the Semantic Network
        ArrayList<Concept> decompositions = new ArrayList<Concept>();
        Graph sentenceGraph = null;
        boolean first = true;
        int counter = 0;
        for (SVWord word : sentence.getWords()) {
            System.out.println(++counter + " : " + word.getDisambiguationSubject() + " " + word.getPOS() + " " + word.getWordType().toString());
            System.out.println("\tdecomposing...");
            Concept concept = decomposition.multiThreadedDecompose(word.getDisambiguationSubject()
                    , word.getWordType(), decompositionDepth);
            System.out.println("\tcomplete...");
            System.out.println("\tgraph...");
            Graph graph = GraphUtil.createJGraph(concept);
            startConcepts.add((Concept) graph.vertexSet().toArray()[0]);
            System.out.println("\tsize:" + graph.vertexSet().size());
            System.out.println("\tcomplete...");
            decompositions.add(concept);
            if (first) {
                sentenceGraph = graph;
                first = !first;
            } else {
                System.out.println("\tmerging...");
                sentenceGraph = GraphUtil.mergeGraph(sentenceGraph, graph);
                System.out.println("\tcomplete...");
            }
        }

        TypedMarkerPassingConfig config = new TypedMarkerPassingConfig();
        TypedMarkerPassing markerPassing = new TypedMarkerPassing(sentenceGraph, config);
        int i = 0;
        markerPassing.start();
        Collection<Node> activeNodes = markerPassing.getActiveNodes();

    }


}
