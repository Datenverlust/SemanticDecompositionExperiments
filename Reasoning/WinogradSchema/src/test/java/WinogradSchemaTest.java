/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

import de.kimanufaktur.nsm.decomposition.Concept;
import de.kimanufaktur.nsm.decomposition.Decomposition;
import de.kimanufaktur.nsm.decomposition.Definition;
import de.kimanufaktur.nsm.decomposition.graph.conceptCache.GraphUtil;
import de.kimanufaktur.nsm.decomposition.graph.edges.WeightedEdge;
import de.kimanufaktur.nsm.decomposition.graph.entities.marker.DoubleMarkerWithOrigin;
import de.kimanufaktur.nsm.decomposition.graph.entities.nodes.DoubleNodeWithMultipleThresholds;
import de.kimanufaktur.nsm.decomposition.graph.spreadingActivation.MarkerPassing.DoubleMarkerPassing;
import de.kimanufaktur.nsm.decomposition.graph.spreadingActivation.MarkerPassing.MarkerPassingConfig;
import de.tuberlin.spreadalgo.Marker;
import de.tuberlin.spreadalgo.Node;
import org.jgrapht.Graph;
import org.jgrapht.graph.ListenableDirectedGraph;

import java.util.*;

/**
 * Created by faehndrich on 30.09.16.
 */
public class WinogradSchemaTest {


    public static void main(String args[]) {
        Decomposition decomposition = new Decomposition();
        decomposition.init();

        WinogradSchemaDataSet dataSet = new WinogradSchemaTrainDataSet();
        Collection<WinogradSchemaQuestion> result = dataSet.ReadExampleDataSet();
        Iterator inter = result.iterator();


        Map<Concept, List<? extends Marker>> conceptMarkerMap = new HashMap<>();
        Map<Concept, Double> threshold = new HashMap<>();

        //Get all graphs for the sentence
        List<Graph> graphs = new ArrayList<>();
        for (WinogradSchemaQuestion winogradSchemaQuestion : result) {
            // WinogradSchemaQuestion winogradSchemaQuestion = (WinogradSchemaQuestion) inter.next();


            //Create Sentence graph
            Definition sentence = new Definition(winogradSchemaQuestion.sentence);
            for (Concept word : sentence.getDefinition()) {
                graphs.add(GraphUtil.getGraph(word.getLitheral(), word.getWordType(), MarkerPassingConfig.getDecompositionDepth()));
                // create start marker for sentence
                if (!Decomposition.getConcepts2Ignore().contains(word)) {
                    List<Marker> markers1 = new ArrayList<>();
                    Concept activeNode = word;
                    DoubleMarkerWithOrigin startMarker = new DoubleMarkerWithOrigin();
                    startMarker.setActivation(MarkerPassingConfig.getStartActivation());
                    startMarker.setOrigin(activeNode);
                    markers1.add(startMarker);
                    conceptMarkerMap.put(activeNode, markers1);
                    threshold.put(activeNode, MarkerPassingConfig.getThreshold());
                }
            }
            //Create answer concepts
            List<Concept> answerConcepts = new ArrayList<>(2);
            for (String answer : winogradSchemaQuestion.answers.keySet()) {
                Definition answerSentence = new Definition(answer);
                for (Concept word : answerSentence.getDefinition()) {
                    Graph answergraph = GraphUtil.getGraph(word.getLitheral(), word.getWordType(), MarkerPassingConfig.getDecompositionDepth());
                    if (!answergraph.vertexSet().isEmpty()) {
                        answerConcepts.add(word);
                        graphs.add(answergraph);
                    }
                    // create start marker for sentence
                    if (!Decomposition.getConcepts2Ignore().contains(word)) {
                        List<Marker> markers1 = new ArrayList<>();
                        Concept activeNode = word;
                        DoubleMarkerWithOrigin startMarker = new DoubleMarkerWithOrigin();
                        startMarker.setActivation(MarkerPassingConfig.getStartActivation() * 10);
                        startMarker.setOrigin(activeNode);
                        markers1.add(startMarker);
                        conceptMarkerMap.put(activeNode, markers1);
                        threshold.put(activeNode, MarkerPassingConfig.getThreshold());
                    }
                }
            }

            //merge all graphs
            Graph mergedGraph = new ListenableDirectedGraph(WeightedEdge.class);
            for (Graph wordGraph :
                    graphs) {
                mergedGraph = GraphUtil.mergeGraph(mergedGraph, wordGraph);
            }


            //do marker passing
            //configure start markers
            //set start markers
            List<Map<Concept, List<? extends Marker>>> startActivation = new ArrayList<>();
            startActivation.add(conceptMarkerMap);

            //create marker passing algorithm
            DoubleMarkerPassing doubleMarkerPassing = new DoubleMarkerPassing(mergedGraph, threshold, DoubleNodeWithMultipleThresholds.class);
            DoubleMarkerPassing.doInitialMarking(startActivation, doubleMarkerPassing);
            doubleMarkerPassing.execute();

        /*TypedMarkerPassingConfig config = new TypedMarkerPassingConfig();
        TypedMarkerPassing markerPassing = new TypedMarkerPassing(mergedGraph, config);
        markerPassing.start();*/
            //analyse results
            Collection<Node> activeNodes = doubleMarkerPassing.getActiveNodes();
            //Map<Concept, Double> doubleactivation = doubleMarkerPassing.getDoubleActivation();

            Map<Concept, Double> sumOfActivation = new HashMap<>(2);
            for (Concept answer : answerConcepts
                    ) {
                sumOfActivation.put(answer, 0.0d);
            }
            for (Node activeNode : activeNodes) {
                for (Marker m : activeNode.getMarkers()) {
                    DoubleMarkerWithOrigin marker = ((DoubleMarkerWithOrigin) m);
                    if (answerConcepts.contains(marker.getOrigin())) {
                        sumOfActivation.put(marker.getOrigin(), sumOfActivation.get(marker.getOrigin()) + marker.getActivation());
                    }
                }

            }
            for (String answer : winogradSchemaQuestion.answers.keySet()) {
                if (winogradSchemaQuestion.answers.get(answer)) {
                    System.out.println("Right answer is: " + answer);
                }
            }
            for (Concept answer : sumOfActivation.keySet()) {
                System.out.println(answer.getLitheral() + ";" + sumOfActivation.get(answer));
            }
        }
    }


}
