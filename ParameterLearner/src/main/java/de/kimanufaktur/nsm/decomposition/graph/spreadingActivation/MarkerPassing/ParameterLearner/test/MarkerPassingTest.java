/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

package de.kimanufaktur.nsm.decomposition.graph.spreadingActivation.MarkerPassing.ParameterLearner.test;

import de.kimanufaktur.markerpassing.Marker;
import de.kimanufaktur.markerpassing.Node;
import de.kimanufaktur.nsm.decomposition.Concept;
import de.kimanufaktur.nsm.decomposition.Decomposition;
import de.kimanufaktur.nsm.decomposition.WordType;
import de.kimanufaktur.nsm.decomposition.graph.conceptCache.GraphUtil;
import de.kimanufaktur.nsm.decomposition.graph.spreadingActivation.DoubleSpreadingActivation.DoubleSpreadingActivation;
import de.kimanufaktur.nsm.decomposition.graph.spreadingActivation.MarkerPassing.DoubleMarkerPassing;
import de.kimanufaktur.nsm.decomposition.graph.spreadingActivation.MarkerPassing.ParameterLearner.ParameterLearner.SemanticDistanceTest;
import de.kimanufaktur.nsm.decomposition.graph.spreadingActivation.MarkerPassing.ParameterLearner.TestRunnable;
import de.kimanufaktur.nsm.graph.entities.marker.DoubleMarkerWithOrigin;
import de.kimanufaktur.nsm.graph.entities.nodes.DoubleNodeWithMultipleThresholds;
import de.kimanufaktur.nsm.semanticDistanceMeasures.DataExample;
import de.kimanufaktur.nsm.semanticDistanceMeasures.SimilarityPair;
import org.jgrapht.Graph;
import org.jgrapht.graph.GraphUnion;

import java.util.*;

/**
 * Created by faehndrich on 25.11.15.
 */
public class MarkerPassingTest extends SemanticDistanceTest {

    public static void main(String[] args) {
        MarkerPassingTest test = new MarkerPassingTest();
        test.test();
    }

    /**
     * Example implementation of a sreading activation algorithm using the marker passing published by Fähndrich et al.
     *
     * @param resultGraph     this is the graph the activation spreading takes place on.
     * @param startActivation the markers in the beginning of the algorithem. Please notice that there should be at least one
     *                        threshold reached. Else there will be no passing of markers.
     * @param threshold       the threshold from which an node is active.
     * @return the graph given as input but now with markern on top, representing the result of the activation spreading algorithm.
     */
    private static DoubleSpreadingActivation getDoubleSpreadingActivation(Graph resultGraph, List<Map<Concept, List<Marker>>> startActivation, double threshold) {
        DoubleSpreadingActivation doubleSpreadingActivationAlgo = new DoubleSpreadingActivation(resultGraph, threshold);
        for (Map<Concept, List<Marker>> m : startActivation) {
            for (Map.Entry<Concept, List<Marker>> e : m.entrySet()) {
                for (Marker marker : e.getValue()) {
                    doubleSpreadingActivationAlgo.addMarkerToNode(e.getKey(), marker);
                }
            }
        }
        doubleSpreadingActivationAlgo.execute();
        return doubleSpreadingActivationAlgo;
    }

    /**
     * Use a marker passing where which marker has a double as data. This is quite similar to activation spreading.
     *
     * @param resultGraph     this is the graph the marker passing takes place on.
     * @param startActivation the markers in the beginning of the algorithem. Please notice that there should be at least one
     *                        threshold reached. Else there will be no passing of markers.
     * @param threshold       the threshold from which an node is active.
     * @return the graph given as input but now with markern on top, representing the result of the marker passing algorithm.
     */
    public <T extends DoubleNodeWithMultipleThresholds> DoubleMarkerPassing getDoubleMarkerPassing(Graph resultGraph, List<Map<Concept, List<? extends Marker>>> startActivation, Map<Concept, Double> threshold, Class<T> nodeType) {
        DoubleMarkerPassing doubleMarkerPassingAlgo = new DoubleMarkerPassing(resultGraph, threshold, nodeType);
        DoubleMarkerPassing.doInitialMarking(startActivation, doubleMarkerPassingAlgo);
        doubleMarkerPassingAlgo.execute();
        return doubleMarkerPassingAlgo;
    }



