/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

import de.dailab.nsm.decomposition.AnalyseUtil;
import de.dailab.nsm.decomposition.Concept;
import de.dailab.nsm.decomposition.Decomposition;
import de.dailab.nsm.decomposition.graph.spreadingActivation.MarkerPassing.MarkerPassingConfig;
import de.tuberlin.spreadalgo.Marker;
import de.tuberlin.spreadalgo.Node;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import org.jgrapht.Graph;
import se.lth.cs.srl.CompletePipeline;

import java.util.*;

public class QuestionAnswer {

    public static QuestionConcept findConcept(List<List<String>> tokenized, String wort, String quoteText, StanfordCoreNLP pipeline) {
        QuestionConcept questionConcept = null;
        if (quoteText.equals("")) {
            int wordNR = -1;
            for (List<String> list : tokenized) {
                int i = 0;
                String[] prons = wort.split(" ");
                if (prons.length > 1) {
                    boolean foundfirst = false;
                    boolean foundsecond = false;
                    for (String word : list) {
                        while (!foundfirst) {
                            if (word.equals(prons[0])) {
                                wordNR = i;
                                //return new PronConcept(pron, wordNR, 0);
                                foundfirst = true;
                            }
                            break;
                        }

                        while (!foundsecond) {
                            if (word.equals(prons[1])) {
                                wordNR = i;
                                foundsecond = true;
                                return new QuestionConcept(wort, wordNR, 0);
                            }
                            break;
                        }
                        i++;
                    }
                } else {
                    for (String word : list) {
                        if (word.equals(wort)) {
                            wordNR = i;
                            return new QuestionConcept(wort, wordNR, 0);
                        }
                        i++;
                    }
                }
            }
        } else {

            Annotation quoteDoc = AnalyseUtil.getAnnotation(quoteText, pipeline);
            List<List<String>> fullQuote = AnalyseUtil.tokenizeText(quoteDoc);
            List<String> quote = fullQuote.get(0);

            int temp = -1;
            for (int i = 0; i < tokenized.size(); i++) {

                List<String> sentence = tokenized.get(i);
                for (int j = 0; j < sentence.size(); j++) {

                    if (sentence.get(j).equals(quote.get(0))) {
                        for (int k = 0; k < quote.size(); k++) {
                            if (sentence.get(k + j).equals(quote.get(k))) {
                                if (sentence.get(k + j).equals(wort)) {
                                    temp = k + j;
                                }
                            } else {
                                break;
                            }
                            if (k == quote.size() - 1) {
                                questionConcept = new QuestionConcept(wort, temp, i);
                                return questionConcept;
                            }
                        }

                    }
                }
            }
        }
        return questionConcept;
    }

    public static void clean(){
        //GraphUtil.setDecomposition(new Decomposition());
        //GraphUtil.setGraphCache(Collections.synchronizedMap(new HashMap<String, Graph>()));
        //GraphUtil.setLockcount(0);
        //GraphUtil.setLockMap(Collections.synchronizedMap(new Hashtable<String, Object>()));
        //GraphUtil.setMergedGraphCache(new HashMap<>(65));

        //TODO: not supported currently or i dont know how to "clean" GraphUtil
    }

    public static void cleanUpMarker(WinogradDoubleMarkerPassing doubleMarkerPassing){


        for(Node node :doubleMarkerPassing.getNodes().values()) {


                WinogradDoubleNode dnode = (WinogradDoubleNode) node;
                if (dnode != null) {
                    dnode.markerInformation = new LinkedList<MarkerInformation>();
                    dnode.markers = new LinkedList<Marker>();
                    for (Marker m : dnode.getActivationHistory()) {
                        dnode.removeMarker(m);

                    }
                }

        }
        Collection<Node> activeNodes = doubleMarkerPassing.getActiveNodes();
        doubleMarkerPassing.doubleActivation = new HashMap<>();


    }



