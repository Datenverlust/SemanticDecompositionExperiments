/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

package de.dailab.nsm.tsdr;

import de.dailab.nsm.decomposition.Concept;
import de.dailab.nsm.decomposition.Decomposition;
import de.dailab.nsm.decomposition.WordType;
import de.dailab.nsm.decomposition.graph.conceptCache.GraphUtil;
import de.dailab.nsm.decomposition.graph.entities.marker.DoubleMarkerWithOrigin;
import de.dailab.nsm.decomposition.graph.entities.nodes.DoubleNodeWithMultipleThresholds;
import de.dailab.nsm.decomposition.graph.spreadingActivation.MarkerPassing.DoubleMarkerPassing;
import de.dailab.nsm.decomposition.graph.spreadingActivation.MarkerPassing.MarkerPassingConfig;
import de.tuberlin.spreadalgo.Marker;
import de.tuberlin.spreadalgo.Node;
import org.jgrapht.Graph;

import java.util.*;


class TopicSimilarityMeasure {

    // parameters
    private int decompositionDepth = MarkerPassingConfig.getDecompositionDepth();
    private double startActivation = MarkerPassingConfig.getStartActivation();
    private double threshold = MarkerPassingConfig.getThreshold();
    private double keyphraseScoreImpact = 0.1; // Must be between 0 and 1



    public List<Double> getTopicSimilarity(DocumentModel docModel1, DocumentModel docModel2) {

        docModel1.buildTopicGraph(decompositionDepth);
        docModel2.buildTopicGraph(decompositionDepth);
        docModel1.buildTopicConcepts();
        docModel2.buildTopicConcepts();

        DoubleMarkerPassing markerPassing = basicDoubleMarkerPassing(docModel1, docModel2);

        return evaluateDoubleMarkerPassingEvaluation(markerPassing, docModel1, docModel2);
    }


    /**
     * Basic version of MarkerPassing for determining the similarity of two topics graphs.
     */

    public DoubleMarkerPassing basicDoubleMarkerPassing(DocumentModel docModel1, DocumentModel docModel2) {
        Logger.outLn("Start marker passing");

        Map<Concept, Double> conceptThresholdMap = new HashMap<>();
        List<Map<Concept, List<? extends Marker>>> startActivation = new ArrayList<>();

        Map<Concept, List<? extends Marker>> conceptMarkerMap1 = createStartMarker(docModel1, conceptThresholdMap);
        startActivation.add(conceptMarkerMap1);

        Map<Concept, List<? extends Marker>> conceptMarkerMap2 = createStartMarker(docModel2, conceptThresholdMap);
        startActivation.add(conceptMarkerMap2);

        Graph mergedGraph = GraphUtil.mergeGraph(docModel1.getTopicGraph(), docModel2.getTopicGraph());

        //create marker passing algorithm
        DoubleMarkerPassing doubleMarkerPassing =
                new DoubleMarkerPassing(mergedGraph, conceptThresholdMap, DoubleNodeWithMultipleThresholds.class);
        DoubleMarkerPassing.doInitialMarking(startActivation, doubleMarkerPassing);
        doubleMarkerPassing.execute();

        Logger.outLn("End marker passing");

        return doubleMarkerPassing;
    }

    private Map<Concept, List<? extends Marker>> createStartMarker(DocumentModel docModel, Map<Concept, Double> conceptThresholdMap) {

        Map<Concept, List<? extends Marker>> conceptMarkerMap = new HashMap<>();

        for (ConceptScorePair csp : docModel.getTopicConceptList()) {
            Concept concept = csp.getConcept();

            List<Marker> markerList = new ArrayList<>();
            DoubleMarkerWithOrigin startMarker = new DoubleMarkerWithOrigin();

            // Keyphrase extraction score influences start activation level
            double startActivation = this.startActivation * (1 - this.keyphraseScoreImpact) + csp.getScore() * this.keyphraseScoreImpact;

            startMarker.setActivation(startActivation);
            startMarker.setOrigin(concept);
            markerList.add(startMarker);

            conceptMarkerMap.put(concept, markerList);
            conceptThresholdMap.put(concept, this.threshold);
        }

        return conceptMarkerMap;
    }

