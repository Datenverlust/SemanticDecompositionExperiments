/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

package test;
import de.kimanufaktur.markerpassing.Marker;
import de.kimanufaktur.nsm.decomposition.Concept;
import de.kimanufaktur.nsm.decomposition.Decomposition;
import de.kimanufaktur.nsm.decomposition.Definition;
import de.kimanufaktur.nsm.decomposition.graph.conceptCache.GraphUtil;
import de.kimanufaktur.nsm.decomposition.graph.edges.WeightedEdge;
import de.kimanufaktur.nsm.decomposition.graph.spreadingActivation.MarkerPassing.MarkerPassingConfig;
import de.kimanufaktur.nsm.reasoning.*;
import org.jgrapht.Graph;
import org.jgrapht.graph.ListenableDirectedGraph;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SciQTest {
    private Decomposition decomposition;
    private Definition answerDefinition;
    private Definition definition;
    Map<Concept, List<? extends Marker>> conceptMarkerMap = new HashMap<>();
    Map<Concept, Double> threshold;
    Graph graph;
    List<Graph> graphs;
    Graph mergedGraph;
    PathMarkerPassing markerPassing;
    List<Map<Concept, List<? extends Marker>>> startActivation;
    int originalConcepts = 0;
    private int nAnswerCount;
    private int questions = 1;
    private boolean correctAnswerChoosen;
    private int solved = 0;
    private int solvedCorrect = 0;
    private int multiOptions =0;
    private int multiOptionsCorrect = 0;
    private int guessed = 0;
    private int guessedCorrect = 0;
    private int nAbductive=0;
    private boolean bAbductive=false;
    private int abductiveSolvedCorrect=0;

    @Before
    public void buildUp() {

        Decomposition decomposition = new Decomposition();
        decomposition.init();

        SciQChallengeDataSet dataSet = new SciQChallengeTrainDataSet();
        Collection<SciQChallengeQuestion> result = dataSet.ReadExampleDataSet();


        for (SciQChallengeQuestion sciQChallengeQuestion : result) {
            try {
                graphs = new ArrayList<>();
                threshold = new HashMap<>();
                Map<Concept, List<? extends Marker>> conceptMarkerMap = new HashMap<>();
                correctAnswerChoosen = false;

                definition = new Definition(sciQChallengeQuestion.getQuestion());
                //question decomposition
                for (Concept word : definition.getDefinition()) {
                    if (!Decomposition.getConcepts2Ignore().contains(word)) {
                        graph = GraphUtil.getGraph(word.getLitheral(), word.getWordType(), MarkerPassingConfig.getDecompositionDepth());
                        graphs.add(graph);
                        List<Marker> markers1 = new ArrayList<>();
                        Concept activeNode = word;
                        PathMarker startMarker = new PathMarker(activeNode);
                        markers1.add(startMarker);
                        conceptMarkerMap.put(activeNode, markers1);
                        threshold.put(activeNode, MarkerPassingConfig.getThreshold());
                        originalConcepts++;
                    }
                }

                nAnswerCount = 1;
                List<Concept> answerConcepts = new ArrayList<>(4);
                for (String answer : sciQChallengeQuestion.getAnswers().keySet()) {
                    answerDefinition = new Definition(answer);
                    //answer decomposition
                    for (Concept word : answerDefinition.getDefinition()) {
                        if (!Decomposition.getConcepts2Ignore().contains(word)) {
                            graph = GraphUtil.getGraph(word.getLitheral(), word.getWordType(), MarkerPassingConfig.getDecompositionDepth());
                            graphs.add(graph);
                            List<Marker> markers1 = new ArrayList<>();
                            Concept activeNode = word;
                            PathMarker startMarker = new PathMarker(activeNode);
                            startMarker.setStartsAtAnswer(true);
                            startMarker.setAnswerNo(nAnswerCount);
                            markers1.add(startMarker);
                            conceptMarkerMap.put(activeNode, markers1);
                            threshold.put(activeNode, MarkerPassingConfig.getThreshold());
                            originalConcepts++;
                        }
                    }
                    nAnswerCount++;
                }

                mergedGraph = new ListenableDirectedGraph(WeightedEdge.class);
                for (Graph wordGraph :
                        graphs) {
                    mergedGraph = GraphUtil.mergeGraph(mergedGraph, wordGraph);
                }
                startActivation = new ArrayList<>();
                startActivation.add(conceptMarkerMap);

                markerPassing = new PathMarkerPassing(mergedGraph, threshold, PathNode.class);
                markerPassing.doInitialMarking(startActivation, markerPassing);
                markerPassing.setQuestionNodes();
                markerPassing.execute();


                int answerNumber= markerPassing.getCorrectAnswerNummer();

                if (markerPassing.abductiveReasoning>0) {
                    bAbductive = true;
                    nAbductive++;
                }else {
                    bAbductive = false;
                    if (markerPassing.bResultIsGuessed)
                        guessed++;
                    else if (markerPassing.bResultIsGuessedOfMultipleOptions)
                        multiOptions++;
                    else
                        solved++;
                }


                //TODO: bessere Lösung als "QuestionString.contains(AnswerString)" finden...
                if (sciQChallengeQuestion.getCorrect_answer().toLowerCase().contains(markerPassing.getCorrectAnswer().toLowerCase())) {
                    if (markerPassing.bResultIsGuessed)
                        guessedCorrect++;
                    else if (markerPassing.bResultIsGuessedOfMultipleOptions)
                        multiOptionsCorrect++;
                    else if (bAbductive)
                        abductiveSolvedCorrect++;
                    else
                        solvedCorrect++;
                }

                if(!markerPassing.bResultIsGuessed) {
                    System.out.println("--------------------- \n");
                    System.out.println("Frage : " + questions);
                    System.out.println("    Iterationen: " + markerPassing.roundCount);
                    System.out.println("    Ermittelte Antwort: " + answerNumber +
                            " Marker-Ursprung: " + markerPassing.getCorrectAnswer());
                    System.out.println("tatsaechliche Antwort: " + sciQChallengeQuestion.getCorrect_answer() );
                    System.out.println("    ");
/*                    System.out.println("    - guessed :" + guessed);
                    System.out.println("        - correctly :" + guessedCorrect);
                    System.out.println("    - multiple Options :" + multiOptions);
                    System.out.println("        - correctly :" + multiOptionsCorrect);
                    System.out.println("    - solved :" + solved);
                    System.out.println("        - correctly :" + solvedCorrect);
*/                    System.out.println("    - abductive :" + nAbductive);
                    System.out.println("        - correctly :" + abductiveSolvedCorrect);
                    System.out.println("");
//                    System.out.println(markerPassing.printInferences());
                }
                questions++;
            }catch (Exception e){
                System.out.println(e.getMessage());
            }
        }
    }

    //@Test
    public void toTestCorrectQuestionBuild() {

        PathMarkerPassing markerPassing = new PathMarkerPassing(mergedGraph, threshold, PathNode.class);
        markerPassing.doInitialMarking(startActivation, markerPassing);
        markerPassing.execute();

        System.out.println("Iterationen: " + markerPassing.roundCount);
        System.out.println("Korrekte Antwort:" + markerPassing.getCorrectAnswerNummer());
        assertTrue(markerPassing.roundCount > 0);
        assertTrue(markerPassing.getCorrectAnswerNummer() > 0);
    }

    @Test
    public void testMainRoutine() {

    }
}