    /**
     * Speading activation test. Here the marker passing is used with a data in the marker as double. This simulates a
     * "normal" activation spreading. This is to show the subsumption of activation spreading by marker passing
     * @param word1 the fist word to multiThreadedDecompose
     * @param wordType1 the POS of the first word
     * @param word2 the second word to multiThreadedDecompose
     * @param wordType2 the POS of the second word
     * @param decompositionDepth the decomposition depth with which the words should be decomposed
     * @return the activated graph after the activation spreading as reached its termination condition.
     */
    public Graph spreadActivation(String word1, WordType wordType1, String word2, WordType wordType2, int decompositionDepth) {
        Graph graphword1 = GraphUtil.getGraph(word1, wordType1, decompositionDepth);
        Graph graphword2 = GraphUtil.getGraph(word2, wordType2, decompositionDepth);
        GraphUnion commonGraph = new GraphUnion(graphword1, graphword2);

        DoubleSpreadingActivation doubleSpreadingActivationAlgo = new DoubleSpreadingActivation(commonGraph, 1.0);
        doubleSpreadingActivationAlgo.fillNodes(commonGraph, 1.0);
        //TODO: implement activation sprading with chosen parameters.

        return commonGraph;
    }


    /**
     * Marker passing, given two concepts and their POS this function creates two decompositions of the given
     * decomposition depth, merges the two graphs and activates over them.
     * @param word1 the fist word to multiThreadedDecompose
     * @param wordType1 the POS of the first word
     * @param word2 the second word to multiThreadedDecompose
     * @param wordType2 the POS of the second word
     * @param decompositionDepth the decomposition depth with which the words should be decomposed
     * @return the average activation found with dounble activation, as an measure of distance.
     */
    public <T extends DoubleNodeWithMultipleThresholds> double passMarker(String word1, WordType wordType1, String word2, WordType wordType2, int decompositionDepth, double startActivationLevel,double thresholdNode1, double thresholdNode2,  Class<T> nodeType) {
        //Get Decompositions
        Graph graphword1 = GraphUtil.getGraph(word1, wordType1, decompositionDepth);
        Graph graphword2 = GraphUtil.getGraph(word2, wordType2, decompositionDepth);
        if (graphword1.equals(graphword2)) {
            return 1.0;
        }

        //Merge the two graphs
        //GraphUnion commonGraph = new GraphUnion(graphword1, graphword2); //TODO: replace this crappy implementation: The first concept found is used in a set.. which has the effect that the concepts are overwirtten.
        //Graphs.addGraph(graphword1,graphword2);
        Graph commonGraph = GraphUtil.mergeGraph(graphword1, graphword2);
        //Create Marker Passsing configuration
        //create start marker1
        Concept activeNode1 = (Concept) graphword1.vertexSet().toArray()[0];
        DoubleMarkerWithOrigin startMarker1 = new DoubleMarkerWithOrigin();
        startMarker1.setActivation(startActivationLevel);
        startMarker1.setOrigin(activeNode1);
        List<Marker> markers1 = new ArrayList<>();
        markers1.add(startMarker1);
        //create start marker2
        Concept activeNode2 = (Concept) graphword2.vertexSet().toArray()[0];
        DoubleMarkerWithOrigin startMarker2 = new DoubleMarkerWithOrigin();
        startMarker2.setActivation(startActivationLevel);
        startMarker2.setOrigin(activeNode2);
        List<Marker> markers2 = new ArrayList<>();
        markers2.add(startMarker2);
        //set start markers
        List<Map<Concept, List<? extends Marker>>> startActivation = new ArrayList<>();
        Map<Concept, List<? extends Marker>> conceptMarkerMap = new HashMap<>();
        //add to marker concept map
        conceptMarkerMap.put(activeNode1, markers1);
        conceptMarkerMap.put(activeNode2, markers2);
        startActivation.add(conceptMarkerMap);
        //set thresholds
        Map<Concept, Double> threshold = new HashMap<>(2);
        threshold.put(activeNode1, thresholdNode1);
        threshold.put(activeNode2, thresholdNode2);
        //create algorithm
        DoubleMarkerPassing doubleMarkerPassing = getDoubleMarkerPassing(commonGraph, startActivation, threshold, nodeType);
        doubleMarkerPassing.fillNodes(commonGraph, threshold, nodeType);
        Collection<Node> activeNodes = doubleMarkerPassing.getActiveNodes();

        List<DoubleNodeWithMultipleThresholds> doubleActiveNodes = getDoubleActivation(activeNodes);
        double avgActivation = getAvgActivation(doubleActiveNodes);
        return avgActivation / (2 * startActivationLevel);
    }

