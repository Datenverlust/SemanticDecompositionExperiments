/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

import de.kimanufaktur.nsm.decomposition.AnalyseUtil;
import de.kimanufaktur.nsm.decomposition.Concept;
import de.kimanufaktur.nsm.decomposition.Decomposition;
import de.kimanufaktur.nsm.decomposition.Definition;
import de.kimanufaktur.nsm.decomposition.graph.conceptCache.GraphUtil;
import de.kimanufaktur.nsm.decomposition.graph.entities.links.*;
import de.kimanufaktur.nsm.decomposition.graph.entities.marker.DoubleMarkerWithOrigin;
import de.kimanufaktur.nsm.decomposition.graph.entities.relations.Hyponym;
import de.kimanufaktur.nsm.decomposition.graph.spreadingActivation.MarkerPassing.MarkerPassingConfig;
import de.tuberlin.spreadalgo.Link;
import de.tuberlin.spreadalgo.Marker;
import de.tuberlin.spreadalgo.Node;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.util.CoreMap;
import links.NerLink;
import links.RoleLink;
import links.SyntaxLink;
import org.jgrapht.Graph;
import se.lth.cs.srl.CompletePipeline;

import java.io.*;
import java.text.DecimalFormat;
import java.util.*;

public class QuestionTest {

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