    List<Double> evaluateDoubleMarkerPassingEvaluation(DoubleMarkerPassing doubleMarkerPassing, DocumentModel docModel1, DocumentModel docModel2) {

        Logger.outLn("--- MarkerPassing Evaluation ---");

//        Logger.outLn("--- Topic Concepts for Document 1: " + docModel1.getDocID());
        List<Concept> topicConcepts1 = new ArrayList<>();
        for (ConceptScorePair csp : docModel1.getTopicConceptList()) {
//            Logger.outLn(csp.getConcept().getLitheral() + ": " + csp.getScore());
            topicConcepts1.add(csp.getConcept());
        }
//        Logger.outLn("--- Topic Concepts for Document 2: " + docModel2.getDocID());
        List<Concept> topicConcepts2 = new ArrayList<>();
        for (ConceptScorePair csp : docModel2.getTopicConceptList()) {
//            Logger.outLn(csp.getConcept().getLitheral() + ": " + csp.getScore());
            topicConcepts2.add(csp.getConcept());
        }

        Collection<Node> activeNodes = doubleMarkerPassing.getActiveNodes();
        Logger.outLn("- Count of activeNodes:            " + activeNodes.size());

        List<DoubleNodeWithMultipleThresholds> doubleActiveNodes = getDoubleActivation(activeNodes);
        Logger.outLn("- count of doubleActiveNodes:      " + doubleActiveNodes.size());
        //doubleActiveNodes.forEach(x -> System.out.println("doubleActiveNode: " + x.getConcept().getLitheral()));

        double rateOfDoubleActivation = (float) doubleActiveNodes.size() / activeNodes.size();
        Logger.outLn("- rateOfDoubleActivation:          " + rateOfDoubleActivation);

        double avgDoubleActivation = getAvgActivation(doubleActiveNodes);
        Logger.outLn("- averageDoubleActivation:         " + avgDoubleActivation);

        int countOforiginConcepsFired = getCountOfDistinctOriginConceptsFired(topicConcepts1, topicConcepts2);
        double rateOfOriginConcepIntersection = getRateOfOriginConceptIntersection(topicConcepts1, topicConcepts2, countOforiginConcepsFired);

        Logger.outLn("- distinctOriginConceptsFired:     " + countOforiginConcepsFired);
        Logger.outLn("- rateOfOriginConceptIntersection: " + rateOfOriginConcepIntersection);

        double avgOriginStartActivation = 0;
        for (ConceptScorePair csp : docModel1.getTopicConceptList()) {
            avgOriginStartActivation += csp.getScore();
        }
        for (ConceptScorePair csp : docModel2.getTopicConceptList()) {
            avgOriginStartActivation += csp.getScore();
        }
        avgOriginStartActivation /= (topicConcepts1.size() + topicConcepts2.size());
        avgOriginStartActivation = this.startActivation * (1 - this.keyphraseScoreImpact) + avgOriginStartActivation * this.keyphraseScoreImpact;
        Logger.outLn("- avgOriginStartActivation:        " + avgOriginStartActivation);

//        double rateOfActivation= (avgDoubleActivation / (countOforiginConcepsFired * avgOriginStartActivation));
        double rateOfActivation = avgDoubleActivation / avgOriginStartActivation;
        Logger.outLn("- rateOfActivation: 		           " + rateOfActivation);

        List<Double> results = new ArrayList<>();
        results.add((double) activeNodes.size());
        results.add((double) doubleActiveNodes.size());
        results.add(rateOfDoubleActivation);
        results.add(avgDoubleActivation);
        results.add(avgOriginStartActivation);
        results.add(rateOfActivation);
        results.add((double) countOforiginConcepsFired);
        results.add(rateOfOriginConcepIntersection);

        return results;
    }

    public List<DoubleNodeWithMultipleThresholds> getDoubleActivation(Collection<Node> activeNodes) {
        List<DoubleNodeWithMultipleThresholds> doubleActiveNodes = new ArrayList<>();
        for (Node node : activeNodes) {
            if (node instanceof DoubleNodeWithMultipleThresholds) {
                DoubleNodeWithMultipleThresholds doubleNodeWithMultipleThresholds = (DoubleNodeWithMultipleThresholds) node;
                Concept concept = doubleNodeWithMultipleThresholds.getConcept();
                if (doubleNodeWithMultipleThresholds.getActivation().size() > 1 && !Decomposition.getConcepts2Ignore().contains(concept.getLitheral())) {
                    doubleActiveNodes.add(doubleNodeWithMultipleThresholds);
                }
            }
        }
        return doubleActiveNodes;
    }

    /**
     * calculate the average activation of all nodes. Given active nodes (nodes containing markers)
     *
     * @param activeNodes the activated node which contain markers.
     * @return the average double activation of all given nodes.
     */
    public double getAvgActivation(List<DoubleNodeWithMultipleThresholds> activeNodes) {
        double avgActivation = 0.0;
        if (activeNodes.size() > 0) {
            for (DoubleNodeWithMultipleThresholds node : activeNodes) {
                for (Concept concept : node.getActivation().keySet()) {
                    if (!node.getActivation(concept).isEmpty()) {
                        //activation over all concepts for current node
                        //TODO: was this intended? needed to refactor this code because old used method signatures where incorrect
                        avgActivation += node.getActivation(concept).keySet().stream().mapToDouble(D -> D.doubleValue()).sum();
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
     * Calculate the number of number of origin concept which where used during marker passing.
     *
     * @param originConceptsSentence1
     * @param originConceptsSentence2
     * @return
     */
    private int getCountOfDistinctOriginConceptsFired(List<Concept> originConceptsSentence1, List<Concept> originConceptsSentence2) {

        HashSet<Concept> distinctConcepts = new HashSet<>(originConceptsSentence1);
        distinctConcepts.addAll(originConceptsSentence2);

        return distinctConcepts.size();
    }

    private double getRateOfOriginConceptIntersection(List<Concept> originConceptsSentence1,
                                                     List<Concept> originConceptsSentence2, int originConceptsFired) {

        double conceptIntersection = (originConceptsSentence1.size() + originConceptsSentence2.size()) - originConceptsFired;

        double rateOfIntersection = conceptIntersection / ((originConceptsSentence1.size() + originConceptsSentence2.size()));

        return rateOfIntersection;
    }
}