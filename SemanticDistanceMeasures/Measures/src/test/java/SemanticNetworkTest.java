/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

import de.kimanufaktur.nsm.decomposition.WordType;
import de.kimanufaktur.nsm.decomposition.graph.conceptCache.GraphUtil;
import de.kimanufaktur.nsm.semanticDistanceMeasures.DataExample;
import de.kimanufaktur.nsm.semanticDistanceMeasures.SimilarityPair;
import org.jgrapht.Graph;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Test class which contains our tests for the semantic network.
 * Here the marker passing and activation spreading is implemented.
 */
public class SemanticNetworkTest extends SemanticDistanceTest {

    public SemanticNetworkTest() {
        super();
    }

    public static void main(String[] args) {

        /**-----------------------------------------------------------------
         Create Testgraph
         -------------------------------------------------------------------*/
        Graph resultGraph = null;
        Graph resultGraph1 = null;
        Graph resultGraph2 = null;
        SemanticNetworkTest test = new SemanticNetworkTest();
        /**-----------------------------------------------------------------
         Test Similarity
         -------------------------------------------------------------------*/

        //test.decomposition.CompareConcepts(null, null);


        //resultGraph = test.getGraph("fish", WordType.NN, 3);

        //test.testMP(2);
        //resultGraph = test.testDistance("boat", WordType.NN, "ship", WordType.NN, 2);
        //resultGraph1 =  test.getGraph("fish", WordType.NN, 2);
        //resultGraph2 = test.testDistance("boat", WordType.NN, "ship", WordType.NN, 2);
        /**-----------------------------------------------------------------
         Prepare Activation spreading
         -------------------------------------------------------------------*/
//        Concept activeNode = (Concept)resultGraph1.vertexSet().toArray()[0];
//        DoubleMarker startMarker = new DoubleMarker();
//        startMarker.setActivation(90.d);
//        List<Map<Concept,List<Marker>>> startActivation = new ArrayList<>();
//        Map<Concept,List<Marker>> conceptMarkerMap = new HashMap<>();
//        List<Marker> markers = new ArrayList<>();
//        markers.add(startMarker);
//        conceptMarkerMap.put(activeNode,markers);
//        startActivation.add(conceptMarkerMap);
//        DoubleSpreadingActivation doubleSpreadingActivationAlgo = getDoubleSpreadingActivation(resultGraph1,startActivation,1.0);
//        Collection<Node> activeNodes = doubleSpreadingActivationAlgo.getActiveNodes();
        /**-----------------------------------------------------------------
         Prepare Marker Passing
         -------------------------------------------------------------------*/
        //double result = test.passMarker("cemetery", WordType.NN, "graveyard", WordType.NN, 2);
        //double result = test.passMarker("implement", WordType.NN, "tool", WordType.NN, 2);
        //double result = test.passMarker("pillow", WordType.NN, "cushion", WordType.NN, 1);


        /**-----------------------------------------------------------------
         Test with with the test dataset of:
         Rubenstein, H., & Goodenough, J. B. (1965). Contextual correlates of synonymy.
         Communications of the ACM, 8(10), 627–633. http://doi.org/10.1145/365628.365657
         -------------------------------------------------------------------*/
        //test.test();
        /**-----------------------------------------------------------------
         draw graph
         -------------------------------------------------------------------*/
        //drawGraph(result, null, 0);


    }