    public static PronConcept findPron(List<List<String>> tokenized, String pron, String quoteText, StanfordCoreNLP pipeline) {
        PronConcept pronConcept = null;
        if (quoteText.equals("")) {
            int wordNR = -1;
            for (List<String> list : tokenized) {
                int i = 0;
                String[] prons = pron.split(" ");
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
                                return new PronConcept(pron, wordNR, 0);
                            }
                            break;
                        }
                        i++;
                    }
                } else {
                    for (String word : list) {
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
        for (Node knoten : activeNodes) {
            //eine summe an noch verbliebenen marker auf jedem je aktivierten knoten
            double sum = 0;

            //Für alle knoten die in activeNodes existieren(???)
            if (knoten != null) {
                activeNodeCounter++;
                WinogradDoubleNode dnode = (WinogradDoubleNode) knoten;
                //System.out.println(dnode.getConcept().getLitheral());
                //Für Marker jeder Herkunft die darauf liegen
                int markerCounter = 0;
                for (Marker marker : dnode.getActivationHistory()) {
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
                for (Marker marker : dnode.getActivationHistory()) {
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

        for (Node knoten : activeNodes) {
            //eine summe an noch verbliebenen marker auf jedem je aktivierten knoten
            double sum = 0;

            //Für alle knoten die in activeNodes existieren(???)
            if (knoten != null) {
                WinogradDoubleNode dnode = (WinogradDoubleNode) knoten;
                //Für Marker jeder Herkunft die darauf liegen
                int markerCounter = 0;
                for (Marker marker : dnode.getActivationHistory()) {
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
                for (Concept con : def.getDefinition()) {
                    //Wenn der aktuell betrachtete Knoten mit dem Pronomen übereinstimmt
                    if (dnode.getConcept().equals(con)) {

                        for (Marker marker : dnode.getActivationHistory()) {
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
                            for (Concept c : dmarker.getVisitedConcepts()) {
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

    public static Map<String, Integer> whereDoTheAnswerMarkersGo(WinogradDoubleMarkerPassing doubleMarkerPassing, QuestionConcept pronConcept, List<String> answers, String rightAnswer, Map<String, Integer> linkStatisticsMap, Map<String, String> nerMap) throws FileNotFoundException, UnsupportedEncodingException, IOException {
        Collection<Node> activeNodes = doubleMarkerPassing.getActiveNodes();
        int totalNodeCount = 0;
        Map<String, Integer> perSchemaLinkMap = new HashMap<>();
        StanfordCoreNLP stanPipeline = AnalyseUtil.getPipeline();
        List<String> lemmatizedAnswers = new LinkedList<>();

        for (Node knoten : activeNodes) {
            //eine summe an noch verbliebenen marker auf jedem je aktivierten knoten
            double sum = 0;

            //Für alle knoten die in activeNodes existieren(???)
            if (knoten != null) {
                totalNodeCount++;
                WinogradDoubleNode dnode = (WinogradDoubleNode) knoten;
                //System.out.println(dnode.getConcept().getLitheral());
                //Für Marker jeder Herkunft die darauf liegen
                int markerCounter = 0;
                for (Marker marker : dnode.getActivationHistory()) {
                    DoubleMarkerWithOrigin dmarker = (DoubleMarkerWithOrigin) marker;
                    // /Wenn marker darauf > 0 , dann füge befindliche Aktivierung der summe hinzu
                    if (dmarker.getActivation() > 0) {
                        sum = sum + dmarker.getActivation();
                    }
                }

                List<Link> linksVisitedByAnswerMarker = new LinkedList<>();
                List<Concept> conceptsVisitedByAnswerMarker = new LinkedList<>();

                List<String> tokenizedAnswers = new LinkedList<>();
                for (String answer : answers) {
                    if (answer.contains(" ")) {
                        String[] answerArray = answer.split(" ");
                        tokenizedAnswers.addAll(Arrays.asList(answerArray));
                    } else {
                        tokenizedAnswers.add(answer);
                    }
                }


                for (String answer : tokenizedAnswers) {
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
                for (Marker marker : dnode.getActivationHistory()) {
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
                    for (Map.Entry<String, Integer> entry : recordLinkStatistics(linksVisitedByAnswerMarker).entrySet()) {
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
                    for (Map.Entry<String, Integer> entry : recordLinkStatistics(linksVisitedByAnswerMarker).entrySet()) {
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
        for (Map.Entry<String, Integer> entry : perSchemaLinkMap.entrySet()) {
            System.out.append("\nLink: " + entry.getKey() + " Count: " + entry.getValue());
        }

        /*for (String s : lemmatizedAnswers){
            System.out.println("Lemmatized Answers: "+s);
        }*/
        return linkStatisticsMap;
    }

    public static Map<String, Integer> whereDoThePronounMarkersGo(WinogradDoubleMarkerPassing doubleMarkerPassing, QuestionConcept pronConcept, List<String> answers, String rightAnswer, Map<String, Integer> linkStatisticsMap) throws FileNotFoundException, UnsupportedEncodingException, IOException {
        Map<String, Integer> perSchemaLinkMap = new HashMap<>();
        float totalMarkerCount = 0;
        float jumpsMarkerCount = 0;

        Collection<Node> activeNodes = doubleMarkerPassing.getActiveNodes();
        int totalNodeCount = 0;

        for (Node knoten : activeNodes) {
            //eine summe an noch verbliebenen marker auf jedem je aktivierten knoten
            double sum = 0;

            //Für alle knoten die in activeNodes existieren(???)
            if (knoten != null) {
                totalNodeCount++;
                WinogradDoubleNode dnode = (WinogradDoubleNode) knoten;
                //System.out.println(dnode.getConcept().getLitheral());
                //Für Marker jeder Herkunft die darauf liegen
                int markerCounter = 0;
                for (Marker marker : dnode.getActivationHistory()) {
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
                for (Marker marker : dnode.getActivationHistory()) {
                    totalMarkerCount++;
                    DoubleMarkerWithOrigin dmarker = (DoubleMarkerWithOrigin) marker;
                    if (dmarker.getOrigin().getLitheral().equals(pronConcept.getLitheral())) {
                        linksVisitedByPronMarker = dmarker.getVisitedLinks();
                        conceptsVisitedByPronMarker = dmarker.getVisitedConcepts();
                    }
                }

                for (Concept c : conceptsVisitedByPronMarker) {
                    if (conceptsVisitedByPronMarker.contains(dnode.getConcept())) {
                        jumpsMarkerCount++;
                    }
                }

                if (!linksVisitedByPronMarker.isEmpty()) {
                    //linkStatisticsMap.putAll(recordLinkStatistics(linksVisitedByPronMarker));
                    for (Map.Entry<String, Integer> entry : recordLinkStatistics(linksVisitedByPronMarker).entrySet()) {
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
                    for (Map.Entry<String, Integer> entry : recordLinkStatistics(linksVisitedByPronMarker).entrySet()) {
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

        for (Link link : linkList) {
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

    public static void clean(){
//        GraphUtil.setDecomposition(new Decomposition());
//        GraphUtil.setGraphCache(Collections.synchronizedMap(new HashMap<String, Graph>()));
//        GraphUtil.setLockcount(0);
//        GraphUtil.setLockMap(Collections.synchronizedMap(new Hashtable<String, Object>()));
//        GraphUtil.setMergedGraphCache(new HashMap<>(65));
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




    public static String lemmatize(String documentText, StanfordCoreNLP pipeline) {
        String lemma = "";
        // Create an empty Annotation just with the given textd
        Annotation document = new Annotation(documentText);
        // run all Annotators on this text
        pipeline.annotate(document);
        // Iterate over all of the sentences found
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
        for (CoreMap sentence : sentences) {
            // Iterate over all tokens in a sentence
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
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
        for (Map.Entry<K, V> entry : list) {
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
        System.out.println("QuestionAnswerTest");
        System.out.println();

        System.out.print("Initializing...");
        //Decomposition decomposition = new Decomposition();
        //decomposition.init();

        System.out.println("done.");

        Map<QuestionClass,List<Question>> questionMap = DataLoader.getQuestionMap();


        int counter = 0;

        List<Evaluation> evaluations = new ArrayList<>();
        evaluations.add(Evaluation.SUMME);
        evaluations.add(Evaluation.SUMMEDURCHWORTANZAHL);
        evaluations.add(Evaluation.SUMMENOUNS);
        evaluations.add(Evaluation.SUMMENOUNSANDVERBS);
        evaluations.add(Evaluation.SUMMENOUNSBERBSANDAFJECTIVES);


        List<StartActivation> startActivationWay = new ArrayList<>();
        startActivationWay.add(StartActivation.HEADWORD);
        startActivationWay.add(StartActivation.VERBSANDNOUNS);
        startActivationWay.add(StartActivation.VERBSNOUNSANDADJECTIVES);


        Map<StartActivation, Map<Evaluation, Integer>> activationMap = new HashMap<>();
        Map<Evaluation, Integer> evaluationMap = new HashMap<>();


        for (StartActivation a : startActivationWay) {
            evaluationMap = new HashMap<>();
            for (Evaluation e : evaluations) {
                evaluationMap.put(e, 0);
            }
            activationMap.put(a, evaluationMap);
        }


        int right1 = 0;
        int right2 = 0;
        int right3 = 0;
        int right4 = 0;
        int right5 = 0;
        int right6 = 0;
        int headVerbRight1 = 0;
        int headVerbRight2 = 0;
        int headVerbRight3 = 0;
        int headVerbRight4 = 0;
        int headVerbRight5 = 0;
        int headVerbRight6 = 0;
        int verbNounAdjective1 = 0;
        int verbNounAdjective2 = 0;
        int verbNounAdjective3 = 0;
        int verbNounAdjective4 = 0;
        int verbNounAdjective5 = 0;
        int verbNounAdjective6 = 0;
        int test1 = 0;
        int test2 = 0;
        int test3 = 0;
        int test4 = 0;
        int test5 = 0;
        int test6 = 0;
        int ergebnis =0;

        List<Integer> answerCounter = new ArrayList<>();

        //Erstelle neues Dataset
        System.out.print("Reading dataset...");
        WinogradSchemaSet dataSet = new WinogradSchemaDataSetReader();
        //Collection<WinogradSchemaData> result = dataSet.readPDPChallangeDataset();
        //Collection<WinogradSchemaData> result = dataSet.getWSCDataSet();
        //Collection<WinogradSchemaData> result = dataSet.getNYUDataSet();
        Collection<WinogradSchemaData> result = dataSet.getRahmanDataSet();
        //Collection<WinogradSchemaData> result = dataSet.getLevesqueDataSet();
        System.out.println("done.");



        Question q = new Question("What do earthquakes tell scientists about the history of the planet?","Earth’s climate is constantly changing","The continents of Earth are continually moving","Dinosaurs became extinct about 65 million years ago","The oceans are much deeper today than millions years ago","The continents of Earth are continually moving");
        ArrayList<Question> questions1 = new ArrayList<>();
        questions1.add(q);
        QuestionClassifier.classifyData(questions1);


        //CONFIG MARKERPASSING
        MarkerPassingConfig.setDecompositionDepth(2);
        MarkerPassingConfig.setStartActivation(1000);

        //MarkerPassingConfig.setTerminationPulsCount(4);

        Map<String, Integer> linkStatisticsMap = new HashMap<>();
        int noLinksFound = 0;

        List<String> troubleSchema = new LinkedList<>();
        //Decomposition decomposition = new Decomposition();
        //decomposition.init();

        long start = System.currentTimeMillis();
        DecimalFormat df2 = new DecimalFormat("###,##");

        int numberOfSchemas = questions1.size();


        for (Question schema : questions1) {
            clean();
            MarkerPassingConfig.setDecompositionDepth(2);
            Decomposition decomposition = new Decomposition();
            decomposition.init();
            StanfordCoreNLP stanPipeline = AnalyseUtil.getPipeline();
            CompletePipeline srlPipeline = SemanticRoleLabeler.getPipeline();

            long before = System.currentTimeMillis();
            //System.out.println(schema.getAnswers()+" "+schema.getCorrectAnswer());
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




            schemaCounter++;
            System.out.println();

            System.out.println("Now working on..." + schemaCounter + "/" + numberOfSchemas + " " + Double.valueOf(df2.format(((100.0d / numberOfSchemas) * schemaCounter))) + "%");
            System.out.println(schema.getQuestionContent());
            System.out.println();
            System.out.print("Annotating schema...");
            Annotation annotation = AnalyseUtil.getAnnotation(schema.getQuestionContent(), stanPipeline);
            System.out.println("done.");

            List<List<String>> tokenized = AnalyseUtil.tokenizeText(annotation);

            System.out.print("Finding pron...");
            System.out.println(schema.getQuestionContent());
            String questionVerb = DataLoader.getVerbs(tokenized, srlPipeline);
            try {
                QuestionConcept questionConcept = findConcept(tokenized, questionVerb, schema.getQuestionContent(), stanPipeline);


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
                answers.add(schema.anwserA);
                answers.add(schema.anwserB);
                answers.add(schema.anwserC);
                answers.add(schema.anwserD);

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
                //System.out.println("1  " + GraphUtil.getGraphCache().size());
                Graph syntaxGraph = GraphBuilder.getSyntaxGraph(edgeList, questionConcept);
                System.out.println("2");
                //System.out.println("afgh: "+syntaxGraph.toString());
                //GraphUtil.saveToGraphML(syntaxGraph, "D:\\syntaxHaven.GraphML");
                Graph nerGraph = GraphBuilder.getNerGraph(nerMap);
                System.out.println("3");
                //System.out.println("afgh: "+nerGraph.toString());
                Graph roleGraph = GraphBuilder.getRoleGraph(roleMap);
                //GraphUtil.saveToGraphML(roleGraph, "D:\\roleDad.GraphML");
                //System.out.println("afgh: "+roleGraph.toString());
                System.out.println("done.");
                List<String> excluded = GraphBuilder.excludedAttribute(edgeList, answers, questionConcept);



                System.out.println("test2: " + tokenized.toString());

                //Different startactivations

                System.out.print("Setting startmarkers...");



                    try {
                        Evaluator eva = new Evaluator();
                        List<Map<Concept, List<? extends Marker>>> startActivation = null;
                        if (schema.type.equals(QuestionClass.SYNTHESIS)||schema.type.equals(QuestionClass.KNOWLEDGE)||schema.type.equals(QuestionClass.EVALUATION)||schema.type.equals(QuestionClass.COMPREHENSION)||schema.type.equals(QuestionClass.ANALYSIS)||schema.type.equals(QuestionClass.APPLICATION)) {
                            startActivation = StartActivator.setStartActivation(StartActivator.getHeadVerbAndNouns(tokenized, srlPipeline, stanPipeline, nerMap, answers));
                        } else if (schema.type.equals(QuestionClass.EVALUATION)) {
                            startActivation = StartActivator.setStartActivation(StartActivator.getAllVerbsAndNouns(tokenized, srlPipeline, stanPipeline, nerMap, answers));
                        }


                        System.out.println("done.");

                        MarkerPassingConfig mk = new MarkerPassingConfig();//new MarkerPassingConfig(DataLoader.getBestConfig(schema.type.name()));//Constructor does not exist and mk was never used in the old version before merge



                        System.out.print("Initializing markerpassing...");
                        WinogradDoubleMarkerPassing doubleMarkerPassing = new WinogradDoubleMarkerPassing(decGraph, WinogradDoubleNode.class, syntaxGraph, nerGraph, roleGraph, mk);
                        System.out.println(doubleMarkerPassing.getNodes().size());
                        System.out.println(doubleMarkerPassing.doubleActivation.size());
                        WinogradDoubleMarkerPassing.doInitialMarking(startActivation, doubleMarkerPassing);
                        System.out.print("done.");

                        System.out.print("Executing markerpassing...");
                        doubleMarkerPassing.execute();
                        System.out.println("done.");
                        System.out.println();



                            if (schema.type.equals(QuestionClass.SYNTHESIS)||schema.type.equals(QuestionClass.KNOWLEDGE)||schema.type.equals(QuestionClass.EVALUATION)||schema.type.equals(QuestionClass.APPLICATION)) {

                                ergebnis = ergebnis + eva.evaluate(eva.summe(doubleMarkerPassing, answers), answers, schema.rightAwnser, excluded);


                            }

                            if (schema.type.equals(QuestionClass.COMPREHENSION)||schema.type.equals(QuestionClass.ANALYSIS)) {

                                ergebnis = ergebnis + eva.evaluate(eva.summeDurchAnzahlNouns(doubleMarkerPassing, answers, stanPipeline, srlPipeline), answers, schema.rightAwnser, excluded);

                            }


                    } catch (Exception ex) {
                        ex.printStackTrace();

                    }



                counter++;
            } catch (Exception e) {
                e.printStackTrace();
            }



            /*
            for(List<Concept> conceptsToActivate:startActivations){

                Evaluator eva = new Evaluator();




            //this part is ony needed if we activate the answers!


//            Map<Integer,Integer> duplicateMap = new HashMap<>();
//            for (int i = 0; i < conceptsToActivate.size(); i++){
//                for (int k = i+1; k < conceptsToActivate.size(); k++){
//                    if(conceptsToActivate.get(i).getLitheral().equals((conceptsToActivate.get(k).getLitheral()))){
//                        int randomNum = ThreadLocalRandom.current().nextInt(1, 100);
//                        duplicateMap.put(k,randomNum);
//                    }
//                }
//            }

            /*if (!duplicateMap.isEmpty()){
                for (Map.Entry<Integer, Integer> entry : duplicateMap.entrySet()) {
                    Concept oldConcept = conceptsToActivate.get(entry.getKey());
                    Concept individualConcept = new Concept(oldConcept.getLitheral()+entry.getValue());
                    conceptsToActivate.add(entry.getKey(), individualConcept);
                }
            }*/


            //List<Map<Concept, List<? extends Marker>>> startActivation = setStartActivation(pronConcept);
            //List<Map<Concept, List<? extends Marker>>> startActivation = setStartActivation(conceptsToActivate);
            //List<Map<Concept, List<? extends Marker>>> startActivation = setStartActivation(pronConcept,conceptsToActivate);
            //For the collision experiment throw the pronoun in the mix
            //conceptsToActivate.add(pronConcept);
            /*
            List<Map<Concept, List<? extends Marker>>> startActivation = StartActivator.setStartActivation(conceptsToActivate);
            System.out.println("done.");

            System.out.print("Initializing markerpassing...");
            WinogradDoubleMarkerPassing doubleMarkerPassing = new WinogradDoubleMarkerPassing(decGraph, WinogradDoubleNode.class, syntaxGraph, nerGraph, roleGraph);
            System.out.println(doubleMarkerPassing.getNodes().size());
            System.out.println(doubleMarkerPassing.doubleActivation.size());
            WinogradDoubleMarkerPassing.doInitialMarking(startActivation, doubleMarkerPassing);
            System.out.print("done.");

            System.out.print("Executing markerpassing...");
            doubleMarkerPassing.execute();
            System.out.println("done.");
            System.out.println();
                //Map<String, Integer> w = whereDoTheAnswerMarkersGo(doubleMarkerPassing,questionConcept,answers,schema.rightAwnser,linkStatisticsMap,nerMap);
                //Map<String, Integer> w2 = whereDoThePronounMarkersGo(doubleMarkerPassing,questionConcept,answers,schema.rightAwnser,linkStatisticsMap);

            //findCollisions(doubleMarkerPassing,pronConcept,answers,schema.rightAwnser);


            System.out.println("Results");
            /*Map<String,Integer> currentSchemaLinkStatsPronoun = whereDoThePronounMarkersGo(doubleMarkerPassing,pronConcept, schema.getAnswers(), schema.getCorrectAnswer(), linkStatisticsMap);
            for (Map.Entry<String, Integer> entry : currentSchemaLinkStatsPronoun.entrySet()){
                if(globalLinkStatisticsMapPronoun.containsKey(entry.getKey())){
                    int oldValue = linkStatisticsMap.get(entry.getKey());
                    int newValue = oldValue+entry.getValue();
                    globalLinkStatisticsMapPronoun.put(entry.getKey(),newValue);
                }else{
                    globalLinkStatisticsMapPronoun.put(entry.getKey(),entry.getValue());
                }
            }

            Map<String,Integer> currentSchemaLinkStatsAnswers = whereDoTheAnswerMarkersGo(doubleMarkerPassing,pronConcept, schema.getAnswers(), schema.getCorrectAnswer(), linkStatisticsMap, nerMap);

            for (Map.Entry<String, Integer> entry : currentSchemaLinkStatsAnswers.entrySet()){
                if(globalLinkStatisticsMapAnswers.containsKey(entry.getKey())){
                    int oldValue = linkStatisticsMap.get(entry.getKey());
                    int newValue = oldValue+entry.getValue();
                    globalLinkStatisticsMapAnswers.put(entry.getKey(),newValue);
                }else{
                    globalLinkStatisticsMapAnswers.put(entry.getKey(),entry.getValue());
                }
            }*/

            /*grabOnlyPron(doubleMarkerPassing,pronConcept, schema.getAnswers(), schema.getCorrectAnswer());
            int currentCollisions = findCollisions(doubleMarkerPassing,pronConcept, schema.getAnswers(), schema.getCorrectAnswer());
            if (currentCollisions != 0){
                collisions += currentCollisions;
            }else{
                noCollisions++;
            }


            Map<String, Double> s = eva.summe(doubleMarkerPassing, answers);
            Map<String, Double> san = eva.summeDurchAnzahl(doubleMarkerPassing, answers);
            Map<String, Double> sap = eva.summeDurchApp(doubleMarkerPassing, answers, tokenized);
            Map<String, Double> saa = eva.summeDurchAnzahlDurchApp(doubleMarkerPassing, answers, tokenized);
            Map<String, Double> sadp = eva.summeDurchAnzahldurchParts(doubleMarkerPassing, answers);
            Map<String, Double> sapa = eva.summeDurchAnzahlDurchPartsDurchApp(doubleMarkerPassing, answers, tokenized);

            if(counter ==0) {

                right1 = right1 + eva.evaluate(s, answers, schema.rightAwnser, excluded);
                right2 = right2 + eva.evaluate(san, answers, schema.rightAwnser, excluded);
                right3 = right3 + eva.evaluate(sap, answers, schema.rightAwnser, excluded);
                right4 = right4 + eva.evaluate(saa, answers, schema.rightAwnser, excluded);
                right5 = right5 + eva.evaluate(sadp, answers, schema.rightAwnser, excluded);
                right6 = right6 + eva.evaluate(sapa, answers, schema.rightAwnser, excluded);
            }

            if(counter == 1){
                System.out.println("HeadWords");
                headVerbRight1 = headVerbRight1 + eva.evaluate(s, answers, schema.rightAwnser, excluded);
                headVerbRight2 = headVerbRight2 + eva.evaluate(san, answers, schema.rightAwnser, excluded);
                headVerbRight3 = headVerbRight3 + eva.evaluate(sap, answers, schema.rightAwnser, excluded);
                headVerbRight4 = headVerbRight4 + eva.evaluate(saa, answers, schema.rightAwnser, excluded);
                headVerbRight5 = headVerbRight5 + eva.evaluate(sadp, answers, schema.rightAwnser, excluded);
                headVerbRight6 = headVerbRight6 + eva.evaluate(sapa, answers, schema.rightAwnser, excluded);
            }

            if(counter == 2){
                    System.out.println("VerbNounAdjective");
                verbNounAdjective1 = verbNounAdjective1 + eva.evaluate(s, answers, schema.rightAwnser, excluded);
                verbNounAdjective2 = verbNounAdjective2 + eva.evaluate(san, answers, schema.rightAwnser, excluded);
                verbNounAdjective3 = verbNounAdjective3 + eva.evaluate(sap, answers, schema.rightAwnser, excluded);
                verbNounAdjective4 = verbNounAdjective4 + eva.evaluate(saa, answers, schema.rightAwnser, excluded);
                verbNounAdjective5 = verbNounAdjective5 + eva.evaluate(sadp, answers, schema.rightAwnser, excluded);
                verbNounAdjective6 = verbNounAdjective6 + eva.evaluate(sapa, answers, schema.rightAwnser, excluded);
                }

                if(counter == 3){
                    System.out.println("Test");
                    test1 = test1 + eva.evaluate(s, answers, schema.rightAwnser, excluded);
                    test2 = test2 + eva.evaluate(san, answers, schema.rightAwnser, excluded);
                    test3 = test3 + eva.evaluate(sap, answers, schema.rightAwnser, excluded);
                    test4 = test4 + eva.evaluate(saa, answers, schema.rightAwnser, excluded);
                    test5 = test5 + eva.evaluate(sadp, answers, schema.rightAwnser, excluded);
                    test6 = test6 + eva.evaluate(sapa, answers, schema.rightAwnser, excluded);
                }


            answerCounter.add(answers.size());
            counter++;



            cleanUpMarker(doubleMarkerPassing);



            }

            long after = System.currentTimeMillis();
            long totaltime = (after - start) / 1000;
            long lastSchema = (after - before) / 1000;
            System.out.println("Time elapsed:" + totaltime + " second with an avg of " + totaltime / schemaCounter + "  seconds and " + totaltime / schemaCounter * (result.size() - schemaCounter) + " seconds to go.");
            */
        }

        //System.out.println("Total number of collisions: "+collisions);
        //System.out.println("WinogradSchemas with no collisions: "+noCollisions +"out of "+schemaCounter+" Schemas.");

        //System.out.println(" synonyms: "+synCounter+"\n antonyms: "+antCounter+"\n hper: "+hyperCounter+"\n hypo: "+hypoCounter);
        //System.out.println(" ner: "+nerCounter+"\n role: "+roleCounter+"\n def: "+defCounter+" syntax: "+syntaxLinkCounter);

        /*System.out.println("TOTAL LINK COUNT:");
        for (Map.Entry<String, Integer> entry : rightLinksMap.entrySet()){
            System.out.println(entry.getKey()+": "+entry.getValue());

        }*/


        double randomScore = 0.0;
        for (int i : answerCounter) {
            randomScore = (randomScore + 1 / ((double) i));
        }
        System.out.println("Der Random-Score war in diesem Fall " + randomScore * 100 / numberOfSchemas + " %");
        System.out.println();

        FileWriter fw = null;

        try {







                    System.out.println("StartActivation: " + questions1.size() + " Damit ergibt sich ein Score von: " + Evaluator.getScore(ergebnis, questions1));
                    fw = new FileWriter("ErgebnisKombination" + ".txt", true);
                    fw.write("Anazhl: "+ questions1.size() + " Damit ergibt sich ein Score von: " + Evaluator.getScore(ergebnis, questions1));
                    BufferedWriter bw = new BufferedWriter(fw);
                    bw.newLine();
                    bw.flush();
                    bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


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


}


