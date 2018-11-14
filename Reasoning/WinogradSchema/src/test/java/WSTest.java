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
import de.dailab.nsm.decomposition.Definition;
import de.dailab.nsm.decomposition.graph.conceptCache.GraphUtil;
import de.dailab.nsm.decomposition.graph.edges.WeightedEdge;
import de.dailab.nsm.decomposition.graph.entities.links.*;
import de.dailab.nsm.decomposition.graph.entities.marker.DoubleMarkerWithOrigin;
import de.dailab.nsm.decomposition.graph.entities.relations.Hyponym;
import de.dailab.nsm.decomposition.graph.spreadingActivation.MarkerPassing.MarkerPassingConfig;
import de.tuberlin.spreadalgo.Link;
import de.tuberlin.spreadalgo.Marker;
import de.tuberlin.spreadalgo.Node;
import edges.NerEdge;
import edges.RoleEdge;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.util.CoreMap;
import links.NerLink;
import links.RoleLink;
import links.SyntaxLink;
import org.jgrapht.Graph;
import org.jgrapht.graph.ListenableDirectedGraph;
import se.lth.cs.srl.CompletePipeline;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * Created by Hannes on 28.03.2017.
 */


public class WSTest {


    public static int synCounter = 0;
    public static int antCounter = 0;
    public static int hyperCounter = 0;
    public static int hypoCounter = 0;
    public static int nerCounter = 0;
    public static int roleCounter = 0;
    public static int defCounter = 0;
    public static int syntaxLinkCounter = 0;
    public static int twoLinks = 0;
    public static int threeLinks = 0;
    public static int fourLinks = 0;
    public static int fiveLinks = 0;
    public static int sixLinks = 0;
    public static Map<String, Integer> linkMap = new HashMap<>();
    public static Map<String, Integer> rightLinksMap = new HashMap<>();
    public static Map<String, Integer> wrongLinksMap = new HashMap<>();
    public static List<String> wrongSyntaxLinksList = new LinkedList<>();
    public static List<String> rightSyntaxLinksList = new LinkedList<>();
    public static int arbitCounter = 0;
    public static int wrongArbitCounter = 0;
    public static int wrongSynCounter = 0;
    public static int worongAntCounter = 0;
    public static int wrongHyperCounter = 0;
    public static int wrongHypoCounter = 0;
    public static int worngNerCounter = 0;
    public static int wrongRoleCounter = 0;
    public static int wrongDefCounter = 0;
    public static int wrongSyntaxLinkCounter = 0;
    public static int totaltwoLinks = 0;
    public static int totalthreeLinks = 0;
    public static int totalfourLinks = 0;
    public static int totalfiveLinks = 0;
    public static int totalsixLinks = 0;
    public static int totalsynCounter = 0;
    public static int totalantCounter = 0;
    public static int totalhyperCounter = 0;
    public static int totalhypoCounter = 0;
    public static int totalnerCounter = 0;
    public static int totalroleCounter = 0;
    public static int totaldefCounter = 0;
    public static int totalsyntaxLinkCounter = 0;
    static StanfordCoreNLP stanPipeline = null;
    static CompletePipeline srlPipeline = null;
    static HashMap<WinogradSchemaData, List<Graph>> cache = new HashMap<>();
    static Decomposition decomposition = null;

