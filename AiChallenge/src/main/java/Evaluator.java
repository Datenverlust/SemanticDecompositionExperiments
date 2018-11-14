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
import de.dailab.nsm.decomposition.graph.entities.links.*;
import de.dailab.nsm.decomposition.graph.entities.marker.DoubleMarkerWithOrigin;
import de.dailab.nsm.decomposition.graph.entities.relations.Hyponym;
import de.tuberlin.spreadalgo.Link;
import de.tuberlin.spreadalgo.Marker;
import de.tuberlin.spreadalgo.Node;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import links.NerLink;
import links.RoleLink;
import links.SyntaxLink;
import se.lth.cs.srl.CompletePipeline;
import se.lth.cs.srl.corpus.Sentence;
import se.lth.cs.srl.corpus.Word;

import java.io.FileNotFoundException;
import java.util.*;

public class Evaluator {

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


    /**
     * @param doubleMarkerPassing
     * @param answersList
     * @return
     * @throws FileNotFoundException
     */
    public  Map<String, Double> summe(WinogradDoubleMarkerPassing doubleMarkerPassing, List<String> answersList) throws FileNotFoundException {
        Collection<Node> activeNodes = doubleMarkerPassing.getActiveNodes();
        Map<String, Double> answerMap = new HashMap<>(answersList.size());


        //Set ResultMap to Zero
        for (String answer : answersList) {
            answerMap.put(answer, 0.0);
        }
        for (Node knoten : activeNodes) {
            //eine summe an noch verbliebenen marker auf jedem je aktivierten knoten
            double sum = 0;

            //Für alle knoten die in activeNodes existieren(???)
            if (knoten != null) {
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


                //Für jede antwort die in Antwortmap existiert
                for (String answer : answerMap.keySet()) {

                    Definition def = new Definition(answer);

                    //Für jedes Wort der Antwort
                    for (Concept con : def.getDefinition()) {

                        //Wenn das Wort kein Stopwort ist
                        if (!Decomposition.getConcepts2Ignore().contains(con)) {
                            //Wenn der aktuell betrachtete Knoten mit dem betrachteten Antwort-Wort übereinstimmt, wird der resultMap hinzugefügt
                            if (dnode.getConcept().equals(con)) {
                                Double answerSum = answerMap.get(answer) + sum;
                                answerMap.put(answer, answerSum);
                                for (Marker marker : dnode.getActivationHistory()) {
                                    //System.out.println("--------New Marker-------------------");
                                    //System.out.println("Node: " + con.getLitheral());
                                    //System.out.println("Visited Nodes: ");
                                    DoubleMarkerWithOrigin dmarker = (DoubleMarkerWithOrigin) marker;
                                    for (Concept c : dmarker.getVisitedConcepts()) {
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
                                    for (Link l : dmarker.getVisitedLinks()) {
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

        return answerMap;
    }

    /**
     * @param doubleMarkerPassing
     * @param answersList
     * @param tokenized
     * @return
     */
    public  Map<String, Double> summeDurchApp(WinogradDoubleMarkerPassing doubleMarkerPassing, List<String> answersList, List<List<String>> tokenized) {
        Collection<Node> activeNodes = doubleMarkerPassing.getActiveNodes();
        Map<String, Double> answerMap = new HashMap<>(answersList.size());
        Map<String, Double> answerCount = new HashMap<>();

        for (String answer : answersList) {
            double count = 0;
            for (List<String> sent : tokenized) {
                for (String word : sent) {
                    String[] parts = answer.split("\\s+");
                    if(parts.length>0) {
                        if (word.toLowerCase().equals(parts[parts.length - 1]) || word.equals(parts[parts.length - 1]))
                            count++;
                    }
                }
            }
            if(count==0){
                count =1;
            }
            answerCount.put(answer, count);
        }

        for (String answer : answersList) {
            double sum = 0.0;
            Definition ansDef = new Definition(answer);
            for (Concept ansCon : ansDef.getDefinition()) {
                if (!Decomposition.getConcepts2Ignore().contains(ansCon)) {
                    for (Node node : activeNodes) {
                        WinogradDoubleNode dnode = (WinogradDoubleNode) node;
                        if (dnode != null && dnode.getConcept().equals(ansCon)) {
                            for (Marker m : dnode.getActivationHistory()) {
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

    /**
     * @param doubleMarkerPassing
     * @param answersList
     * @return
     */
    public  Map<String, Double> summeDurchAnzahl(WinogradDoubleMarkerPassing doubleMarkerPassing, List<String> answersList) {
        Collection<Node> activeNodes = doubleMarkerPassing.getActiveNodes();
        Map<String, Double> answerMap = new HashMap<>(answersList.size());

        for (String answer : answersList) {
            double sum = 0.0;
            double number = 0.0;
            Definition ansDef = new Definition(answer);
            for (Concept ansCon : ansDef.getDefinition()) {
                if (!Decomposition.getConcepts2Ignore().contains(ansCon)) {
                    for (Node node : activeNodes) {
                        WinogradDoubleNode dnode = (WinogradDoubleNode) node;
                        if (dnode != null && dnode.getConcept().equals(ansCon)) {
                            for (Marker m : dnode.getActivationHistory()) {
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

    /**
     * @param doubleMarkerPassing
     * @param answersList
     * @return
     */
    public  Map<String, Double> summeDurchAnzahldurchParts(WinogradDoubleMarkerPassing doubleMarkerPassing, List<String> answersList) {
        Collection<Node> activeNodes = doubleMarkerPassing.getActiveNodes();
        Map<String, Double> answerMap = new HashMap<>(answersList.size());

        for (String answer : answersList) {
            double sum = 0.0;
            double cons = 0.0;
            double number = 0.0;
            Definition ansDef = new Definition(answer);
            for (Concept ansCon : ansDef.getDefinition()) {
                if (!Decomposition.getConcepts2Ignore().contains(ansCon)) {
                    for (Node node : activeNodes) {
                        WinogradDoubleNode dnode = (WinogradDoubleNode) node;
                        if (dnode != null && dnode.getConcept().equals(ansCon)) {
                            cons++;
                            for (Marker m : dnode.getActivationHistory()) {
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

    /**
     * @param doubleMarkerPassing
     * @param answersList
     * @param tokenized
     * @return
     */
    public  Map<String, Double> summeDurchAnzahlDurchApp(WinogradDoubleMarkerPassing doubleMarkerPassing, List<String> answersList, List<List<String>> tokenized) {
        Collection<Node> activeNodes = doubleMarkerPassing.getActiveNodes();
        Map<String, Double> answerMap = new HashMap<>(answersList.size());
        Map<String, Double> answerCount = new HashMap<>();

        for (String answer : answersList) {
            double count = 0;
            for (List<String> sent : tokenized) {
                for (String word : sent) {
                    String[] parts = answer.split("\\s+");
                    if (parts.length > 0) {
                        if (word.toLowerCase().equals(parts[parts.length - 1]) || word.equals(parts[parts.length - 1]))
                            count++;
                    }
                }
            }
            if(count==0){
                count =1;
            }
            answerCount.put(answer, count);
        }

        for (String answer : answersList) {
            double sum = 0.0;

            double number = 0.0;
            Definition ansDef = new Definition(answer);
            for (Concept ansCon : ansDef.getDefinition()) {
                if (!Decomposition.getConcepts2Ignore().contains(ansCon)) {
                    for (Node node : activeNodes) {
                        WinogradDoubleNode dnode = (WinogradDoubleNode) node;
                        if (dnode != null && dnode.getConcept().equals(ansCon)) {
                            for (Marker m : dnode.getActivationHistory()) {
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



    /**
     * @param doubleMarkerPassing
     * @param answersList
     * @param tokenized
     * @return
     */
    public  Map<String, Double> summeDurchAnzahlDurchPartsDurchApp(WinogradDoubleMarkerPassing doubleMarkerPassing, List<String> answersList, List<List<String>> tokenized) {
        Collection<Node> activeNodes = doubleMarkerPassing.getActiveNodes();
        Map<String, Double> answerMap = new HashMap<>(answersList.size());
        Map<String, Double> answerCount = new HashMap<>();

        for (String answer : answersList) {
            double count = 0;
            for (List<String> sent : tokenized) {
                for (String word : sent) {
                    String[] parts = answer.split("\\s+");
                    if (word.toLowerCase().equals(parts[parts.length - 1]) || word.equals(parts[parts.length - 1]))
                        count++;
                }
            }
            if(count==0){
                count =1;
            }
            answerCount.put(answer, count);
        }

        for (String answer : answersList) {
            double sum = 0.0;
            double cons = 0.0;
            double number = 0.0;
            Definition ansDef = new Definition(answer);
            for (Concept ansCon : ansDef.getDefinition()) {
                if (!Decomposition.getConcepts2Ignore().contains(ansCon)) {
                    for (Node node : activeNodes) {
                        WinogradDoubleNode dnode = (WinogradDoubleNode) node;
                        if (dnode != null && dnode.getConcept().equals(ansCon)) {
                            cons++;
                            for (Marker m : dnode.getActivationHistory()) {
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

    /**
     * @param doubleMarkerPassing
     * @param answersList
     * @return
     */
    public  Map<String, Double> summeDurchAnzahlNouns(WinogradDoubleMarkerPassing doubleMarkerPassing, List<String> answersList, StanfordCoreNLP stanPipeline, CompletePipeline pipeline) {
        Collection<Node> activeNodes = doubleMarkerPassing.getActiveNodes();
        Map<String, Double> answerMap = new HashMap<>(answersList.size());

        for (String answer : answersList) {
            double sum = 0.0;
            double number = 0.0;
            Definition ansDef = new Definition(getAllNouns(AnalyseUtil.tokenizeText(AnalyseUtil.getAnnotation(answer,stanPipeline)),pipeline));
            for (Concept ansCon : ansDef.getDefinition()) {
                if (!Decomposition.getConcepts2Ignore().contains(ansCon)) {
                    for (Node node : activeNodes) {
                        WinogradDoubleNode dnode = (WinogradDoubleNode) node;
                        if (dnode != null && dnode.getConcept().equals(ansCon)) {
                            for (Marker m : dnode.getActivationHistory()) {
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

    public  Map<String, Double> summeDurchAnzahlNounsAndVerbs(WinogradDoubleMarkerPassing doubleMarkerPassing, List<String> answersList, StanfordCoreNLP stanPipeline, CompletePipeline pipeline) {
        Collection<Node> activeNodes = doubleMarkerPassing.getActiveNodes();
        Map<String, Double> answerMap = new HashMap<>(answersList.size());

        for (String answer : answersList) {
            double sum = 0.0;
            double number = 0.0;
            Definition ansDef = new Definition(getAllNounsANdVerbs(AnalyseUtil.tokenizeText(AnalyseUtil.getAnnotation(answer,stanPipeline)),pipeline));
            for (Concept ansCon : ansDef.getDefinition()) {
                if (!Decomposition.getConcepts2Ignore().contains(ansCon)) {
                    for (Node node : activeNodes) {
                        WinogradDoubleNode dnode = (WinogradDoubleNode) node;
                        if (dnode != null && dnode.getConcept().equals(ansCon)) {
                            for (Marker m : dnode.getActivationHistory()) {
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

    public  Map<String, Double> summeDurchAnzahlNounsVerbsAndAdjective(WinogradDoubleMarkerPassing doubleMarkerPassing, List<String> answersList, StanfordCoreNLP stanPipeline, CompletePipeline pipeline) {
        Collection<Node> activeNodes = doubleMarkerPassing.getActiveNodes();
        Map<String, Double> answerMap = new HashMap<>(answersList.size());

        for (String answer : answersList) {
            double sum = 0.0;
            double number = 0.0;
            Definition ansDef = new Definition(getAllVerbsNounsAndAdjectives(AnalyseUtil.tokenizeText(AnalyseUtil.getAnnotation(answer,stanPipeline)),pipeline));
            for (Concept ansCon : ansDef.getDefinition()) {
                if (!Decomposition.getConcepts2Ignore().contains(ansCon)) {
                    for (Node node : activeNodes) {
                        WinogradDoubleNode dnode = (WinogradDoubleNode) node;
                        if (dnode != null && dnode.getConcept().equals(ansCon)) {
                            for (Marker m : dnode.getActivationHistory()) {
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


    public static String getAllVerbsNounsAndAdjectives(List<List<String>> text, CompletePipeline pipeline) {

        Map<List<Concept>, List<String>> roleMap = new HashMap<>();
        ArrayList<String> WordsToActivate = new ArrayList();
        String ans="";



        //Für jeden im Schema vorkommenden Satz
        for(int i=0;i<text.size();i++) {
            List<String> sentence=text.get(i);
            List<String> newText = new ArrayList<>();
            if(sentence.size()>0) {
                newText.addAll(sentence);
            }
            Sentence s = null;

            try {
                if(newText.size()>1) {
                    s = pipeline.parse(newText);
                }else{
                    WordsToActivate.addAll(newText);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            if(s!=null) {

                for (Word w : s) {
                    if ((w.getPOS().startsWith("N") || w.getPOS().startsWith("V")|| w.getPOS().startsWith("J"))&& !StartActivator.isAlreadyinList(WordsToActivate,w.getForm())){
                        WordsToActivate.add(w.getForm());
                        ans = ans + " "+w.getForm();
                    }
                }
            }

        }
        System.out.println("WordsToActivateAntwort: "+WordsToActivate);
        return ans;
    }
    public static String getAllNouns(List<List<String>> text, CompletePipeline pipeline) {

        Map<List<Concept>, List<String>> roleMap = new HashMap<>();
        ArrayList<String> WordsToActivate = new ArrayList();
        String ans="";


        //Für jeden im Schema vorkommenden Satz
        for(int i=0;i<text.size();i++) {
            List<String> sentence=text.get(i);
            List<String> newText = new ArrayList<>();
            if(sentence.size()>0) {
                newText.addAll(sentence);
            }
            Sentence s = null;

            try {
                if(newText.size()>1) {
                    s = pipeline.parse(newText);
                }else{
                    WordsToActivate.addAll(newText);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            if(s!=null) {

                for (Word w : s) {
                    if (w.getPOS().startsWith("N")&& !StartActivator.isAlreadyinList(WordsToActivate,w.getForm())){
                        WordsToActivate.add(w.getForm());
                        ans = ans + " "+w.getForm();
                    }
                }
            }

        }
        System.out.println("WordsToActivateAntwort: "+WordsToActivate);
        return ans;
    }

    public static String getAllNounsANdVerbs(List<List<String>> text, CompletePipeline pipeline) {

        Map<List<Concept>, List<String>> roleMap = new HashMap<>();
        ArrayList<String> WordsToActivate = new ArrayList();
        String ans="";


        //Für jeden im Schema vorkommenden Satz
        for(int i=0;i<text.size();i++) {
            List<String> sentence=text.get(i);
            List<String> newText = new ArrayList<>();
            if(sentence.size()>0) {
                newText.addAll(sentence);
            }
            Sentence s = null;

            try {
                if(newText.size()>1) {
                    s = pipeline.parse(newText);
                }else{
                    WordsToActivate.addAll(newText);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            if(s!=null) {

                for (Word w : s) {
                    if ((w.getPOS().startsWith("N") || w.getPOS().startsWith("V"))&& !StartActivator.isAlreadyinList(WordsToActivate,w.getForm())){
                        WordsToActivate.add(w.getForm());
                        ans = ans + " "+w.getForm();
                    }
                }
            }

        }
        System.out.println("WordsToActivateAntwort: "+WordsToActivate);
        return ans;
    }

    public  String getLinkType(Link l) {
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

    public  int evaluate(Map<String, Double> answerMap, List<String> answerList, String correctAnswer, List<String> toExclude) {
        System.out.println("answer List: " + Arrays.toString(answerList.toArray()));
        System.out.println("correct Answer: " + correctAnswer);

        //remove exclusions from answerlist and -map
        Map<String, Double> answerMapCpy=  new HashMap<>(answerMap);
        List<String> answerCpy = new ArrayList<>(answerList);
        answerCpy.removeAll(toExclude);
        toExclude.forEach( k -> answerMapCpy.remove(k));

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
        while(iter.hasNext()){
            Map.Entry<String, Double> e  = iter.next();
            if(e.getValue() > maxEntry.getValue()) maxEntry = e;
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

    public static double getScore(int right, Collection<Question> result) {
        return ((double) right / (double) result.size()) * 100;
    }
    public static double getHighestAnswer(Map<String, Double> map){
        double i =0;
        for(String s: map.keySet()){
            if(map.get(s)>i){
                i = map.get(s);

            }
        }
        return i;
    }

    public static double getSecondHighestAnswer(Map<String, Double> map) {
        double i =0;
        String best = getAnswerQuestion(map);
        String a="";
        for(String s: map.keySet()){
            if(map.get(s)>i&&!best.equals(s)){
                i = map.get(s);

            }
        }
        return i;
    }

    public static double getRightAnswer(Map<String, Double> map, String right) {
        double i =0;
        for(String s: map.keySet()){
            if(right.equals(s)){
                i = map.get(s);
            }
        }
        return i;
    }

    public static String getAnswerQuestion(Map<String, Double> map) {
        double i =0;
        String a="";
        for(String s: map.keySet()){
            if(map.get(s)>i){
                i = map.get(s);
                a = s;
            }
        }
        return a;
    }
    public static String getSecondAnswerQuestion(Map<String, Double> map) {
        double i =0;
        String best = getAnswerQuestion(map);
        String a="";
        for(String s: map.keySet()){
            if(map.get(s)>i&&!best.equals(s)){
                i = map.get(s);
                a = s;
            }
        }
        return a;
    }
}
