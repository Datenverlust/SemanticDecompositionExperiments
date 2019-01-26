/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

package de.dailab.nsm.decomposition.graph.spreadingActivation.MarkerPassing.ParameterLearner;

import de.dailab.nsm.decomposition.Decomposition;
import de.dailab.nsm.decomposition.WordType;
import de.dailab.nsm.decomposition.graph.SemanticNetworkVisualizer;
import de.dailab.nsm.decomposition.graph.conceptCache.GraphUtil;
import de.dailab.nsm.decomposition.graph.edges.WeightedEdge;
import de.dailab.nsm.decomposition.graph.spreadingActivation.MarkerPassing.MarkerPassingConfig;
import de.dailab.nsm.semanticDistanceMeasures.DataExample;
import de.dailab.nsm.semanticDistanceMeasures.SimilarityPair;
import de.dailab.nsm.semanticDistanceMeasures.data.WordSimilarityDataSet;
import org.jgrapht.Graph;
import org.jgrapht.graph.ListenableDirectedGraph;

import javax.swing.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by faehndrich on 25.11.15.
 */
public abstract class SemanticDistanceTest {
    static MarkerPassingConfig markerPassingConfig = new MarkerPassingConfig();
    static SemanticNetworkVisualizer graphVirtualizer;
    protected List<DataExample> testSynonymPairs = new ArrayList<>();
    protected ExecutorService threadPoolExecutor = Executors.newFixedThreadPool(1);//Runtime.getRuntime().availableProcessors());
    Collection<WordSimilarityDataSet> datasets = new ArrayList<>();

    public SemanticDistanceTest() {
        /*
        Load de.dailab.nsm.semanticDistanceMeasures.data sets
         */
//        WordSim353DataSet wordSim353DataSet = new WordSim353DataSet();
//        datasets.add(wordSim353DataSet);
//        Rubenstein1965Dataset rubenstein1965Dataset = new Rubenstein1965Dataset();

//        datasets.add(rubenstein1965Dataset);
//        MENDataSet menDataSet = new MENDataSet();
//        datasets.add(menDataSet);
//        MtrukDataSet mtrukDataSet = new MtrukDataSet();
//        datasets.add(mtrukDataSet);
//        StanfordRareWordSimilarityDataset stanfordRareWordSimilarityDataset = new StanfordRareWordSimilarityDataset();
//        datasets.add(stanfordRareWordSimilarityDataset);

        for (WordSimilarityDataSet dataSet : datasets) {
            testSynonymPairs.addAll(dataSet.ReadExampleDataSet());
        }
        /*
        Sort the synonym pairs so that they are sortet from more likely to less likely.
         */
        Collections.sort((List) testSynonymPairs, new Comparator<SimilarityPair>() {
            @Override
            public int compare(SimilarityPair o1, SimilarityPair o2) {
                if (o1.getResult() - o2.getResult() < 0) {
                    return 1;
                } else if (o1.getResult() == o2.getResult()) {
                    return 0;
                } else {
                    return -1;
                }
            }
        });

        //graphVirtualizer = new SemanticNetworkVisualizer();
        Decomposition.init();
    }

    /**
     * draw the given graph in a JFrame. This is a first test implementation and should be detailed
     * in further work.
     *
     * @param graph              the jgrapht to draw
     * @param decompositionWord  the word which decomposition should be drawn
     * @param decompositionDepth the decomposition depth of the words decomposition
     */
    private static void drawGraph(Graph graph, String decompositionWord, int decompositionDepth) {
        graphVirtualizer.setGraph(graph);
        graphVirtualizer.init(decompositionWord, WordType.UNKNOWN, decompositionDepth);
        JFrame frame = new JFrame();
        frame.add(graphVirtualizer);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public abstract void test();


    /**
     * Create a graph containing all nodes and edges which the two graphs have in common.
     *
     * @param graphword1 the first graph to be compared
     * @param graphword2 the secound graph to be compared
     * @return a new graph containing the cut of bowth graphs.
     */
    public Graph getCommonGraph(Graph graphword1, Graph graphword2) {
        Graph commonGraph = new ListenableDirectedGraph(WeightedEdge.class);

        //add all nodes which the two graphs have in common.
        for (Object g1 : graphword1.vertexSet()) {
            if (graphword2.containsVertex(g1)) {
                commonGraph.addVertex(g1);
            }
        }
        //add all edges the two graphs have in common.
        for (WeightedEdge edge : GraphUtil.getCommonEdges(graphword1, graphword2)) {
            commonGraph.addEdge(edge.getSource(), edge.getTarget(), edge);
        }
        return commonGraph;
    }

}
