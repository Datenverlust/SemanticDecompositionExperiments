/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

package de.kimanufaktur.nsm.decompostion.ssd;

import de.kimanufaktur.markerpassing.Marker;
import de.kimanufaktur.markerpassing.Node;
import de.kimanufaktur.nsm.decomposition.Concept;
import de.kimanufaktur.nsm.decomposition.Decomposition;
import de.kimanufaktur.nsm.decomposition.Definition;
import de.kimanufaktur.nsm.decomposition.WordType;
import de.kimanufaktur.nsm.decomposition.graph.conceptCache.GraphUtil;
import de.kimanufaktur.nsm.decomposition.graph.spreadingActivation.MarkerPassing.DoubleMarkerPassing;
import de.kimanufaktur.nsm.decomposition.graph.spreadingActivation.MarkerPassing.MarkerPassingConfig;
import de.kimanufaktur.nsm.decomposition.graph.spreadingActivation.MarkerPassing.MarkerPassingSemanticDistanceMeasure;
import de.kimanufaktur.nsm.graph.entities.marker.DoubleMarkerWithOrigin;
import de.kimanufaktur.nsm.graph.entities.nodes.DoubleNodeWithMultipleThresholds;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import org.apache.log4j.Logger;
import org.jgrapht.Graph;

import java.util.*;

public class SentenceSemanticSimilarityMeasure {

    private static final Logger logger = Logger.getLogger(SentenceSemanticSimilarityMeasure.class);
    //CoreNLP fields
    private static StanfordCoreNLP pipeline = null;
    Decomposition decomposition;
    //SemanticNetworkVisualizer graphVirtualizer;
    List<Concept> originConceptsSentence1;
    List<Concept> originConceptsSentence2;
    MarkerPassingSemanticDistanceMeasure semanticDistanceMeasure = new MarkerPassingSemanticDistanceMeasure();


    public SentenceSemanticSimilarityMeasure() {
        Decomposition.init();
        if (pipeline == null) {
            // creates a StanfordCoreNLP object, with POS tagging, lemmatization, NER, parsing, and coreference resolution
            Properties props = new Properties();
//            props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
            props.put("annotators", "tokenize, ssplit, pos, lemma, parse, depparse");
            pipeline = new StanfordCoreNLP(props);
        }
        this.decomposition = new Decomposition();
        //this.graphVirtualizer = new SemanticNetworkVisualizer();
    }

    public static void main(String[] args) {

        //NOTE: gold standards have been moved to submodule Experiments

        SentenceSemanticSimilarityMeasure instance = new SentenceSemanticSimilarityMeasure();
        //instance.test(1);
        //The maximum for the test data set is 750.
//       instance.runGoldStandard( 459,"STS.gs.SMTeuroparl.txt", "STS.input.SMTeuroparl.txt");
//        instance.runGoldStandard(2, 750,"STS.gs.MSRvid.txt", "STS.input.MSRvid.txt");
//        instance.runGoldStandard(1, 750,"STS.gs.MSRpar.txt", "STS.input.MSRpar.txt");

        return;
    }

    /**
     * Calculates the semantic similarity or distance of two given sentence.
     *
     * @param sentence1 First sentence.
     * @param sentence2 Second sentence.
     * @return The semantic distance between sentence1 and sentence2.
     */
    public double compare(String sentence1, String sentence2) {
        //logger.info("Start compare of: '" + sentence1 + "'" + "'" + sentence2 + "'");


        String s1 = prepareSentence(sentence1);
        String s2 = prepareSentence(sentence2);

        double result = naiveMarkerPassing(s1, s2);

//      double result = syntacticMarkerPassing(s1, s2);
//       double result = ADW(s1, s2);


        //logger.info("End compare of: '" + sentence1 + "'" + " '" + sentence2 + "' result: " + result);
        return result;
    }

//    private double ADW(String sentence1, String sentence2){
//
//        //types of the two lexical items
//        ItemType srcTextType = ItemType.SURFACE;
//        ItemType trgTextType = ItemType.SURFACE;
//
//        //if lexical items has to be disambiguated
//        DisambiguationMethod disMethod = DisambiguationMethod.NONE;
//
//        //measure for comparing semantic signatures
//        SignatureComparison measure = new Cosine(); //new WeightedOverlap();
//        ADW adwPipeLine = ADW.getInstance();
//
//        double score = adwPipeLine.getPairSimilarity(sentence1, sentence2,
//                disMethod, measure,
//                srcTextType, trgTextType);
//        return score;
//
//    }