    public static List<GraphCache> getGraphs(List<Question> questions){
        List<GraphCache> questionGraphs = new ArrayList<>();
        for(Question q :questions) {
            clean();
            MarkerPassingConfig.setDecompositionDepth(2);


            Decomposition decomposition = new Decomposition();
            decomposition.init();
            StanfordCoreNLP stanPipeline = AnalyseUtil.getFullPipeline();
            CompletePipeline srlPipeline = SemanticRoleLabeler.getPipeline();

            System.out.println(q.getQuestionContent());
            System.out.println();
            System.out.print("Annotating schema...");
            Annotation annotation = AnalyseUtil.getAnnotation(q.getQuestionContent(), stanPipeline);
            System.out.println("done.");

            List<List<String>> tokenized = AnalyseUtil.tokenizeText(annotation);

            System.out.print("Finding pron...");
            System.out.println(q.getQuestionContent());
            String questionVerb = DataLoader.getVerbs(tokenized, srlPipeline);
            try {
                QuestionConcept questionConcept = findConcept(tokenized, questionVerb, q.getQuestionContent(), stanPipeline);


                System.out.println("done.");


                System.out.print("Getting syntactic dependencies...");
                List<List<SemanticGraphEdge>> edgeList = GraphBuilder.getSyntactics(annotation);
                //here begins a stupid approach to allow reflux of markers
                List<List<SemanticGraphEdge>> sentenceListWithReverses = new LinkedList<>();
                for (List<SemanticGraphEdge> sentence : edgeList) {
                    List<SemanticGraphEdge> reverseList = new LinkedList<SemanticGraphEdge>();
                    for (SemanticGraphEdge edge : sentence) {
                        SemanticGraphEdge newEdge = new SemanticGraphEdge(edge.getTarget(), edge.getSource(), edge.getRelation(), edge.getWeight(), edge.isExtra());
                        reverseList.add(newEdge);
                    }
                    sentence.addAll(reverseList);
                    sentenceListWithReverses.add(sentence);
                }
                edgeList = sentenceListWithReverses;

                System.out.println("done.");
                List<String> answers = new ArrayList<>();
                answers.add(q.anwserA);
                answers.add(q.anwserB);
                answers.add(q.anwserC);
                answers.add(q.anwserD);

                //List<String> excluded = excludedAttribute(edgeList, answers, pronConcept);

                System.out.print("Getting named entities...");
                Map<String, String> nerMap = GraphBuilder.getNerMap(annotation);
                System.out.println("done.");

                System.out.print("Getting roles...");
                Map<List<Concept>, List<String>> roleMap = SemanticRoleLabeler.parse(tokenized, questionConcept, srlPipeline);
                System.out.println("done.");


                System.out.print("Building graphs...");
                Graph decGraph = GraphBuilder.getDecGraph(tokenized, roleMap, nerMap);
                //System.out.println("afgh: "+decGraph.toString());

                Graph syntaxGraph = GraphBuilder.getSyntaxGraph(edgeList, questionConcept);

                //System.out.println("afgh: "+syntaxGraph.toString());
                //GraphUtil.saveToGraphML(syntaxGraph, "D:\\syntaxHaven.GraphML");
                Graph nerGraph = GraphBuilder.getNerGraph(nerMap);

                //System.out.println("afgh: "+nerGraph.toString());
                Graph roleGraph = GraphBuilder.getRoleGraph(roleMap);

                List<Map<Concept, List<? extends Marker>>> startActivation = null;

                startActivation = StartActivator.setStartActivation(StartActivator.getAllVerbsNounsAndAdjectives(tokenized, srlPipeline, stanPipeline, nerMap, answers));

                questionGraphs.add(new GraphCache(q,decGraph,syntaxGraph,nerGraph,roleGraph,nerMap,startActivation));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return questionGraphs;

    }

    static Map<String,Double> getAnswerMap(GraphCache g){
        Map<String, Integer> superLinkMap = new HashMap<>();


        System.out.println();
        System.out.println("QuestionAnswerTest");
        System.out.println();

        System.out.print("Initializing...");
        //Decomposition decomposition = new Decomposition();
        //decomposition.init();

        System.out.println("done.");

        Question q = g.question;


        //CONFIG MARKERPASSING
        MarkerPassingConfig.setDecompositionDepth(2);
        //MarkerPassingConfig.setTerminationPulsCount(4);

        clean();
        MarkerPassingConfig.setDecompositionDepth(2);




        System.out.println(q.getQuestionContent());
        System.out.println();
        System.out.print("Annotating schema...");

        System.out.println("done.");




        try {


            System.out.println("done.");


            List<String> answers = new ArrayList<>();
            answers.add(q.anwserA);
            answers.add(q.anwserB);
            answers.add(q.anwserC);
            answers.add(q.anwserD);

            //List<String> excluded = excludedAttribute(edgeList, answers, pronConcept);

            System.out.print("Getting named entities...");
            Map<String, String> nerMap = g.nerMap;
            System.out.println("done.");


            System.out.print("Building graphs...");
            Graph decGraph = g.decGraph;
            //System.out.println("afgh: "+decGraph.toString());

            Graph syntaxGraph = g.syntaxGraph;

            //System.out.println("afgh: "+syntaxGraph.toString());
            //GraphUtil.saveToGraphML(syntaxGraph, "D:\\syntaxHaven.GraphML");
            Graph nerGraph = g.nerGraph;

            //System.out.println("afgh: "+nerGraph.toString());
            Graph roleGraph = g.roleGraph;
            //GraphUtil.saveToGraphML(roleGraph, "D:\\roleDad.GraphML");
            //System.out.println("afgh: "+roleGraph.toString());
            System.out.println("done.");


            try {
                Evaluator eva = new Evaluator();
                List<Map<Concept, List<? extends Marker>>> startActivation = g.startActivation;




                System.out.println("done.");

                System.out.print("Initializing markerpassing...");
                WinogradDoubleMarkerPassing doubleMarkerPassing = new WinogradDoubleMarkerPassing(decGraph, WinogradDoubleNode.class, syntaxGraph, nerGraph, roleGraph, new MarkerPassingConfig());
                System.out.println(doubleMarkerPassing.getNodes().size());
                System.out.println(doubleMarkerPassing.doubleActivation.size());
                WinogradDoubleMarkerPassing.doInitialMarking(startActivation, doubleMarkerPassing);
                System.out.print("done.");

                System.out.print("Executing markerpassing...");
                doubleMarkerPassing.execute();
                System.out.println("done.");
                System.out.println();
                Map<String,Double>  result =eva.summe(doubleMarkerPassing,answers);
                cleanUpMarker(doubleMarkerPassing);
                return result;




            }catch(Exception ex){
                ex.printStackTrace();

            }





        }catch(Exception e){
            e.printStackTrace();
        }
        return null;

    }


    static Map<String,Double> getAnswerMap(Question q){
        Map<String, Integer> superLinkMap = new HashMap<>();


        System.out.println();
        System.out.println("QuestionAnswerTest");
        System.out.println();

        System.out.print("Initializing...");
        //Decomposition decomposition = new Decomposition();
        //decomposition.init();

        System.out.println("done.");










        ArrayList<Question> questions = new ArrayList<>();





        //CONFIG MARKERPASSING
        MarkerPassingConfig.setDecompositionDepth(2);
        //MarkerPassingConfig.setTerminationPulsCount(4);






            clean();
            MarkerPassingConfig.setDecompositionDepth(2);


            Decomposition decomposition = new Decomposition();
            decomposition.init();
            StanfordCoreNLP stanPipeline = AnalyseUtil.getFullPipeline();
            CompletePipeline srlPipeline = SemanticRoleLabeler.getPipeline();

            System.out.println(q.getQuestionContent());
            System.out.println();
            System.out.print("Annotating schema...");
            Annotation annotation = AnalyseUtil.getAnnotation(q.getQuestionContent(),stanPipeline);
            System.out.println("done.");

            List<List<String>> tokenized=AnalyseUtil.tokenizeText(annotation);

            System.out.print("Finding pron...");
            System.out.println(q.getQuestionContent());
            String questionVerb = DataLoader.getVerbs(tokenized,srlPipeline);
            try {
                QuestionConcept questionConcept = findConcept(tokenized, questionVerb, q.getQuestionContent(), stanPipeline);



                System.out.println("done.");






                System.out.print("Getting syntactic dependencies...");
                List<List<SemanticGraphEdge>> edgeList = GraphBuilder.getSyntactics(annotation);
                //here begins a stupid approach to allow reflux of markers
                List<List<SemanticGraphEdge>> sentenceListWithReverses = new LinkedList<>();
                for (List<SemanticGraphEdge> sentence : edgeList) {
                    List<SemanticGraphEdge> reverseList = new LinkedList<SemanticGraphEdge>();
                    for (SemanticGraphEdge edge : sentence) {
                        SemanticGraphEdge newEdge = new SemanticGraphEdge(edge.getTarget(), edge.getSource(), edge.getRelation(), edge.getWeight(), edge.isExtra());
                        reverseList.add(newEdge);
                    }
                    sentence.addAll(reverseList);
                    sentenceListWithReverses.add(sentence);
                }
                edgeList = sentenceListWithReverses;

                System.out.println("done.");
                List<String> answers = new ArrayList<>();
                answers.add(q.anwserA);
                answers.add(q.anwserB);
                answers.add(q.anwserC);
                answers.add(q.anwserD);

                //List<String> excluded = excludedAttribute(edgeList, answers, pronConcept);

                System.out.print("Getting named entities...");
                Map<String, String> nerMap = GraphBuilder.getNerMap(annotation);
                System.out.println("done.");

                System.out.print("Getting roles...");
                Map<List<Concept>, List<String>> roleMap = SemanticRoleLabeler.parse(tokenized,questionConcept, srlPipeline);
                System.out.println("done.");


                System.out.print("Building graphs...");
                Graph decGraph = GraphBuilder.getDecGraph(tokenized, roleMap, nerMap);
                //System.out.println("afgh: "+decGraph.toString());

                Graph syntaxGraph = GraphBuilder.getSyntaxGraph(edgeList,questionConcept);

                //System.out.println("afgh: "+syntaxGraph.toString());
                //GraphUtil.saveToGraphML(syntaxGraph, "D:\\syntaxHaven.GraphML");
                Graph nerGraph = GraphBuilder.getNerGraph(nerMap);

                //System.out.println("afgh: "+nerGraph.toString());
                Graph roleGraph = GraphBuilder.getRoleGraph(roleMap);
                //GraphUtil.saveToGraphML(roleGraph, "D:\\roleDad.GraphML");
                //System.out.println("afgh: "+roleGraph.toString());
                System.out.println("done.");
                List<String> excluded = GraphBuilder.excludedAttribute(edgeList, answers, questionConcept);








                List<String> concepts = new ArrayList(answers);
                for(String s : DataLoader.parse(tokenized,srlPipeline)){
                    concepts.add(s);
                }
                System.out.println("test2: "+tokenized.toString());

                //Different startactivations

                System.out.print("Setting startmarkers...");
                List<Concept> allVerbsAndNouns = StartActivator.getAllVerbsAndNouns(tokenized,srlPipeline,stanPipeline,nerMap,answers);



                                try {
                                    Evaluator eva = new Evaluator();
                                    List<Map<Concept, List<? extends Marker>>> startActivation = null;

                                    startActivation = StartActivator.setStartActivation(StartActivator.getAllVerbsNounsAndAdjectives(tokenized, srlPipeline, stanPipeline, nerMap, answers));


                                    System.out.println("done.");

                                    System.out.print("Initializing markerpassing...");
                                    WinogradDoubleMarkerPassing doubleMarkerPassing = new WinogradDoubleMarkerPassing(decGraph, WinogradDoubleNode.class, syntaxGraph, nerGraph, roleGraph, new MarkerPassingConfig());
                                    System.out.println(doubleMarkerPassing.getNodes().size());
                                    System.out.println(doubleMarkerPassing.doubleActivation.size());
                                    WinogradDoubleMarkerPassing.doInitialMarking(startActivation, doubleMarkerPassing);
                                    System.out.print("done.");

                                    System.out.print("Executing markerpassing...");
                                    doubleMarkerPassing.execute();
                                    System.out.println("done.");
                                    System.out.println();
                                    return eva.summe(doubleMarkerPassing,answers);



                                }catch(Exception ex){
                                    ex.printStackTrace();

                                }





            }catch(Exception e){
                e.printStackTrace();
            }
            return null;

        }
}