    public static PronConcept findPron(List<List<String>> tokenized, String pron, String quoteText, StanfordCoreNLP pipeline) {
        //gues forst pronoun occurence
        int wordNumber = 0;
        String[] words = quoteText.split("([\\W\\s]+)");
        Map<String, Integer> counts = new HashMap<String, Integer>();
        for (int i = 1; i < words.length; i++) {
            if (words[i].equals(pron)) {
                wordNumber = i;
            }
        }
        PronConcept pronConcept = new PronConcept(pron, wordNumber, 0);
        if (quoteText.equals("")) {
            int wordNR = -1;
            for (List<String> list: tokenized) {
                int i = 0;
                String[] prons = pron.split(" ");
                if (prons.length > 1) {
                    boolean foundfirst = false;
                    boolean foundsecond = false;
                    for (String word: list) {
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
                                return new PronConcept(pron, wordNR, 0);
                            }
                            break;
                        }
                        i++;
                    }
                } else {
                    for (String word: list) {
                        if (word.equals(pron)) {
                            wordNR = i;
                            return new PronConcept(pron, wordNR, 0);
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
                                if (sentence.get(k + j).equals(pron)) {
                                    temp = k + j;
                                }
                            } else {
                                break;
                            }
                            if (k == quote.size() - 1) {
                                pronConcept = new PronConcept(pron, temp, i);
                                return pronConcept;
                            }
                        }

                    }
                }
            }
        }
        return pronConcept;
    }

    /**
     * Build the syntactic graph.
     * @param document the text to build the syntax graph from.
     * @return A list of edge lists.
     */
    public static List<List<SemanticGraphEdge>> getSyntactics(Annotation document) {
        List<List<SemanticGraphEdge>> edgeList = new ArrayList<>();
        List<SemanticGraph> synGraphs = new ArrayList<>();

        //Erstellt für jeden Satz aus Schema einen semantischen Graphen
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);

        for (CoreMap sentence: sentences) {
            if (sentence.containsKey(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class)) {
                synGraphs.add(sentence.get(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class));
            }
        }

        //Sammelt die Edges eines jeden Graphen in einer Liste von Listen
        for (SemanticGraph dependencies: synGraphs) {
            List<SemanticGraphEdge> edges = dependencies.edgeListSorted();
            edgeList.add(edges);
        }

        return edgeList;
    }

    /**
     * This is an optimization. Answers which are not connected, (which are not logical) are excluded. This e.g. is the
     * case if the pronoun is before this anser posibility.
     * @param edgeList The edges to check
     * @param answerList the list of answer where one or more could be logically excluded
     * @param pron the pronoun we want to disambiguate
     * @return a list of excluded answers.
     */
    public static List<String> excludedAttribute(List<List<SemanticGraphEdge>> edgeList, List<String> answerList, PronConcept pron) {
        List<String> toExclude = new ArrayList<>();
        if (edgeList != null && edgeList.size() > 0) {
            List<SemanticGraphEdge> edges = edgeList.get(pron.getSatzNr());

            for (SemanticGraphEdge edge: edges) {
                String[] name = edge.getRelation().getShortName().split(("[\\p{Punct}]+"));
                String relationName = name[0];
                if (relationName.equals("prep")) {
                    IndexedWord source = edge.getSource();
                    IndexedWord target = edge.getTarget();
                    if (source.index() - 1 == pron.getWortNr() || target.index() - 1 == pron.getWortNr()) {
                        for (String answer: answerList) {
                            Definition ansDef = new Definition(answer);
                            for (Concept ansCon: ansDef.getDefinition()) {
                                if (target.lemma().equals(ansCon.getLemma()) || source.lemma().equals(ansCon.getLemma())) {
                                    toExclude.add(answer);
                                    System.out.println("EXCLUDED ANSWER: " + answer);
                                }
                            }
                        }
                    }
                }

            }
        }
        return toExclude;

    }

    public static Graph getSyntaxGraph(List<List<SemanticGraphEdge>> edgeList, PronConcept pron) {

        List<Graph> graphs = WinogradGraphUtil.synEdgesToGraphs(edgeList, pron);

        Graph mergedGraph = WinogradGraphUtil.mergeAllSynGraphs(graphs);

        return mergedGraph;
    }

    public static Map<String, String> getNerMap(Annotation document) {
        Map<String, String> nerMap = new HashMap<>();
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
        for (CoreMap sentence: sentences) {
            for (CoreLabel token: sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                String ne = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
                if (!ne.equals("O")) {
                    ne = ne.toLowerCase();
                    nerMap.put(token.lemma(), ne);
                }
            }
        }
        return nerMap;
    }

    public static Graph getNerGraph(Map<String, String> nerMap) {
        List<Graph> graphs = new ArrayList<>();
        for (String word: nerMap.keySet()) {
            Concept source = new Concept(word);
            String lemma = nerMap.get(word);

            Definition targetdef = new Definition(lemma);
            for (Concept target: targetdef.getDefinition()) {
                //graphs.add(GraphUtil.getGraph(targetcon.getLitheral(), targetcon.getWordType(), MarkerPassingConfig.getDecompositionDepth()));
                ListenableDirectedGraph graph = new ListenableDirectedGraph(WeightedEdge.class);
                graph.addVertex(source);
                graph.addVertex(target);
                NerEdge nerEdge = new NerEdge();
                nerEdge.setSource(source);
                nerEdge.setTarget(target);

                graph.addEdge(source, target, nerEdge);
                graphs.add(graph);
            }
        }
        return WinogradGraphUtil.mergeAllNerGraphs(graphs);
    }

    public static Graph getRoleGraph(Map<List<Concept>, List<String>> roles) {
        List<Graph> graphList = new ArrayList<>();

        //Für jedes Argument von Prädikat
        for (List<Concept> phrase: roles.keySet()) {
            //Für jedes Wort in Argumentphrase
            for (Concept word: phrase) {
                if ((!Decomposition.getConcepts2Ignore().contains(word))) {
                    //Für jede für Argument vorgesehene Rolle
                    for (String role: roles.get(phrase)) {
                        Definition roleDef = new Definition(role);
                        //Für jedes Concept in Rollenbeschreibung
                        for (Concept roleCon: roleDef.getDefinition()) {
                            if ((!Decomposition.getConcepts2Ignore().contains(roleCon))) {
                                ListenableDirectedGraph graph = new ListenableDirectedGraph(RoleEdge.class);
                                graph.addVertex(word);
                                graph.addVertex(roleCon);
                                RoleEdge roleEdge = new RoleEdge();
                                if (roles.get(phrase).indexOf(role) == 0) {
                                    roleEdge.setRoleType("role");
                                } else {
                                    roleEdge.setRoleType("vnrole");
                                }
                                roleEdge.setSource(word);
                                roleEdge.setTarget(roleCon);
                                graph.addEdge(word, roleCon, roleEdge);
                                graphList.add(graph);
                            }
                        }
                    }

                }
            }
        }


        return WinogradGraphUtil.mergeAllRoleGraphs(graphList);
    }

    public static Graph getDecGraph(List<List<String>> schema, Map<List<Concept>, List<String>> roleMap, Map<String, String> nerMap, PronConcept pron) {

        List<Graph> graphs = new ArrayList<>();

        for (List<String> sentence: schema) {
            for (String word: sentence) {
                Definition wordDef = new Definition(word);
                for (Concept wordCon: wordDef.getDefinition()) {
                    if (!Decomposition.getConcepts2Ignore().contains(wordCon) && !nerMap.containsKey(wordCon.getLitheral()) && !wordCon.getLitheral().equals(pron.getLitheral())) {
                        graphs.add(GraphUtil.getGraph(wordCon.getLitheral(), wordCon.getWordType(), MarkerPassingConfig.getDecompositionDepth()));
                    }
                }
            }
        }

        for (List<Concept> phrase: roleMap.keySet()) {
            List<String> roles = roleMap.get(phrase);
            for (String role: roles) {
                Definition roleDef = new Definition(role);
                for (Concept roleCon: roleDef.getDefinition()) {
                    if (!Decomposition.getConcepts2Ignore().contains(roleCon)) {
                        graphs.add(GraphUtil.getGraph(roleCon.getLitheral(), roleCon.getWordType(), MarkerPassingConfig.getDecompositionDepth()));
                    }
                }
            }
        }

        for (String name: nerMap.keySet()) {
            String entity = nerMap.get(name);
            Definition entDef = new Definition(entity);
            for (Concept entCon: entDef.getDefinition()) {
                graphs.add(GraphUtil.getGraph(entCon.getLitheral(), entCon.getWordType(), MarkerPassingConfig.getDecompositionDepth()));
            }
        }

        return WinogradGraphUtil.mergeDecGraphs(graphs);
    }

    private static List<Map<Concept, List<? extends Marker>>> setStartActivation(PronConcept pron, MarkerPassingConfig config) {
        List<Map<Concept, List<? extends Marker>>> startActivation = new ArrayList<>();
        Map<Concept, List<? extends Marker>> conceptMarkerMap = new HashMap<>();
        List<Marker> markers1 = new ArrayList<>();
        DoubleMarkerWithOrigin startMarker = new DoubleMarkerWithOrigin();
        startMarker.setActivation(config.getStartActivation());

        startMarker.setOrigin(pron);
        markers1.add(startMarker);
        conceptMarkerMap.put(pron, markers1);
        startActivation.add(conceptMarkerMap);

        return startActivation;

    }

    public static List<Map<Concept, List<? extends Marker>>> setStartActivation(PronConcept pron, List<Concept> answers, MarkerPassingConfig config) {
        List<Map<Concept, List<? extends Marker>>> startActivation = new ArrayList<>();
        Map<Concept, List<? extends Marker>> conceptMarkerMap = new HashMap<>();
        List<Marker> markers1 = new ArrayList<>();
        DoubleMarkerWithOrigin startMarker = new DoubleMarkerWithOrigin();
        startMarker.setActivation(config.getStartActivation());
        startMarker.setActivation(MarkerPassingConfig.getStartActivation());

        startMarker.setOrigin(pron);
        startMarker.setAnswers(answers);
        markers1.add(startMarker);
        conceptMarkerMap.put(pron, markers1);
        startActivation.add(conceptMarkerMap);

        return startActivation;

    }

    public static List<Map<Concept, List<? extends Marker>>> setStartActivation(List<Concept> conceptsToActivate, MarkerPassingConfig config) {
        List<Map<Concept, List<? extends Marker>>> startActivation = new ArrayList<>();
        for (Concept c: conceptsToActivate) {
            Map<Concept, List<? extends Marker>> conceptMarkerMap = new HashMap<>();
            List<Marker> markers = new ArrayList<>();
            DoubleMarkerWithOrigin startMarker = new DoubleMarkerWithOrigin();
            //TODO: Discuss Why thousand times Markerpassing.getStartActivation?
            startMarker.setActivation(1000 * config.getStartActivation());
            startMarker.setActivation(MarkerPassingConfig.getStartActivation());
            startMarker.setOrigin(c);
            markers.add(startMarker);
            conceptMarkerMap.put(c, markers);
            startActivation.add(conceptMarkerMap);
        }

        return startActivation;

    }

    public static Map<String, Double> summe(WinogradDoubleMarkerPassing doubleMarkerPassing, WinogradSchemaData schema) throws FileNotFoundException {
        Collection<Node> activeNodes = doubleMarkerPassing.getActiveNodes();
        List<String> answersList = schema.getAnswers();
        Map<String, Double> answerMap = new HashMap<>(answersList.size());


        //Set ResultMap to Zero
        for (String answer: answersList) {
            answerMap.put(answer, 0.0);
        }
        for (Node knoten: activeNodes) {
            //eine summe an noch verbliebenen marker auf jedem je aktivierten knoten
            double sum = 0;

            //Für alle knoten die in activeNodes existieren(???)
            if (knoten != null) {
                WinogradDoubleNode dnode = (WinogradDoubleNode) knoten;
                //System.out.println(dnode.getConcept().getLitheral());
                //Für Marker jeder Herkunft die darauf liegen
                int markerCounter = 0;
                for (Marker marker: dnode.getActivationHistory()) {
                    DoubleMarkerWithOrigin dmarker = (DoubleMarkerWithOrigin) marker;
                    // /Wenn marker darauf > 0 , dann füge befindliche Aktivierung der summe hinzu
                    if (dmarker.getActivation() > 0) {
                        sum = sum + dmarker.getActivation();
                    }
                }


                //Für jede antwort die in Antwortmap existiert
                for (String answer: answerMap.keySet()) {

                    Definition def = new Definition(answer);

                    //Für jedes Wort der Antwort
                    for (Concept con: def.getDefinition()) {

                        //Wenn das Wort kein Stopwort ist
                        if (!Decomposition.getConcepts2Ignore().contains(con)) {
                            //Wenn der aktuell betrachtete Knoten mit dem betrachteten Antwort-Wort übereinstimmt, wird der resultMap hinzugefügt
                            if (dnode.getConcept().equals(con)) {
                                Double answerSum = answerMap.get(answer) + sum;
                                answerMap.put(answer, answerSum);
                                for (Marker marker: dnode.getActivationHistory()) {
                                    //System.out.println("--------New Marker-------------------");
                                    //System.out.println("Node: " + con.getLitheral());
                                    //System.out.println("Visited Nodes: ");
                                    DoubleMarkerWithOrigin dmarker = (DoubleMarkerWithOrigin) marker;
                                    for (Concept c: dmarker.getVisitedConcepts()) {
                                        //System.out.println(c.getLitheral());
                                    }
                                    if (dmarker.getVisitedLinks().size() == 2) {
                                        twoLinks++;
                                    }
                                    if (dmarker.getVisitedLinks().size() == 3) {
                                        threeLinks++;
                                    }
                                    if (dmarker.getVisitedLinks().size() == 4) {
                                        fourLinks++;
                                    }
                                    if (dmarker.getVisitedLinks().size() == 5) {
                                        fiveLinks++;
                                    }
                                    if (dmarker.getVisitedLinks().size() == 6) {
                                        sixLinks++;
                                    }

                                    String linksConcat = "";
                                    for (Link l: dmarker.getVisitedLinks()) {
                                        linksConcat = linksConcat + "|" + getLinkType(l);

                                        if (l instanceof SyntaxLink) {
                                            //System.out.println(((SyntaxLink) l).getName());
                                            WinogradDoubleNode d = (WinogradDoubleNode) l.getSource();
                                            WinogradDoubleNode e = (WinogradDoubleNode) l.getTarget();
                                            //System.out.println("Source: " + d.getConcept().getLitheral());
                                            //System.out.println("Target: " + e.getConcept().getLitheral());
                                            syntaxLinkCounter++;
                                        }
                                        if (l instanceof ArbitraryRelationLink) {
                                            //System.out.println(((ArbitraryRelationLink) l).getRelationName());
                                            WinogradDoubleNode d = (WinogradDoubleNode) l.getSource();
                                            WinogradDoubleNode e = (WinogradDoubleNode) l.getTarget();
                                            //System.out.println("Source: " + d.getConcept().getLitheral());
                                            //System.out.println("Target: " + e.getConcept().getLitheral());
                                        }
                                        if (l instanceof SynonymLink) {
                                            //System.out.println("syn");
                                            WinogradDoubleNode d = (WinogradDoubleNode) l.getSource();
                                            WinogradDoubleNode e = (WinogradDoubleNode) l.getTarget();
                                            //System.out.println("Source: " + d.getConcept().getLitheral());
                                            //System.out.println("Target: " + e.getConcept().getLitheral());
                                            synCounter++;
                                        }
                                        if (l instanceof AntonymLink) {
                                            //System.out.println("ant");
                                            WinogradDoubleNode d = (WinogradDoubleNode) l.getSource();
                                            WinogradDoubleNode e = (WinogradDoubleNode) l.getTarget();
                                            //System.out.println("Source: " + d.getConcept().getLitheral());
                                            //System.out.println("Target: " + e.getConcept().getLitheral());
                                            antCounter++;
                                        }
                                        if (l instanceof HypernymLink) {
                                            //System.out.println("hyper");
                                            WinogradDoubleNode d = (WinogradDoubleNode) l.getSource();
                                            WinogradDoubleNode e = (WinogradDoubleNode) l.getTarget();
                                            //System.out.println("Source: " + d.getConcept().getLitheral());
                                            //System.out.println("Target: " + e.getConcept().getLitheral());
                                            hyperCounter++;
                                        }
                                        if (l instanceof Hyponym) {
                                            System.out.println("Hypo");
                                            WinogradDoubleNode d = (WinogradDoubleNode) l.getSource();
                                            WinogradDoubleNode e = (WinogradDoubleNode) l.getTarget();
                                            //System.out.println("Source: " + d.getConcept().getLitheral());
                                            //System.out.println("Target: " + e.getConcept().getLitheral());
                                            hypoCounter++;
                                        }
                                        if (l instanceof NerLink) {
                                            //System.out.println("ner");
                                            WinogradDoubleNode d = (WinogradDoubleNode) l.getSource();
                                            WinogradDoubleNode e = (WinogradDoubleNode) l.getTarget();
                                            //System.out.println("Source: " + d.getConcept().getLitheral());
                                            //System.out.println("Target: " + e.getConcept().getLitheral());
                                            nerCounter++;
                                        }
                                        if (l instanceof RoleLink) {
                                            //System.out.println("role");
                                            WinogradDoubleNode d = (WinogradDoubleNode) l.getSource();
                                            WinogradDoubleNode e = (WinogradDoubleNode) l.getTarget();
                                            //System.out.println("Source: " + d.getConcept().getLitheral());
                                            //System.out.println("Target: " + e.getConcept().getLitheral());
                                            roleCounter++;
                                        }
                                        if (l instanceof DefinitionLink) {
                                            //System.out.println("def");
                                            WinogradDoubleNode d = (WinogradDoubleNode) l.getSource();
                                            WinogradDoubleNode e = (WinogradDoubleNode) l.getTarget();
                                            //System.out.println("Source: " + d.getConcept().getLitheral());
                                            //System.out.println("Target: " + e.getConcept().getLitheral());
                                            defCounter++;
                                        }

                                    }
                                    if (!linkMap.containsKey(linksConcat)) {
                                        linkMap.put(linksConcat, 1);
                                    } else {
                                        int oldInt = linkMap.get(linksConcat);
                                        int newInt = oldInt + 1;
                                        linkMap.put(linksConcat, newInt);
                                    }
                                }

                            }
                        }
                    }
                }

            }
        }
        schema.setAnswerMap(answerMap);
        return answerMap;
    }

    public static String getLinkType(Link l) {
        String returnString = "other";
        if (l instanceof SyntaxLink) {
            //System.out.println(((SyntaxLink) l).getName());
            WinogradDoubleNode d = (WinogradDoubleNode) l.getSource();
            WinogradDoubleNode e = (WinogradDoubleNode) l.getTarget();
            returnString = "syntax: " + ((SyntaxLink) l).getName();
        }
        if (l instanceof ArbitraryRelationLink) {
            //System.out.println(((ArbitraryRelationLink) l).getRelationName());
            WinogradDoubleNode d = (WinogradDoubleNode) l.getSource();
            WinogradDoubleNode e = (WinogradDoubleNode) l.getTarget();
            returnString = "arbitrary: " + ((ArbitraryRelationLink) l).getRelationName();
        }
        if (l instanceof SynonymLink) {
            WinogradDoubleNode d = (WinogradDoubleNode) l.getSource();
            WinogradDoubleNode e = (WinogradDoubleNode) l.getTarget();
            returnString = "synonym";
        }
        if (l instanceof AntonymLink) {
            WinogradDoubleNode d = (WinogradDoubleNode) l.getSource();
            WinogradDoubleNode e = (WinogradDoubleNode) l.getTarget();
            returnString = "antonym";
        }
        if (l instanceof HypernymLink) {
            WinogradDoubleNode d = (WinogradDoubleNode) l.getSource();
            WinogradDoubleNode e = (WinogradDoubleNode) l.getTarget();
            returnString = "hypernym";
        }
        if (l instanceof Hyponym) {
            WinogradDoubleNode d = (WinogradDoubleNode) l.getSource();
            WinogradDoubleNode e = (WinogradDoubleNode) l.getTarget();
            returnString = "hyponym";
        }
        if (l instanceof NerLink) {
            WinogradDoubleNode d = (WinogradDoubleNode) l.getSource();
            WinogradDoubleNode e = (WinogradDoubleNode) l.getTarget();
            returnString = "ner";
        }
        if (l instanceof RoleLink) {
            WinogradDoubleNode d = (WinogradDoubleNode) l.getSource();
            WinogradDoubleNode e = (WinogradDoubleNode) l.getTarget();
            returnString = "role";
        }
        if (l instanceof DefinitionLink) {
            WinogradDoubleNode d = (WinogradDoubleNode) l.getSource();
            WinogradDoubleNode e = (WinogradDoubleNode) l.getTarget();
            returnString = "definition";
        }
        return returnString;

    }

    public static int findCollisions(WinogradDoubleMarkerPassing doubleMarkerPassing, PronConcept pronConcept, List<String> answers, String rightAnswer) throws FileNotFoundException {
        Collection<Node> activeNodes = doubleMarkerPassing.getActiveNodes();

        int activeNodeCounter = 0;
        int collisionCounter = 0;
        for (Node knoten: activeNodes) {
            //eine summe an noch verbliebenen marker auf jedem je aktivierten knoten
            double sum = 0;

            //Für alle knoten die in activeNodes existieren(???)
            if (knoten != null) {
                activeNodeCounter++;
                WinogradDoubleNode dnode = (WinogradDoubleNode) knoten;
                //System.out.println(dnode.getConcept().getLitheral());
                //Für Marker jeder Herkunft die darauf liegen
                int markerCounter = 0;
                for (Marker marker: dnode.getActivationHistory()) {
                    DoubleMarkerWithOrigin dmarker = (DoubleMarkerWithOrigin) marker;
                    // /Wenn marker darauf > 0 , dann füge befindliche Aktivierung der summe hinzu
                    if (dmarker.getActivation() > 0) {
                        sum = sum + dmarker.getActivation();
                    }
                }

                List<Link> linksVisitedByAnswerMarker = new LinkedList<>();
                List<Concept> conceptsVisitedByAnswerMarker = new LinkedList<>();
                List<Link> linksVisitedByPronMarker = new LinkedList<>();
                List<Concept> conceptsVisitedByPronMarker = new LinkedList<>();

                //Definition def = new Definition(answer);
                boolean containsAnswer = false;
                boolean containsPron = false;
                boolean isRightAnswer = false;
                for (Marker marker: dnode.getActivationHistory()) {
                    DoubleMarkerWithOrigin dmarker = (DoubleMarkerWithOrigin) marker;
                    if (answers.contains(dmarker.getOrigin().getLitheral())) {
                        if (dmarker.getOrigin().getLitheral().equals(rightAnswer)) {
                            isRightAnswer = true;
                        }
                        containsAnswer = true;
                        linksVisitedByAnswerMarker = dmarker.getVisitedLinks();
                        conceptsVisitedByAnswerMarker = dmarker.getVisitedConcepts();
                    }
                    if (dmarker.getOrigin().getLitheral().equals(pronConcept.getLitheral())) {
                        containsPron = true;
                        linksVisitedByPronMarker = dmarker.getVisitedLinks();
                        conceptsVisitedByPronMarker = dmarker.getVisitedConcepts();
                    }
                }
                if (containsAnswer && containsPron) {
                    System.out.println("----------------------ATTENTION!!!----------------------------------------");
                    System.out.println("We have a collision at " + dnode.getConcept().getLitheral());
                    collisionCounter++;
                    if (isRightAnswer) {
                        System.out.println("BINGO COLLISION!");
                    }
                    /*System.out.println("Links visited by answerMarker: ");
                    for (Link link : linksVisitedByAnswerMarker){
                        System.out.println("LinkType: "+ getLinkType(link));
                    }*/
                }


                //}
            }

        }
        System.out.println("Total nodes checked: " + activeNodeCounter);
        System.out.println("Colliosions found: " + collisionCounter);
        return collisionCounter;
    }

    public static void grabOnlyPron(WinogradDoubleMarkerPassing doubleMarkerPassing, PronConcept pronConcept, List<String> answers, String rightAnswer) throws FileNotFoundException {
        Collection<Node> activeNodes = doubleMarkerPassing.getActiveNodes();
        //Map<String, Double> answerMap=new HashMap<>(answersList.size());

        for (Node knoten: activeNodes) {
            //eine summe an noch verbliebenen marker auf jedem je aktivierten knoten
            double sum = 0;

            //Für alle knoten die in activeNodes existieren(???)
            if (knoten != null) {
                WinogradDoubleNode dnode = (WinogradDoubleNode) knoten;
                //Für Marker jeder Herkunft die darauf liegen
                int markerCounter = 0;
                for (Marker marker: dnode.getActivationHistory()) {
                    DoubleMarkerWithOrigin dmarker = (DoubleMarkerWithOrigin) marker;
                    // /Wenn marker darauf > 0 , dann füge befindliche Aktivierung der summe hinzu
                    if (dmarker.getActivation() > 0) {
                        sum = sum + dmarker.getActivation();
                    }
                }


                //Für jedes Wort der Antwort
                //for (Concept con : def.getDefinition())
                int totalMarkerCounter = 0;
                double markerEnergyCounter = 0;
                double totalEnergyCounter = 0;
                int rightMarkerCounter = 0;
                String pron = pronConcept.getOriginalName();
                Definition def = new Definition(pron);
                for (Concept con: def.getDefinition()) {
                    //Wenn der aktuell betrachtete Knoten mit dem Pronomen übereinstimmt
                    if (dnode.getConcept().equals(con)) {

                        for (Marker marker: dnode.getActivationHistory()) {
                            DoubleMarkerWithOrigin dmarker = (DoubleMarkerWithOrigin) marker;
                            totalMarkerCounter++;
                            totalEnergyCounter = totalEnergyCounter + dmarker.getActivation();
                            //System.out.println("Marker: "+dmarker.getOrigin().getLitheral());
                            //System.out.println("Answer: "+rightAnswer);
                            //if (dmarker.getOrigin().getLitheral().equals(rightAnswer)){
                            if (rightAnswer.toLowerCase().contains(dmarker.getOrigin().getLitheral().toLowerCase()) || dmarker.getOrigin().getLitheral().toLowerCase().contains(rightAnswer.toLowerCase())) {
                                rightMarkerCounter++;
                                markerEnergyCounter = markerEnergyCounter + dmarker.getActivation();
                            }
                            System.out.println("--------New Marker-------------------");
                            System.out.println("Node: " + pron);
                            System.out.println("origin: " + dmarker.getOrigin());
                            System.out.println("Visited Nodes: ");
                            for (Concept c: dmarker.getVisitedConcepts()) {
                                System.out.println(c.getLitheral());
                            }
                                        /*System.out.println("visited Links: ");

                                        for (Link l : dmarker.getVisitedLinks()) {
                                            if (l instanceof SyntaxLink) {
                                                System.out.println(((SyntaxLink) l).getName());
                                                WinogradDoubleNode d = (WinogradDoubleNode) l.getSource();
                                                WinogradDoubleNode e = (WinogradDoubleNode) l.getTarget();
                                                System.out.println("Source: " + d.getConcept().getLitheral());
                                                System.out.println("Target: " + e.getConcept().getLitheral());
                                                syntaxLinkCounter++;
                                                rightLinksMap.put("synt",syntaxLinkCounter);
                                                rightSyntaxLinksList.add(((SyntaxLink) l).getName());
                                            }
                                            if (l instanceof ArbitraryRelationLink) {
                                                System.out.println(((ArbitraryRelationLink) l).getRelationName());
                                                WinogradDoubleNode d = (WinogradDoubleNode) l.getSource();
                                                WinogradDoubleNode e = (WinogradDoubleNode) l.getTarget();
                                                System.out.println("Source: " + d.getConcept().getLitheral());
                                                System.out.println("Target: " + e.getConcept().getLitheral());
                                                arbitCounter++;
                                                rightLinksMap.put("arbit",arbitCounter);
                                            }
                                            if (l instanceof SynonymLink) {
                                                System.out.println("syn");
                                                WinogradDoubleNode d = (WinogradDoubleNode) l.getSource();
                                                WinogradDoubleNode e = (WinogradDoubleNode) l.getTarget();
                                                System.out.println("Source: " + d.getConcept().getLitheral());
                                                System.out.println("Target: " + e.getConcept().getLitheral());
                                                synCounter++;
                                                rightLinksMap.put("syn",synCounter);
                                            }
                                            if (l instanceof AntonymLink) {
                                                System.out.println("ant");
                                                WinogradDoubleNode d = (WinogradDoubleNode) l.getSource();
                                                WinogradDoubleNode e = (WinogradDoubleNode) l.getTarget();
                                                System.out.println("Source: " + d.getConcept().getLitheral());
                                                System.out.println("Target: " + e.getConcept().getLitheral());
                                                antCounter++;
                                                rightLinksMap.put("ant",antCounter);
                                            }
                                            if (l instanceof HypernymLink) {
                                                System.out.println("hyper");
                                                WinogradDoubleNode d = (WinogradDoubleNode) l.getSource();
                                                WinogradDoubleNode e = (WinogradDoubleNode) l.getTarget();
                                                System.out.println("Source: " + d.getConcept().getLitheral());
                                                System.out.println("Target: " + e.getConcept().getLitheral());
                                                hyperCounter++;
                                                rightLinksMap.put("hyper",hyperCounter);
                                            }
                                            if (l instanceof Hyponym) {
                                                System.out.println("Hypo");
                                                WinogradDoubleNode d = (WinogradDoubleNode) l.getSource();
                                                WinogradDoubleNode e = (WinogradDoubleNode) l.getTarget();
                                                System.out.println("Source: " + d.getConcept().getLitheral());
                                                System.out.println("Target: " + e.getConcept().getLitheral());
                                                hypoCounter++;
                                                rightLinksMap.put("hypo",hyperCounter);
                                            }
                                            if (l instanceof NerLink) {
                                                System.out.println("ner");
                                                WinogradDoubleNode d = (WinogradDoubleNode) l.getSource();
                                                WinogradDoubleNode e = (WinogradDoubleNode) l.getTarget();
                                                System.out.println("Source: " + d.getConcept().getLitheral());
                                                System.out.println("Target: " + e.getConcept().getLitheral());
                                                nerCounter++;
                                                rightLinksMap.put("ner",nerCounter);
                                            }
                                            if (l instanceof RoleLink) {
                                                System.out.println("role");
                                                WinogradDoubleNode d = (WinogradDoubleNode) l.getSource();
                                                WinogradDoubleNode e = (WinogradDoubleNode) l.getTarget();
                                                System.out.println("Source: " + d.getConcept().getLitheral());
                                                System.out.println("Target: " + e.getConcept().getLitheral());
                                                roleCounter++;
                                                rightLinksMap.put("role",roleCounter);
                                            }
                                            if (l instanceof DefinitionLink) {
                                                System.out.println("def");
                                                WinogradDoubleNode d = (WinogradDoubleNode) l.getSource();
                                                WinogradDoubleNode e = (WinogradDoubleNode) l.getTarget();
                                                System.out.println("Source: " + d.getConcept().getLitheral());
                                                System.out.println("Target: " + e.getConcept().getLitheral());
                                                defCounter++;
                                                rightLinksMap.put("def",defCounter);
                                            }

                                        }


                                    }*/
                        }
                        System.out.println("Total Markers: " + totalMarkerCounter + " \nRatio Total/Right: " + totalMarkerCounter + "/" + rightMarkerCounter);
                        System.out.println("Total Energy: " + totalEnergyCounter + " \nRatio Total/Right: " + totalEnergyCounter + "/" + markerEnergyCounter);
                        if ((markerEnergyCounter / totalEnergyCounter) > 0.5) {
                            System.out.println("Majority Right!");
                        }

                    }

                }
            }
        }
    }

    public static Map<String, Integer> whereDoTheAnswerMarkersGo(WinogradDoubleMarkerPassing doubleMarkerPassing, PronConcept pronConcept, List<String> answers, String rightAnswer, Map<String, Integer> linkStatisticsMap, Map<String, String> nerMap) throws FileNotFoundException, UnsupportedEncodingException, IOException {
        Collection<Node> activeNodes = doubleMarkerPassing.getActiveNodes();
        int totalNodeCount = 0;
        Map<String, Integer> perSchemaLinkMap = new HashMap<>();
        StanfordCoreNLP stanPipeline = AnalyseUtil.getPipeline();
        List<String> lemmatizedAnswers = new LinkedList<>();

        for (Node knoten: activeNodes) {
            //eine summe an noch verbliebenen marker auf jedem je aktivierten knoten
            double sum = 0;

            //Für alle knoten die in activeNodes existieren(???)
            if (knoten != null) {
                totalNodeCount++;
                WinogradDoubleNode dnode = (WinogradDoubleNode) knoten;
                //System.out.println(dnode.getConcept().getLitheral());
                //Für Marker jeder Herkunft die darauf liegen
                int markerCounter = 0;
                for (Marker marker: dnode.getActivationHistory()) {
                    DoubleMarkerWithOrigin dmarker = (DoubleMarkerWithOrigin) marker;
                    // /Wenn marker darauf > 0 , dann füge befindliche Aktivierung der summe hinzu
                    if (dmarker.getActivation() > 0) {
                        sum = sum + dmarker.getActivation();
                    }
                }

                List<Link> linksVisitedByAnswerMarker = new LinkedList<>();
                List<Concept> conceptsVisitedByAnswerMarker = new LinkedList<>();

                List<String> tokenizedAnswers = new LinkedList<>();
                for (String answer: answers) {
                    if (answer.contains(" ")) {
                        String[] answerArray = answer.split(" ");
                        tokenizedAnswers.addAll(Arrays.asList(answerArray));
                    } else {
                        tokenizedAnswers.add(answer);
                    }
                }


                for (String answer: tokenizedAnswers) {
                    if (nerMap.keySet().contains(answer)) {
                        answer = nerMap.get(answer);
                        lemmatizedAnswers.add(lemmatize(answer, stanPipeline));
                    } else {
                        answer = lemmatize(answer, stanPipeline);
                        lemmatizedAnswers.add(answer);
                    }
                }

                //Definition def = new Definition(answer);
                String origin = "";
                for (Marker marker: dnode.getActivationHistory()) {
                    DoubleMarkerWithOrigin dmarker = (DoubleMarkerWithOrigin) marker;
                    if (dmarker.getOrigin().getLitheral().contains("_")) {
                        origin = dmarker.getOrigin().getLitheral().split("_")[0];
                    } else {
                        origin = dmarker.getOrigin().getLitheral();
                    }

                    if (lemmatizedAnswers.contains(origin)) {
                        linksVisitedByAnswerMarker = dmarker.getVisitedLinks();
                        conceptsVisitedByAnswerMarker = dmarker.getVisitedConcepts();
                    }
                }

                if (!linksVisitedByAnswerMarker.isEmpty()) {
                    //linkStatisticsMap.putAll(recordLinkStatistics(linksVisitedByPronMarker));
                    for (Map.Entry<String, Integer> entry: recordLinkStatistics(linksVisitedByAnswerMarker).entrySet()) {
                        if (perSchemaLinkMap.containsKey(entry.getKey())) {
                            int oldValue = perSchemaLinkMap.get(entry.getKey());
                            int newValue = oldValue + entry.getValue();
                            perSchemaLinkMap.put(entry.getKey(), newValue);
                        } else {
                            perSchemaLinkMap.put(entry.getKey(), entry.getValue());
                        }
                    }
                }

                if (!linksVisitedByAnswerMarker.isEmpty()) {
                    for (Map.Entry<String, Integer> entry: recordLinkStatistics(linksVisitedByAnswerMarker).entrySet()) {
                        if (linkStatisticsMap.containsKey(entry.getKey())) {
                            int oldValue = linkStatisticsMap.get(entry.getKey());
                            int newValue = oldValue + entry.getValue();
                            linkStatisticsMap.put(entry.getKey(), newValue);
                        } else {
                            linkStatisticsMap.put(entry.getKey(), entry.getValue());
                        }
                    }
                }
            }

        }
        linkStatisticsMap = sortByValue(perSchemaLinkMap);
        for (Map.Entry<String, Integer> entry: perSchemaLinkMap.entrySet()) {
            System.out.append("\nLink: " + entry.getKey() + " Count: " + entry.getValue());
        }

        /*for (String s : lemmatizedAnswers){
            System.out.println("Lemmatized Answers: "+s);
        }*/
        return linkStatisticsMap;
    }

    public static Map<String, Integer> whereDoThePronounMarkersGo(WinogradDoubleMarkerPassing doubleMarkerPassing, PronConcept pronConcept, List<String> answers, String rightAnswer, Map<String, Integer> linkStatisticsMap) throws FileNotFoundException, UnsupportedEncodingException, IOException {
        Map<String, Integer> perSchemaLinkMap = new HashMap<>();
        float totalMarkerCount = 0;
        float jumpsMarkerCount = 0;

        Collection<Node> activeNodes = doubleMarkerPassing.getActiveNodes();
        int totalNodeCount = 0;

        for (Node knoten: activeNodes) {
            //eine summe an noch verbliebenen marker auf jedem je aktivierten knoten
            double sum = 0;

            //Für alle knoten die in activeNodes existieren(???)
            if (knoten != null) {
                totalNodeCount++;
                WinogradDoubleNode dnode = (WinogradDoubleNode) knoten;
                //System.out.println(dnode.getConcept().getLitheral());
                //Für Marker jeder Herkunft die darauf liegen
                int markerCounter = 0;
                for (Marker marker: dnode.getActivationHistory()) {
                    DoubleMarkerWithOrigin dmarker = (DoubleMarkerWithOrigin) marker;
                    // /Wenn marker darauf > 0 , dann füge befindliche Aktivierung der summe hinzu
                    if (dmarker.getActivation() > 0) {
                        sum = sum + dmarker.getActivation();
                    }
                }

                //List<Link> linksVisitedByAnswerMarker = new LinkedList<>();
                //List<Concept> conceptsVisitedByAnswerMarker = new LinkedList<>();
                List<Link> linksVisitedByPronMarker = new LinkedList<>();
                List<Concept> conceptsVisitedByPronMarker = new LinkedList<>();

                //Definition def = new Definition(answer);
                for (Marker marker: dnode.getActivationHistory()) {
                    totalMarkerCount++;
                    DoubleMarkerWithOrigin dmarker = (DoubleMarkerWithOrigin) marker;
                    if (dmarker.getOrigin().getLitheral().equals(pronConcept.getLitheral())) {
                        linksVisitedByPronMarker = dmarker.getVisitedLinks();
                        conceptsVisitedByPronMarker = dmarker.getVisitedConcepts();
                    }
                }

                for (Concept c: conceptsVisitedByPronMarker) {
                    if (conceptsVisitedByPronMarker.contains(dnode.getConcept())) {
                        jumpsMarkerCount++;
                    }
                }

                if (!linksVisitedByPronMarker.isEmpty()) {
                    //linkStatisticsMap.putAll(recordLinkStatistics(linksVisitedByPronMarker));
                    for (Map.Entry<String, Integer> entry: recordLinkStatistics(linksVisitedByPronMarker).entrySet()) {
                        if (perSchemaLinkMap.containsKey(entry.getKey())) {
                            int oldValue = perSchemaLinkMap.get(entry.getKey());
                            int newValue = oldValue + entry.getValue();
                            perSchemaLinkMap.put(entry.getKey(), newValue);
                        } else {
                            perSchemaLinkMap.put(entry.getKey(), entry.getValue());
                        }
                    }
                }

                if (!linksVisitedByPronMarker.isEmpty()) {
                    //linkStatisticsMap.putAll(recordLinkStatistics(linksVisitedByPronMarker));
                    for (Map.Entry<String, Integer> entry: recordLinkStatistics(linksVisitedByPronMarker).entrySet()) {
                        if (linkStatisticsMap.containsKey(entry.getKey())) {
                            int oldValue = linkStatisticsMap.get(entry.getKey());
                            int newValue = oldValue + entry.getValue();
                            linkStatisticsMap.put(entry.getKey(), newValue);
                        } else {
                            linkStatisticsMap.put(entry.getKey(), entry.getValue());
                        }
                    }
                }
            }

        }
        /*for(Map.Entry<String, Integer> entry : perSchemaLinkMap.entrySet()){
            System.out.println("Link: "+entry.getKey()+" Count: "+entry.getValue());
        }*/
        linkStatisticsMap = sortByValue(linkStatisticsMap);
        //System.out.println("Total markers: "+totalMarkerCount+" JumpMarkers: "+jumpsMarkerCount);
        //System.out.println("Percentage: "+(jumpsMarkerCount/totalMarkerCount));
        return linkStatisticsMap;
    }

    public static Map<String, Integer> recordLinkStatistics(List<Link> linkList) {
        Map<String, Integer> linkStatisticsMap = new HashMap<>();

        for (Link link: linkList) {
            String linkType = getLinkType(link);
            if (linkStatisticsMap.containsKey(linkType)) {
                int oldCounterValue = linkStatisticsMap.get(linkType);
                int newCounterValue = oldCounterValue + 1;
                linkStatisticsMap.remove(linkType);
                linkStatisticsMap.put(linkType, newCounterValue);
            } else {
                linkStatisticsMap.put(linkType, 1);
            }
        }

        return linkStatisticsMap;
    }

    public static Map<String, Double> summeDurchApp(WinogradDoubleMarkerPassing doubleMarkerPassing, List<String> answersList, List<List<String>> tokenized) {
        Collection<Node> activeNodes = doubleMarkerPassing.getActiveNodes();
        Map<String, Double> answerMap = new HashMap<>(answersList.size());
        Map<String, Double> answerCount = new HashMap<>();

        for (String answer: answersList) {
            double count = 0;
            for (List<String> sent: tokenized) {
                for (String word: sent) {
                    String[] parts = answer.split("\\s+");
                    if (word.toLowerCase().equals(parts[parts.length - 1]) || word.equals(parts[parts.length - 1]))
                        count++;
                }
            }
            answerCount.put(answer, count);
        }

        for (String answer: answersList) {
            double sum = 0.0;
            Definition ansDef = new Definition(answer);
            for (Concept ansCon: ansDef.getDefinition()) {
                if (!Decomposition.getConcepts2Ignore().contains(ansCon)) {
                    for (Node node: activeNodes) {
                        WinogradDoubleNode dnode = (WinogradDoubleNode) node;
                        if (dnode != null && dnode.getConcept().equals(ansCon)) {
                            for (Marker m: dnode.getActivationHistory()) {
                                DoubleMarkerWithOrigin dm = (DoubleMarkerWithOrigin) m;
                                sum = sum + dm.getActivation();
                            }
                        }
                    }
                }
            }
            sum = sum / answerCount.get(answer);
            answerMap.put(answer, sum);
        }

        return answerMap;
    }

    public static Map<String, Double> summeDurchAnzahl(WinogradDoubleMarkerPassing doubleMarkerPassing, List<String> answersList) {
        Collection<Node> activeNodes = doubleMarkerPassing.getActiveNodes();
        Map<String, Double> answerMap = new HashMap<>(answersList.size());

        for (String answer: answersList) {
            double sum = 0.0;
            double number = 0.0;
            Definition ansDef = new Definition(answer);
            for (Concept ansCon: ansDef.getDefinition()) {
                if (!Decomposition.getConcepts2Ignore().contains(ansCon)) {
                    for (Node node: activeNodes) {
                        WinogradDoubleNode dnode = (WinogradDoubleNode) node;
                        if (dnode != null && dnode.getConcept().equals(ansCon)) {
                            for (Marker m: dnode.getActivationHistory()) {
                                DoubleMarkerWithOrigin dm = (DoubleMarkerWithOrigin) m;
                                sum = sum + dm.getActivation();
                                number++;
                            }
                        }
                    }
                }
            }
            if (number == 0) number = 1;
            sum = sum / number;
            answerMap.put(answer, sum);
        }

        return answerMap;
    }

    public static Map<String, Double> summeDurchAnzahldurchParts(WinogradDoubleMarkerPassing doubleMarkerPassing, List<String> answersList) {
        Collection<Node> activeNodes = doubleMarkerPassing.getActiveNodes();
        Map<String, Double> answerMap = new HashMap<>(answersList.size());

        for (String answer: answersList) {
            double sum = 0.0;
            double cons = 0.0;
            double number = 0.0;
            Definition ansDef = new Definition(answer);
            for (Concept ansCon: ansDef.getDefinition()) {
                if (!Decomposition.getConcepts2Ignore().contains(ansCon)) {
                    for (Node node: activeNodes) {
                        WinogradDoubleNode dnode = (WinogradDoubleNode) node;
                        if (dnode != null && dnode.getConcept().equals(ansCon)) {
                            cons++;
                            for (Marker m: dnode.getActivationHistory()) {
                                DoubleMarkerWithOrigin dm = (DoubleMarkerWithOrigin) m;
                                sum = sum + dm.getActivation();
                                number++;
                            }
                        }
                    }
                }
            }
            if (cons == 0) cons = 1;
            if (number == 0) number = 1;
            sum = sum / number / cons;
            answerMap.put(answer, sum);
        }

        return answerMap;
    }

    public static Map<String, Double> summeDurchAnzahlDurchApp(WinogradDoubleMarkerPassing doubleMarkerPassing, List<String> answersList, List<List<String>> tokenized) {
        Collection<Node> activeNodes = doubleMarkerPassing.getActiveNodes();
        Map<String, Double> answerMap = new HashMap<>(answersList.size());
        Map<String, Double> answerCount = new HashMap<>();

        for (String answer: answersList) {
            double count = 0;
            for (List<String> sent: tokenized) {
                for (String word: sent) {
                    String[] parts = answer.split("\\s+");
                    if (word.toLowerCase().equals(parts[parts.length - 1]) || word.equals(parts[parts.length - 1]))
                        count++;
                }
            }
            answerCount.put(answer, count);
        }

        for (String answer: answersList) {
            double sum = 0.0;

            double number = 0.0;
            Definition ansDef = new Definition(answer);
            for (Concept ansCon: ansDef.getDefinition()) {
                if (!Decomposition.getConcepts2Ignore().contains(ansCon)) {
                    for (Node node: activeNodes) {
                        WinogradDoubleNode dnode = (WinogradDoubleNode) node;
                        if (dnode != null && dnode.getConcept().equals(ansCon)) {
                            for (Marker m: dnode.getActivationHistory()) {
                                DoubleMarkerWithOrigin dm = (DoubleMarkerWithOrigin) m;
                                sum = sum + dm.getActivation();
                                number++;
                            }
                        }
                    }
                }
            }
            if (number == 0) number = 1;
            sum = sum / number / answerCount.get(answer);
            answerMap.put(answer, sum);
        }

        return answerMap;
    }

    public static Map<String, Double> summeDurchAnzahlDurchPartsDurchApp(WinogradDoubleMarkerPassing doubleMarkerPassing, List<String> answersList, List<List<String>> tokenized) {
        Collection<Node> activeNodes = doubleMarkerPassing.getActiveNodes();
        Map<String, Double> answerMap = new HashMap<>(answersList.size());
        Map<String, Double> answerCount = new HashMap<>();

        for (String answer: answersList) {
            double count = 0;
            for (List<String> sent: tokenized) {
                for (String word: sent) {
                    String[] parts = answer.split("\\s+");
                    if (word.toLowerCase().equals(parts[parts.length - 1]) || word.equals(parts[parts.length - 1]))
                        count++;
                }
            }
            answerCount.put(answer, count);
        }

        for (String answer: answersList) {
            double sum = 0.0;
            double cons = 0.0;
            double number = 0.0;
            Definition ansDef = new Definition(answer);
            for (Concept ansCon: ansDef.getDefinition()) {
                if (!Decomposition.getConcepts2Ignore().contains(ansCon)) {
                    for (Node node: activeNodes) {
                        WinogradDoubleNode dnode = (WinogradDoubleNode) node;
                        if (dnode != null && dnode.getConcept().equals(ansCon)) {
                            cons++;
                            for (Marker m: dnode.getActivationHistory()) {
                                DoubleMarkerWithOrigin dm = (DoubleMarkerWithOrigin) m;
                                sum = sum + dm.getActivation();
                                number++;
                            }
                        }
                    }
                }
            }

            if (cons == 0) cons = 1;
            if (number == 0) {
                number = 1;
            }
            sum = sum / number / cons / (answerCount.get(answer));
            answerMap.put(answer, sum);
        }

        return answerMap;
    }

    public static int evaluate(Map<String, Double> answerMap, List<String> answerList, String correctAnswer, List<String> toExclude) {
        System.out.println("answer List: " + Arrays.toString(answerList.toArray()));
        System.out.println("correct Answer: " + correctAnswer);

        //remove exclusions from answerlist and -map
        Map<String, Double> answerMapCpy = new HashMap<>(answerMap);
        List<String> answerCpy = new ArrayList<>(answerList);
        answerCpy.removeAll(toExclude);
        toExclude.forEach(k -> answerMapCpy.remove(k));

        int right;
        int pick = 0;

        //TODO: explain: you could simply remove the elements contained in toExclude from answerList (removeAll)
//        for (int i = 0; i < answerList.size(); i++) {
//            if (toExclude.contains(answerList.get(i))) pick++;
//            else break;
//        }

        //TODO: then you count the amount of exluded elements and at the same time treat the count as index for the
        //Element to pick from the answerset. Is this really correct and intended especially because
        // the former loop is left when the very first element simply is not contained in the exclude set?
//        String max = answerList.get(pick);

        //In addition it seems that the answer(s) from the answerset are contained in the map.
        //So, it is sufficient to simply iterate over the map and getting the "maximum" element.

//        for (String answer : answerMap.keySet()) {
//            System.out.println(answer + ": " + answerMap.get(answer));
//            if (!toExclude.contains(answer) && answerMap.get(answer) > answerMap.get(max)) max = answer;
//        }
        Iterator<Map.Entry<String, Double>> iter = answerMapCpy.entrySet().iterator();
        Map.Entry<String, Double> maxEntry = iter.next();
        while (iter.hasNext()) {
            Map.Entry<String, Double> e = iter.next();
            if (e.getValue() > maxEntry.getValue()) maxEntry = e;
        }
        String max = maxEntry.getKey();
        /*List<String> maxList=new ArrayList<>();
        for(String answer:answerMap.keySet()){
            if(answerMap.get(answer)==answerMap.get(max)){
                maxList.add(answer);
            }
        }
        max=maxList.get(maxList.size()-1);*/
        System.out.println("Right answer is: " + correctAnswer);
        System.out.println("max : " + max);
        if (max.equals(correctAnswer)) {
            right = 1;
            System.out.println("KORRREKT");
        } else {
            right = 0;
            System.out.println("FALSCH");
        }
        return right;
    }

    /**
     * Evaluate the result of the marker passing
     *
     * @param schema    schema to evaluate
     * @param toExclude answers to exlude
     * @return zero if the correct answer has been selected else the activation difference between the selected on an the corret one.
     */
    public static double evaluateWinogradSchema(WinogradSchemaData schema, List<String> toExclude) {
        double result = Double.NaN;
        int pick = 0;
        List<String> answerList = schema.getAnswers();
        Map<String, Double> answerMap = schema.getAnswerMap();
        String correctAnswer = schema.getCorrectAnswer();
        double activationOfCorrectAnswer = 0;
        if (!answerMap.containsKey(correctAnswer)) {
            System.out.println("The correct Answer: " + "correctAnswer, " + "has been removed.");
        } else {
            activationOfCorrectAnswer = answerMap.get(correctAnswer);
        }

        for (int i = 0; i < answerList.size(); i++) {
            if (toExclude.contains(answerList.get(i))) pick++;
            else break;
        }
        double activationOnAnswers = 0;
        if (answerList.size() > pick) {
            String max = answerList.get(pick);

            //get answer with maximal activation on markers
            for (String answer: answerMap.keySet()) {
                if (!toExclude.contains(answer) && answerMap.get(answer) > answerMap.get(max)) max = answer;
                if (!answer.equals(correctAnswer)) {
                    activationOnAnswers += answerMap.get(answer);
                }
            }
            double activationOfMaxAnswer = answerMap.get(max);
        }

        //We have a perfact match if all activation is on the correct answer.
        //result = (activationOfCorrectAnswer)/(activationOfCorrectAnswer - activationOnAnswers);

        result = activationOfCorrectAnswer - (activationOnAnswers);


        //We only count if we found the right answer.
     /*   if (max.equals(correctAnswer)) {
            result = 1;
        } else {
            result = 0;
        }*/
        return result;
    }

    public static double getScore(double right, Collection<WinogradSchemaData> result) {
        return ((double) right / (double) result.size()) * 100;
    }

    public static List<Double> getList(List<Map<String, Double>> results, String correct) {
        List<Double> toWrite = new ArrayList<>();
        for (Map<String, Double> map: results) {
            toWrite.add(map.get(correct));
            double max = 0.0;
            String maxName = "";
            for (String answer: map.keySet()) {
                if (map.get(answer) > max) {
                    max = map.get(answer);
                    maxName = answer;
                }
            }
            toWrite.add(max);
            map.remove(maxName);
            max = 0.0;
            for (String answer: map.keySet()) {
                if (map.get(answer) > max) {
                    max = map.get(answer);
                }
            }
            toWrite.add(max);
        }
        return toWrite;
    }

    public static String lemmatize(String documentText, StanfordCoreNLP pipeline) {
        String lemma = "";
        // Create an empty Annotation just with the given textd
        Annotation document = new Annotation(documentText);
        // run all Annotators on this text
        pipeline.annotate(document);
        // Iterate over all of the sentences found
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
        for (CoreMap sentence: sentences) {
            // Iterate over all tokens in a sentence
            for (CoreLabel token: sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                // Retrieve and add the lemma for each word into the
                // list of lemmas
                lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
            }
        }
        return lemma;
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list =
                new LinkedList<>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry: list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    public static void main(String args[]) throws FileNotFoundException, UnsupportedEncodingException, IOException {

        Map<String, Integer> superLinkMap = new HashMap<>();
        int noCollisions = 0;
        int collisions = 0;
        int schemaCounter = 0;

        Map<String, Integer> globalLinkStatisticsMapPronoun = new HashMap<>();
        Map<String, Integer> globalLinkStatisticsMapAnswers = new HashMap<>();

        System.out.println();
        System.out.println("SEMANTIC APPROACH FOR RESOLVING WINOGRAD SCHEMES");
        System.out.println();

        System.out.print("Initializing...");
        //Decomposition decomposition = new Decomposition();
        //decomposition.init();
        init();

        System.out.println("done.");

        double right1 = 0;
        double right2 = 0;
        double right3 = 0;
        double right4 = 0;
        double right5 = 0;
        double right6 = 0;

        List<Integer> answerCounter = new ArrayList<>();

        //Erstelle neues Dataset
        System.out.print("Reading dataset...");
        WinogradSchemaSet dataSet = new WinogradSchemaDataSetReader();
        //Collection<WinogradSchemaData> result = dataSet.readPDPChallangeDataset();
        Collection<WinogradSchemaData> result = dataSet.getWSCDataSet();
        //Collection<WinogradSchemaData> result = dataSet.getNYUDataSet();
        //Collection<WinogradSchemaData> result = dataSet.getRahmanDataSet();
        //Collection<WinogradSchemaData> result = dataSet.getLevesqueDataSet();
        System.out.println("done.");


        Map<String, Integer> linkStatisticsMap = new HashMap<>();
        int noLinksFound = 0;

        List<String> troubleSchema = new LinkedList<>();


        int numberOfSchemas = result.size();


        for (WinogradSchemaData schema: result) {

            //resetting edge counters
            restEdgeCounters();

            schemaCounter++;
            //Annotate the schema
            Annotation annotation = AnalyseUtil.getAnnotation(schema.getText(), AnalyseUtil.getPipeline());
            List<List<String>> tokenized = AnalyseUtil.tokenizeText(annotation);

            //find ponoun concept
            PronConcept pronConcept = findPron(tokenized, schema.getPron(), schema.getQuote(), stanPipeline);

            //get syntactic edges
            List<List<SemanticGraphEdge>> edgeList = getSyntactics(annotation);

            //here begins a stupid approach to allow reflux of markers
            List<List<SemanticGraphEdge>> sentenceListWithReverses = new LinkedList<>();
            for (List<SemanticGraphEdge> sentence: edgeList) {
                List<SemanticGraphEdge> reverseList = new LinkedList<SemanticGraphEdge>();
                for (SemanticGraphEdge edge: sentence) {
                    SemanticGraphEdge newEdge = new SemanticGraphEdge(edge.getTarget(), edge.getSource(), edge.getRelation(), edge.getWeight(), edge.isExtra());
                    reverseList.add(newEdge);
                }
                sentence.addAll(reverseList);
                sentenceListWithReverses.add(sentence);
            }
            edgeList = sentenceListWithReverses;

            //Exclude answers which have no connection
            List<String> excluded = excludedAttribute(edgeList, schema.getAnswers(), pronConcept);

            //Do marker passing
            WinogradDoubleMarkerPassing doubleMarkerPassing = WinogradDoubleMarkerPassing(stanPipeline, srlPipeline, schema, annotation, tokenized, pronConcept, edgeList, new MarkerPassingConfig());


            //Different interpretation of result
            summe(doubleMarkerPassing, schema);
            //Evaluate interpretations
            right1 = right1 + evaluateWinogradSchema(schema, excluded);

            /*Map<String, Double> san = summeDurchAnzahl(doubleMarkerPassing, schema.getAnswers());
            Map<String, Double> sap = summeDurchApp(doubleMarkerPassing, schema.getAnswers(), tokenized);
            Map<String, Double> saa = summeDurchAnzahlDurchApp(doubleMarkerPassing, schema.getAnswers(), tokenized);
            Map<String, Double> sadp = summeDurchAnzahldurchParts(doubleMarkerPassing, schema.getAnswers());
            Map<String, Double> sapa = summeDurchAnzahlDurchPartsDurchApp(doubleMarkerPassing, schema.getAnswers(), tokenized);*/

            //Evaluate interpretations
            /*right2 = right2 + evaluate(san, schema.getAnswers(), schema.getCorrectAnswer(), excluded);
            right3 = right3 + evaluate(sap, schema.getAnswers(), schema.getCorrectAnswer(), excluded);
            right4 = right4 + evaluate(saa, schema.getAnswers(), schema.getCorrectAnswer(), excluded);
            right5 = right5 + evaluate(sadp, schema.getAnswers(), schema.getCorrectAnswer(), excluded);
            right6 = right6 + evaluate(sapa, schema.getAnswers(), schema.getCorrectAnswer(), excluded);*/


            answerCounter.add(schema.getAnswers().size());

            /*if (evaluate(sadp, schema.getAnswers(), schema.getCorrectAnswer(), excluded) == 1 || evaluate(sadp, schema.getAnswers(), schema.getCorrectAnswer(), excluded) != 1) {
                totaltwoLinks = totaltwoLinks + twoLinks;
                totalthreeLinks = totalthreeLinks + threeLinks;
                totalfourLinks = totalfourLinks + fourLinks;
                totalfiveLinks = totalfiveLinks + fiveLinks;
                totalsixLinks = totalsixLinks + sixLinks;

                totalsynCounter = totalsynCounter + synCounter;
                totalantCounter = totalantCounter + antCounter;
                totalhyperCounter = totalhyperCounter + hyperCounter;
                totalhypoCounter = totalhypoCounter + hypoCounter;
                totalnerCounter = totalnerCounter + nerCounter;
                totalroleCounter = totalroleCounter + roleCounter;
                totaldefCounter = totaldefCounter + defCounter;
                totalsyntaxLinkCounter = totalsyntaxLinkCounter + syntaxLinkCounter;

                for (Map.Entry entry : linkMap.entrySet()) {
                    String string = (String) entry.getKey();
                    if (superLinkMap.containsKey(string)) {

                        int oldInt = superLinkMap.get(string);
                        int newInt = oldInt + linkMap.get(string);

                        superLinkMap.put(string, newInt);
                    } else {

                        superLinkMap.put(string, 1);
                    }
                }
            }*/
        }

        //System.out.println("Total number of collisions: "+collisions);
        //System.out.println("WinogradSchemas with no collisions: "+noCollisions +"out of "+schemaCounter+" Schemas.");

        //System.out.println(" synonyms: "+synCounter+"\n antonyms: "+antCounter+"\n hper: "+hyperCounter+"\n hypo: "+hypoCounter);
        //System.out.println(" ner: "+nerCounter+"\n role: "+roleCounter+"\n def: "+defCounter+" syntax: "+syntaxLinkCounter);

        System.out.println("TOTAL LINK COUNT:");
        for (Map.Entry<String, Integer> entry : rightLinksMap.entrySet()){
            System.out.println(entry.getKey()+": "+entry.getValue());

        }

        System.out.println();
        System.out.println("Ergebnis 1: " + right1 + " von " + numberOfSchemas + " waren dieses Mal korrekt.");
        System.out.println("Damit ergibt sich ein Score von: " + getScore(right1, result) + " %");
        System.out.println();

        System.out.println("Ergebnis 2 " + right2 + " von " + numberOfSchemas + " waren dieses Mal korrekt.");
        System.out.println("Damit ergibt sich ein Score von: " + getScore(right2, result) + " %");
        System.out.println();

        System.out.println("Ergebnis 3: " + right3 + " von " + numberOfSchemas + " waren dieses Mal korrekt.");
        System.out.println("Damit ergibt sich ein Score von: " + getScore(right3, result) + " %");
        System.out.println();

        System.out.println("Ergebnis 4: " + right4 + " von " + numberOfSchemas + " waren dieses Mal korrekt.");
        System.out.println("Damit ergibt sich ein Score von: " + getScore(right4, result) + " %");
        System.out.println();

        System.out.println("Ergebnis 5: " + right5 + " von " + numberOfSchemas + " waren dieses Mal korrekt.");
        System.out.println("Damit ergibt sich ein Score von: " + getScore(right5, result) + " %");
        System.out.println();

        System.out.println("Ergebnis 6: " + right6 + " von " + numberOfSchemas + " waren dieses Mal korrekt.");
        System.out.println("Damit ergibt sich ein Score von: " + getScore(right6, result) + " %");
        System.out.println();

        System.out.println();
        double randomScore = 0.0;
        for (int i : answerCounter) {
            randomScore = (randomScore + 1 / ((double) i));
        }
        System.out.println("Der Random-Score war in diesem Fall " + randomScore * 100 / numberOfSchemas + " %");
        System.out.println();


        superLinkMap = sortByValue(superLinkMap);
        for (Map.Entry entry : superLinkMap.entrySet()) {
            //System.out.println(entry.getKey());
            //System.out.println(entry.getValue());
        }

        System.out.println("ENDE");
        //globalLinkStatisticsMapAnswers = sortByValue(globalLinkStatisticsMapAnswers);
        //System.out.println("\n-----------------GLOBAL LINK COUNT FOR Answers: -------------------------------\n");
        //System.out.println("Number of schemas with no Links found: "+noLinksFound);
        /*System.out.println("Concepts that were activated: ");
        for (String answer: activatedConcepts){
            System.out.println(answer);
        }*/
        /*int totalAmountOfLinks = 0;
        for (Map.Entry<String,Integer> entry : globalLinkStatisticsMapAnswers.entrySet()){
            System.out.println("Link: "+entry.getKey()+" Count: "+entry.getValue());
            totalAmountOfLinks = totalAmountOfLinks+entry.getValue();
        }
        System.out.println("total amount of links: "+totalAmountOfLinks);*/

        /*globalLinkStatisticsMapPronoun = sortByValue(globalLinkStatisticsMapPronoun);
        int totalAmountOfLinks = 0;
        System.out.println("\n-----------------GLOBAL LINK COUNT FOR Pronouns: -------------------------------\n");
        for (Map.Entry<String,Integer> entry : globalLinkStatisticsMapPronoun.entrySet()){
            System.out.println("Link: "+entry.getKey()+" Count: "+entry.getValue());
            totalAmountOfLinks = totalAmountOfLinks+entry.getValue();
        }
        System.out.println("total amount of links: "+totalAmountOfLinks);*/
        /*System.out.println("Synonym:"+synCounter);
        System.out.println("Antonym:"+antCounter);
        System.out.println("Hyper:"+hyperCounter);
        System.out.println("Hypo:"+hypoCounter);
        System.out.println("Ner:"+nerCounter);
        System.out.println("Role:"+roleCounter);
        System.out.println("Def:"+defCounter);
        System.out.println("Syntax: "+syntaxLinkCounter);*/
        System.exit(0);
    }

    private static void restEdgeCounters() {
        twoLinks = 0;
        threeLinks = 0;
        fourLinks = 0;
        fiveLinks = 0;
        sixLinks = 0;
        linkMap.clear();

        synCounter = 0;
        antCounter = 0;
        hyperCounter = 0;
        hypoCounter = 0;
        nerCounter = 0;
        roleCounter = 0;
        defCounter = 0;
        syntaxLinkCounter = 0;
    }

    private static WinogradDoubleMarkerPassing WinogradDoubleMarkerPassing(StanfordCoreNLP stanPipeline, CompletePipeline srlPipeline, WinogradSchemaData schema, Annotation annotation, List<List<String>> tokenized, PronConcept pronConcept, List<List<SemanticGraphEdge>> edgeList, MarkerPassingConfig config) {
        //Get named entities
        Map<String, String> nerMap = getNerMap(annotation);

        //get semantic roles
        Map<List<Concept>, List<String>> roleMap = SemanticRoleLabeler.parse(tokenized, pronConcept, srlPipeline);
        Graph decGraph = null;
        Graph syntaxGraph = null;
        Graph nerGraph = null;
        Graph roleGraph = null;
        //We cache the graphs we build by the schema we are look at. So in learning we can resue them.
        if (cache.containsKey(schema)) {
            decGraph = cache.get(schema).get(0);
            syntaxGraph = cache.get(schema).get(1);
            nerGraph = cache.get(schema).get(2);
            roleGraph = cache.get(schema).get(3);
        } else {
            //Decompose graphs
            decGraph = getDecGraph(tokenized, roleMap, nerMap, pronConcept);
            syntaxGraph = getSyntaxGraph(edgeList, pronConcept);
            nerGraph = getNerGraph(nerMap);
            roleGraph = getRoleGraph(roleMap);

            cache.put(schema, Arrays.asList(decGraph, syntaxGraph, nerGraph, roleGraph));
        }
        //configure Start Markers
        List<Map<Concept, List<? extends Marker>>> startActivation = getStartActivation(stanPipeline, schema, pronConcept, nerMap, config);
        //Configure Marker Passing
        WinogradDoubleMarkerPassing doubleMarkerPassing = new WinogradDoubleMarkerPassing(decGraph, WinogradDoubleNode.class, syntaxGraph, nerGraph, roleGraph, config);
        WinogradDoubleMarkerPassing.doInitialMarking(startActivation, doubleMarkerPassing);

        //Execute Marker Passing
        doubleMarkerPassing.execute();
        return doubleMarkerPassing;
    }

    private static List<Map<Concept, List<? extends Marker>>> getStartActivation(StanfordCoreNLP stanPipeline, WinogradSchemaData schema, PronConcept pronConcept, Map<String, String> nerMap, MarkerPassingConfig config) {
        //this part is ony needed if we activate the answers!
        List<Concept> conceptsToActivate = new LinkedList<>();
        for (String answer: schema.getAnswers()) {
            if (answer.contains(" ")) {
                for (String word: answer.split(" ")) {
                    if (!Decomposition.getConcepts2Ignore().contains(word) && !nerMap.keySet().contains(word)) {
                        Concept answerConcept = new Concept(lemmatize(word, stanPipeline));
                        conceptsToActivate.add(answerConcept);
                    } else {
                        String lemma = lemmatize(nerMap.get(word), stanPipeline);
                        Concept answerConcept = new Concept(lemma + "_" + word);
                        conceptsToActivate.add(answerConcept);
                    }
                }
            } else {
                if (!Decomposition.getConcepts2Ignore().contains(answer) && !nerMap.keySet().contains(answer)) {
                    Concept answerConcept = new Concept(lemmatize(answer, stanPipeline));
                    conceptsToActivate.add(answerConcept);
                } else {
                    String lemma = lemmatize(nerMap.get(answer), stanPipeline);
                    Concept answerConcept = new Concept(lemma + "_" + answer);
                    conceptsToActivate.add(answerConcept);
                }
            }
        }


        //List<Map<Concept, List<? extends Marker>>> startActivation = setStartActivation(conceptsToActivate);
        //List<Map<Concept, List<? extends Marker>>> startActivation = setStartActivation(pronConcept,conceptsToActivate);
        //For the collision experiment throw the pronoun in the mix
        //conceptsToActivate.add(pronConcept);
        //List<Map<Concept, List<? extends Marker>>> startActivation = setStartActivation(conceptsToActivate);
        return setStartActivation(pronConcept, config);
    }

    public static void init() {
        if (stanPipeline == null) {
            stanPipeline = AnalyseUtil.getPipeline();
        }
        if (srlPipeline == null) {
            srlPipeline = SemanticRoleLabeler.getPipeline();
        }
        decomposition = new Decomposition();
        decomposition.init();
    }

    public double evaluateWinogradSchema(WinogradSchemaData schema, MarkerPassingConfig config) {
        //StanfordCoreNLP stanPipeline = de.dailab.nsm.decomposition.AnalyseUtil.getPipeline();
        //CompletePipeline srlPipeline = SemanticRoleLabeler.getPipeline();

        //Annotate the schema
        Annotation annotation = AnalyseUtil.getAnnotation(schema.getText(), stanPipeline);
        List<List<String>> tokenized = AnalyseUtil.tokenizeText(annotation);

        //find ponoun concept
        PronConcept pronConcept = findPron(tokenized, schema.getPron(), schema.getQuote(), stanPipeline);

        //get syntactic edges
        List<List<SemanticGraphEdge>> edgeList = getSyntactics(annotation);

        //here begins a stupid approach to allow reflux of markers
        List<List<SemanticGraphEdge>> sentenceListWithReverses = new LinkedList<>();
        for (List<SemanticGraphEdge> sentence: edgeList) {
            List<SemanticGraphEdge> reverseList = new LinkedList<SemanticGraphEdge>();
            for (SemanticGraphEdge edge: sentence) {
                SemanticGraphEdge newEdge = new SemanticGraphEdge(edge.getTarget(), edge.getSource(), edge.getRelation(), edge.getWeight(), edge.isExtra());
                reverseList.add(newEdge);
            }
            sentence.addAll(reverseList);
            sentenceListWithReverses.add(sentence);
        }
        edgeList = sentenceListWithReverses;

        //Exclude answers which have no connection
        List<String> excluded = excludedAttribute(edgeList, schema.getAnswers(), pronConcept);

        //Do marker passing
        WinogradDoubleMarkerPassing doubleMarkerPassing = WinogradDoubleMarkerPassing(stanPipeline, srlPipeline, schema, annotation, tokenized, pronConcept, edgeList, config);

        try {
            summe(doubleMarkerPassing, schema);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //Evaluate interpretations
        return evaluateWinogradSchema(schema, excluded);
    }


}