    /**
     * This is a marker passing to calculate the similarity of sentences by taking into account the syntactical structure.
     * The idea is similar to
     * J. Oliva, J. I. Serrano, M. D. del Castillo, and Á. Iglesias,
     * “SyMSS: A syntax-based measure for short-text semantic similarity,”
     * Data & Knowledge Engineering, vol. 70, no. 4, pp. 390–405, Apr. 2011.
     *
     * @param s1 Sentence one to compare to sentence given in the second argument
     * @param s2 Sentence two to compare to sentence given in the first argument
     * @return a similarity measure.
     */
    private double syntacticMarkerPassing(String s1, String s2) {
        double result = 0;

        //calculate syntactical structur of both sentences

        SemanticGraph semanticGraphSentence1 = CreateSemanticGraph(s1);
        SemanticGraph semanticGraphSentence2 = CreateSemanticGraph(s2);


        IndexedWord root1 = semanticGraphSentence1.getFirstRoot();
        IndexedWord root2 = semanticGraphSentence2.getFirstRoot();

//        double[][] matchingResult = new double[semanticGraphSentence1.vertexSet().size()][semanticGraphSentence2.vertexSet().size()];
//        checkTreeSimilarity(semanticGraphSentence1, semanticGraphSentence2, root1,root2,matchingResult,0,0);
//        for (int i = 0; i <matchingResult.length; i++) {
//            result += matchingResult[i][i];
//        }
//        result = result/matchingResult.length;
//        result = result - (Math.abs(semanticGraphSentence1.vertexSet().size() - semanticGraphSentence2.vertexSet().size()) *0.1);


        //make word sense disambiguation
        //Create concepts
        List<Concept> sentence1 = CreateConceptSentence(s1);
        List<Concept> sentence2 = CreateConceptSentence(s2);
        //Select definitions

        //Make marker passing accoring to the semantic and syntactic graph
        result = checkTreeSimilarity(semanticGraphSentence1, semanticGraphSentence2, root1, root2, sentence1, sentence2);


        //calculate if there are differences in sentance length and roles.
        //1/n * (Sum(sim(x,y)) - l * PunishmentFactor)
        //result = result - (Math.abs(semanticGraphSentence1.vertexSet().size() - semanticGraphSentence2.vertexSet().size()) * 0.1);

        return result;
    }