    @Override
    public void test() {
        System.out.println("Word;Synonym;WordVerteces;SynonymVerteces;WordEdges;SynonymEdges;CommonVerteces;ComonEdges");
        for (DataExample pair : testSynonymPairs) {
            try {
                //threadPoolExecutor.submit(new TestRunnable() {
                int decompositionDepth = 2;
                //     @Override
                //     public void run() {
                //spreadActivation(this.word1, this.wordType1, this.word2, this.wordType2, this.decompositionDepth);

                Graph commonGraph = testDistance(((SimilarityPair) pair).getString1(), WordType.NN, ((SimilarityPair) pair).getString2(), WordType.NN, decompositionDepth);
                //     }
                // });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //threadPoolExecutor.shutdown();
        return;
    }


    /**
     * Calculate the cut of two graphs as a first heuristic for the distance of two concepts. This is rather
     * week in terms of semantics, since we only count the common nodes and edges. The relations, and their
     * structure are ignored.
     * The function trys to load the graphs from ~/.decomposition/word_decompositionDepth/
     * If the graph is not found in this directory, the decomposition is started, and the result is saved there
     * for further use.
     *
     * @param word1              the first word to be compared
     * @param wordType1          the POS of the first word
     * @param word2              the second word to be compared
     * @param wordType2          the POS of the second word
     * @param decompositionDepth the decomposition depth, with which the tow words should be compared.
     * @return
     */
    public Graph testDistance(String word1, WordType wordType1, String word2, WordType wordType2, int decompositionDepth) {
        Graph graphword1 = GraphUtil.getGraph(word1, wordType1, decompositionDepth);
        Graph graphword2 = GraphUtil.getGraph(word2, wordType2, decompositionDepth);
        Graph commonGraph = getCommonGraph(graphword1, graphword2);
        double word1countVerteces = graphword1.vertexSet().size();
        double word2countVerteces = graphword2.vertexSet().size();
        double word1countEdges = graphword1.edgeSet().size();
        double word2countEdges = graphword2.edgeSet().size();
        double countVertecesCommon = commonGraph.vertexSet().size();
        double countEdgesCommon = commonGraph.edgeSet().size();
        //System.out.println(word1 +" und "+ word2 +" have : " + countEdgesCommon/(word1countEdges+word2countEdges/2) + "% edges and " + countVertecesCommon/(word1countVerteces+word2countVerteces/2) + "% vertexes in common");

        System.out.println(word1 + ";" + word2 + ";" + word1countVerteces + ";" + word2countVerteces + ";" + word1countEdges + ";" + word2countEdges + ";" + countVertecesCommon + ";" + countEdgesCommon);
        return commonGraph;
    }

    private List<Graph> getWordGraphs(int decompositionDepth, List<SimilarityPair> testSet) {
        List<Graph> wordGraphs = new ArrayList<>();
        ArrayList<Future> futures = new ArrayList<Future>();
        ExecutorService pool = Executors.newCachedThreadPool();
        for (SimilarityPair pair : testSet) {
            DecompositionCallable callable = new DecompositionCallable();
            callable.setDecompositionWord(pair.getString1());
            callable.setWordType(WordType.NN);
            callable.setDecompositionDepth(decompositionDepth);
            Callable<Graph> runnable = callable;
            futures.add(pool.submit(runnable));
        }
        Graph futureresult = null;
        while (!futures.isEmpty()) {
            for (int i = 0; i < futures.size(); i++) {
                try {
                    if (futures.get(i).isDone()) {
                        futureresult = (Graph) futures.get(i).get();
                        if (futureresult != null) {
                            wordGraphs.add(futureresult);
                        }
                        futures.remove(futures.get(i));
                    } else if (futures.get(i).isCancelled()) {
                        futures.remove(futures.get(i));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // remove old futures
        futures.clear();
        return wordGraphs;
    }

    private List<Graph> getSynonymGraphs(int decompositionDepth, List<SimilarityPair> testSet) {
        List<Graph> wordGraphs = new ArrayList<>();
        ArrayList<Future> futures = new ArrayList<Future>();
        ExecutorService pool = Executors.newCachedThreadPool();
        for (SimilarityPair pair : testSet) {
            DecompositionCallable callable = new DecompositionCallable();
            callable.setDecompositionWord(pair.getString2());
            callable.setWordType(WordType.NN);
            callable.setDecompositionDepth(decompositionDepth);
            Callable<Graph> runnable = callable;
            futures.add(pool.submit(runnable));
        }
        Graph futureresult = null;
        while (!futures.isEmpty()) {
            for (int i = 0; i < futures.size(); i++) {
                try {
                    if (futures.get(i).isDone()) {
                        futureresult = (Graph) futures.get(i).get();
                        if (futureresult != null) {
                            wordGraphs.add(futureresult);
                        }
                        futures.remove(futures.get(i));
                    } else if (futures.get(i).isCancelled()) {
                        futures.remove(futures.get(i));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // remove old futures
        futures.clear();
        return wordGraphs;
    }

    /**
     * The callable for the decompsotion. This is used for multithreading the decomposition. Please notice that
     * the use of parallel computing might incluence the results, since it is not yet clear in which oder the
     * decomposition will take place. Depending on the common concepts of two decompositions, and the decomposition
     * depths, the decompsotion will allway use the allready exsisting decomposition. Meaning first one (thread)
     * that decomposes the concepts decides the decomposition depth.
     */
    class DecompositionCallable implements Callable {
        String decompositionWord = null;
        WordType wordType = null;
        int decompositionDepth = 0;

        public String getDecompositionWord() {
            return decompositionWord;
        }

        public void setDecompositionWord(String decompositionWord) {
            this.decompositionWord = decompositionWord;
        }

        public WordType getWordType() {
            return wordType;
        }

        public void setWordType(WordType wordType) {
            this.wordType = wordType;
        }

        public int getDecompositionDepth() {
            return decompositionDepth;
        }

        public void setDecompositionDepth(int decompositionDepth) {
            this.decompositionDepth = decompositionDepth;
        }

        @Override
        public Object call() {
            return GraphUtil.getGraph(this.decompositionWord, this.wordType, this.decompositionDepth);
        }
    }
}