    /**
     * calculate the average activation of all nodes. Given activ nodes (nodes containing markers)
     * @param activeNodes the activated node which contain markers.
     * @return the average double activation of all given nodes.
     */
    public double getAvgActivation(List<DoubleNodeWithMultipleThresholds> activeNodes) {
        double avgActivation = 0.0;
        double sum = 0.0;
        if (activeNodes.size() > 0) {
            for (DoubleNodeWithMultipleThresholds node : activeNodes) {
                for (Concept concept : node.getActivation().keySet()) {
                    if (!node.getActivation(concept).isEmpty()) {
                        //activation over all concepts for current node
                        //TODO: was this intended? needed to refactor this code because old used method signatures where incorrect
                        avgActivation += node.getActivation(concept).keySet().stream().mapToDouble(D->D.doubleValue()).sum();
                    }
                }
            }
            avgActivation = avgActivation / activeNodes.size();
            return avgActivation;
        } else {
            return 0.0;
        }

    }

    /**
     * Helper method which gets all markers of nodes which have been activated by at least two sources.
     * This method is thought for the decomposition to be run before, because we use the list of ignored concepts, to
     * filter unwanted nodes.
     *
     * @param activeNodes The active nodes which should be analyzed.
     * @return a list of Node which have been activated by at least two sources.
     */
    public List<DoubleNodeWithMultipleThresholds> getDoubleActivation(Collection<Node> activeNodes) {
        List<DoubleNodeWithMultipleThresholds> doubleActiveNodes = new ArrayList<>();
        for (Node node : activeNodes) {
            if (node instanceof DoubleNodeWithMultipleThresholds) {
            	
            	// 4 testing
            	System.out.println("getDoubleActivation active nodes are: " + ((DoubleNodeWithMultipleThresholds) node).getConcept().getLitheral());
               
            	DoubleNodeWithMultipleThresholds doubleNodeWithMultipleThresholds = (DoubleNodeWithMultipleThresholds) node;
                if (doubleNodeWithMultipleThresholds.getActivation().size() > 1 && !Decomposition.getConcepts2Ignore().contains(doubleNodeWithMultipleThresholds.getConcept().getLitheral())) {
                    doubleActiveNodes.add(doubleNodeWithMultipleThresholds);
                }
            }
        }
        return doubleActiveNodes;
    }

    @Override
    public void test() {
        for (DataExample p : testSynonymPairs) {
            try {
                SimilarityPair pair = (SimilarityPair) p;
                TestRunnable test = new TestRunnable() {
                    int decompositionDepth = 1;
                    @Override
                    public void run() {
                        //spreadActivation(this.word1, this.wordType1, this.word2, this.wordType2, this.decompositionDepth);
                        pair.setResult(passMarker(pair.getString1(), WordType.NN, pair.getString2(), WordType.NN, decompositionDepth, 100, 2.0, 2.0, DoubleNodeWithMultipleThresholds.class));
                        System.out.println(pair.getString1() + ";" + pair.getString2() + ";" + pair.getResult() + ";" + pair.getResult());
                    }
                };
                test.setPair(pair);
                threadPoolExecutor.submit(test);
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
        threadPoolExecutor.shutdown();
        return;
    }
}