    /**
     * Create the sum of the maximum of the matches in one semantic tree layer. This means each concept of tree1 is
     * matched to one at the same level of the tree in tree2. The maximum of this matches are summed up for this tree
     * level and is given upwards.
     *
     * @param semanticGraphSentence1 the semantic tree of sentence one to compare
     * @param semanticGraphSentence2 the semantic tree of sentence two to compare
     * @param root1                  The current root element of the dependency graph one  for which to analyze the sub tree.
     * @param root2                  The current root element of the dependency graph two  for which to analyze the sub tree.
     * @param sentence1              The sentence for which dependency graph one has been created.
     * @param sentence2              The sentence for which dependency graph two has been created.
     * @return sum(max ( matchInTreeLayer ( root1, root2))
     */
    private double checkTreeSimilarity(SemanticGraph semanticGraphSentence1, SemanticGraph semanticGraphSentence2, IndexedWord root1, IndexedWord root2, List<Concept> sentence1, List<Concept> sentence2) {

        double rootSimilarity = 0;
        Concept rootOne = sentence1.get(root1.get(CoreAnnotations.IndexAnnotation.class) - 1);
        Concept rootTwo = sentence2.get(root2.get(CoreAnnotations.IndexAnnotation.class) - 1);
        try {
            rootSimilarity = semanticDistanceMeasure.findSim(rootOne, rootTwo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<IndexedWord> children1 = semanticGraphSentence1.getChildList(root1);
        List<IndexedWord> children2 = semanticGraphSentence2.getChildList(root2);
        List<Double> childSimilarity = new ArrayList<>();
        for (IndexedWord child1 : children1) {
            Double max = 0d;
            for (IndexedWord child2 : children2) {
                if (child1.lemma().equals(child2.lemma()) && child1.get(CoreAnnotations.PartOfSpeechAnnotation.class).equals(child2.get(CoreAnnotations.PartOfSpeechAnnotation.class))) {
                    max = 1.0;
                    break;
                } else {

                    if (Decomposition.getConcepts2Ignore().contains(new Concept(child1.lemma())) || Decomposition.getConcepts2Ignore().contains(new Concept(child2.lemma()))) {
                        continue;
                    }
                    if (child1.lemma().length() < 3 || child2.lemma().length() < 3) { //We remove small words to improve the performance!
                        continue;
                    }
                    double tmp = checkTreeSimilarity(semanticGraphSentence1, semanticGraphSentence2, child1, child2, sentence1, sentence2);
                    if (tmp > max) {
                        max = tmp;
                    }
                }
            }
            childSimilarity.add(max);
            if (max == 1.0) {
                continue;
            }
        }
        if (rootSimilarity < 0) {
            rootSimilarity = 0;
        }

        return (0.8 * rootSimilarity + 0.2 * ((childSimilarity.stream().mapToDouble(value -> value).sum()) / childSimilarity.size())) / 2; //(Math.max(children1.size(), children2.size())))); \\Math.max(rootSimilarity,((childSimilarity.stream().mapToDouble(value -> value).sum()) ));//
    }


    private void checkTreeSimilarity(SemanticGraph semanticGraphSentence1, SemanticGraph semanticGraphSentence2, IndexedWord root1, IndexedWord root2, double[][] matchingResult, int i, int j) {

        Concept rootOne = new Concept(root1.lemma());
        rootOne.setWordType(WordType.valueOf(root1.get(CoreAnnotations.PartOfSpeechAnnotation.class)));
        Concept rootTwo = new Concept(root2.lemma());
        rootTwo.setWordType(WordType.valueOf(root2.get(CoreAnnotations.PartOfSpeechAnnotation.class)));

        try {
            matchingResult[i][j] = semanticDistanceMeasure.findSim(rootOne, rootTwo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (IndexedWord child1 : semanticGraphSentence1.getChildList(root1)) {
            for (IndexedWord child2 : semanticGraphSentence2.getChildList(root2)) {
                // child1.get()
                checkTreeSimilarity(semanticGraphSentence1, semanticGraphSentence2, child1, child2, matchingResult, ++i, ++j);
            }
        }
    }


    /**
     * Create a list of concept for the given sentence. Here POS and lemma are created from the sentence.
     *
     * @param s1 the sentence to create a list of concept from
     * @return a list of concepts which represent the sentence of the parameter.
     */
    private List<Concept> CreateConceptSentence(String s1) {
        Definition definition = new Definition(s1);
        return definition.getDefinition();

    }

    /**
     * Create a semantic graph with coreNLP
     *
     * @param s1 the sentence to create the semantic graph for
     * @return a semantic graph from CoreNLP
     */
    private SemanticGraph CreateSemanticGraph(String s1) {
        Annotation document = new Annotation(s1);
        pipeline.annotate(document);
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
        for (CoreMap sen : sentences) {
            // this is the Stanford dependency graph of the current sentence
            return sen.get(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class);
        }
        return null;
    }

    /**
     * Prepares a arbitrary sentence for the algorithm.
     *
     * @param sentence A Sentence as it occurs in the nature. No restrictions, words should be separated by whitespace.
     *                 Non alphanumeric characters are ignored and can lead to new words.
     * @return A prepared sentence as input for the function buildSentenceGraph(String sentence)
     */
    private String prepareSentence(String sentence) {

        //TODO: determine part-of-speech (POS)

        // remove all non alphanumeric characters except spaces
        sentence = sentence.replaceAll("[^a-zA-Z0-9\\s]", "");

        //TODO: implement stemming if needed e.g. like and likes than likes is not found

        return sentence;
    }


    /**
     * Builds for each word of the sentence a graph by decomposition
     * and merge all graphs into one graph which then represents the sentence.
     *
     * @param sentence       Prepared sentence where all punctuation characters are removed.
     * @param originConcepts Empty List<Concepts> where each word of the sentence as a origin Concepts is stored.
     * @return The graph which was build by all words of the sentence and now represents the sentence.
     */
    private Graph buildSentenceGraph(String sentence, List<Concept> originConcepts) {
        logger.info("Start build graph for: '" + sentence + "'");

        Graph graph = null;

        StringTokenizer st = new StringTokenizer(sentence);

        while (st.hasMoreTokens()) {
            String word = st.nextToken();
            Concept concept = decomposition.multiThreadedDecompose(word, WordType.UNKNOWN, MarkerPassingConfig.getDecompositionDepth());
            Graph tmpGraph = GraphUtil.createJGraph(concept);
            if (tmpGraph.vertexSet().size() == 0) {
                continue;
            }
            originConcepts.add((Concept) tmpGraph.vertexSet().toArray()[0]);
            logger.info("Add graph size: " + tmpGraph.vertexSet().size());
            if (graph == null)
                graph = tmpGraph;
            else if (tmpGraph != null)
                graph = GraphUtil.mergeGraph(graph, tmpGraph);
        }

        logger.info("End build graph for: '" + sentence + "' graph size: " + graph.vertexSet().size());
        return graph;
    }

    /**
     * Marker passing for two graphs where each graph represents a sentence.
     * Each word of a sentence is represented as root in the graph.
     * Each root node receives a start marker.
     * <p>
     * TODO: Distribute start marker based on the POS of each word in the sentence.
     *
     * @param sentence1 Sentence which represents the first sentence.
     * @param sentence2 Sentence which represents the second sentence.
     * @return The semantic distance between graph1 and graph1.
     */
    private double naiveMarkerPassing(String sentence1, String sentence2) {
        logger.info("Start marker passing");
        Graph graph1 = buildSentenceGraph(sentence1, originConceptsSentence1 = new ArrayList<Concept>());
        Graph graph2 = buildSentenceGraph(sentence2, originConceptsSentence2 = new ArrayList<Concept>());


        MarkerPassingConfig config = new MarkerPassingConfig();


        Graph commonGraph = GraphUtil.mergeGraph(graph1, graph2);

        Map<Concept, List<? extends Marker>> conceptMarkerMap = new HashMap<>();
        Map<Concept, Double> threshold = new HashMap<>();

        // create start marker for sentence1
        createStartMarker4Sentence(originConceptsSentence1, MarkerPassingConfig.getStartActivation(), MarkerPassingConfig.getThreshold(), conceptMarkerMap, threshold);
        //set start markers
        List<Map<Concept, List<? extends Marker>>> startActivation = new ArrayList<>();
        startActivation.add(conceptMarkerMap);

        // create start marker for sentence2
        createStartMarker4Sentence(originConceptsSentence2, MarkerPassingConfig.getStartActivation(), MarkerPassingConfig.getThreshold(), conceptMarkerMap, threshold);

        //set start markers
        startActivation.add(conceptMarkerMap);

        //create marker passing algorithm      
        DoubleMarkerPassing doubleMarkerPassing = new DoubleMarkerPassing(commonGraph, threshold, DoubleNodeWithMultipleThresholds.class);
        DoubleMarkerPassing.doInitialMarking(startActivation, doubleMarkerPassing);
        doubleMarkerPassing.execute();
        Collection<Node> activeNodes = doubleMarkerPassing.getActiveNodes();
        logger.info("count of activeNodes: " + activeNodes.size());

        // read result of marker passing
        List<DoubleNodeWithMultipleThresholds> doubleActiveNodes = getDoubleActivation(activeNodes);
        logger.info("count of doubleActiveNodes: " + doubleActiveNodes.size());
        //doubleActiveNodes.forEach(x -> System.out.println("doubleActiveNode: " + x.getConcept().getLitheral()));
        double avgActivation = getAvgActivation(doubleActiveNodes);
        logger.info("average activation: " + avgActivation);

        logger.info("End marker passing");

        originConceptsSentence1.forEach(x -> logger.info("1 origins were: " + x.getLitheral()));
        originConceptsSentence2.forEach(x -> logger.info("2 origins were: " + x.getLitheral()));

        int countOforiginConcepsFired = getCountOfDistinctOriginConcepsFired(originConceptsSentence1, originConceptsSentence2);
        double rateOfOriginConcepIntersection = getRateOfOriginConcepIntersection(originConceptsSentence1, originConceptsSentence2, countOforiginConcepsFired);

        logger.info("distinctOriginConcepsFired:     " + countOforiginConcepsFired);
        logger.info("rateOfOriginConcepIntersection: " + rateOfOriginConcepIntersection);
        logger.info("rateOfSentenseSimilarity: 		 " + (avgActivation / (countOforiginConcepsFired * MarkerPassingConfig.getStartActivation())));

        return rateOfOriginConcepIntersection + (avgActivation / (countOforiginConcepsFired * MarkerPassingConfig.getStartActivation()));
    }

    private void createStartMarker4Sentence(List<Concept> originConceptsSentence, double startActivationLevel, double thresholdNode, Map<Concept, List<? extends Marker>> conceptMarkerMap, Map<Concept, Double> threshold) {
        for (Concept rootConcept : originConceptsSentence) {
            List<Marker> markers1 = new ArrayList<>();
            Concept activeNode = rootConcept;
            DoubleMarkerWithOrigin startMarker = new DoubleMarkerWithOrigin();
            startMarker.setActivation(startActivationLevel);
            startMarker.setOrigin(activeNode);
            markers1.add(startMarker);
            conceptMarkerMap.put(activeNode, markers1);
            threshold.put(activeNode, thresholdNode);
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
                DoubleNodeWithMultipleThresholds doubleNodeWithMultipleThresholds = (DoubleNodeWithMultipleThresholds) node;
                Concept concept = doubleNodeWithMultipleThresholds.getConcept();
                if (doubleNodeWithMultipleThresholds.getActivation().size() > 1 && !Decomposition.getConcepts2Ignore().contains(concept.getLitheral())) {
                    doubleActiveNodes.add(doubleNodeWithMultipleThresholds);
                }
                // marker are not merged within mergeGraph, therefore two identical concepts are not found via marker passing because only one marker is passed
                // note: word order is ignored here!
                if (originConceptsSentence1.contains(concept) && originConceptsSentence2.contains(concept)) {

                    //TODO add not 100.0 add real start activation
                    //Double startAction = 100.0;
                    //Double bonus = startAction; // Bonus is consumed within due getAvgActivation
                    //doubleNodeWithMultipleThresholds.addActivation(concept, startAction+bonus);
                    //doubleActiveNodes.add(doubleNodeWithMultipleThresholds);
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
    private int getCountOfDistinctOriginConcepsFired(List<Concept> originConceptsSentence1, List<Concept> originConceptsSentence2) {

        HashSet<Concept> distinctConcepts = new HashSet<>();

        originConceptsSentence1.forEach(x -> distinctConcepts.add(x));
        originConceptsSentence2.forEach(x -> distinctConcepts.add(x));

        return distinctConcepts.size();
    }

    private double getRateOfOriginConcepIntersection(List<Concept> originConceptsSentence1,
                                                     List<Concept> originConceptsSentence2, int originConcepsFired) {

        double conceptIntersection = (originConceptsSentence1.size() + originConceptsSentence2.size()) - originConcepsFired;

        double rateOfIntersection = conceptIntersection / (((originConceptsSentence1.size() + originConceptsSentence2.size()) / 2));

        return rateOfIntersection;
    }

//    /**
//     * Draw a graph to a JFrame.
//     * TODO: should save the JFrame as an image.
//     *
//     * @param graph The graph of a word or sentence.
//     */
//    private void drawGraph(Graph graph) {
//        graphVirtualizer.setGraph(graph);
//        graphVirtualizer.init("", WordType.UNKNOWN, MarkerPassingConfig.getDecompositionDepth());
//        JFrame frame = new JFrame();
//        frame.add(graphVirtualizer);
//        frame.pack();
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setVisible(true);
//    }

    /**
     * Runs some simple and short test sentence and
     * write the result to the console.
     *
     * @param decompositionDepth The decomposition depth with which the words of the sentence should be decomposed.
     */
    private void test(int decompositionDepth) {

        Map<String, String> exampleSet = new HashMap<String, String>();

        //exampleSet.put("gem", "jewel"); // data from Rubenstein, result should be 0.52 and returns 0.52
        //exampleSet.put("spinning", "dancing"); // result is 0.0
        //exampleSet.put("midday", "noon"); // result is 0.86
        //exampleSet.put("midday gem.", "noon jewel.");

        //exampleSet.put("cat", "cat");

        //exampleSet.put("piano", "guitar");
        exampleSet.put("A baby panda goes down a slide.", "A panda slides down a slide.");

        for (Map.Entry<String, String> example : exampleSet.entrySet()) {
            compare(example.getKey(), example.getValue());
        }
    }


    /**
     *  NOTE: runGoldStandard has moved to class MSRVIDGoldStandard located in the new Experiments sub module.
     *  This was done to get rid of the msvid dependency and seems to be more logical to split this gold standard test/exüeriment
     *  from the actual code base
     */


    /**
     * Runs the MSRvid gold standard data set from STS-12
     * and write the results to MSRvid_Result.txt and console.
     *
     * @param top                The number of test data pairs which should be processed.
     */
//    private void runGoldStandard( int top, String gsFile, String inputFile) {
//
//        // read MSRvid data set from STS-12
//        MSRvid msrvid = new MSRvid();
//        DecimalFormat df = new DecimalFormat("#.##");
//        List<SimilarityPair> dataSet = msrvid.readMSRvid(top, gsFile, inputFile);
//        Evaluation.normalize(dataSet);
//        double cumulativeResultError = 0;
//        // run data set
//
//        for (SimilarityPair pair : dataSet) {
//            double result = compare(pair.getString1(), pair.getString2());
//            if(Double.isNaN(result)){
//                result = 0.5;
//            }
//            pair.setResult(result);
//            logger.info(pair.getString1() + ";" + pair.getString2() + ";" + df.format(pair.getDistance()) + ";" + df.format((pair.getResult())));
//            cumulativeResultError += (Math.abs(pair.getDistance() - pair.getResult()));
//        }
//
//
//        System.out.println("SpearmanCorrelation: " + Evaluation.SpearmanCorrelation(dataSet));
//        System.out.println("PearsonCorrelation: " + Evaluation.PearsonCorrelation(dataSet));
//        System.out.println("Total error: " + cumulativeResultError);
//
//        // write result to file
//        try {
//            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
//            //String rulstPath = System.getProperty("user.home").toString() + File.separator + ".Decomposition" + File.separator + "Experiments"+ File.separator + "SentenceSimilarity";
//            PrintWriter writer = new PrintWriter(timeStamp + "_MSRvid_Result.csv", "UTF-8");
//            writer.println("Sentence1;Sentence2;STS12Distance;OurResult");
//            for (SimilarityPair pair : dataSet) {
//                double should = pair.getDistance();
//                double is = pair.getResult();
//                String result = pair.getString1() + ";" + pair.getString2() + ";" + df.format(should) + ";" + df.format(is);
//                writer.println(result);
//            }
//            writer.close();
//
//        } catch (FileNotFoundException | UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//
//        return;
//    }

}
