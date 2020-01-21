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
import de.dailab.nsm.decomposition.graph.entities.marker.DoubleMarkerWithOrigin;
import de.dailab.nsm.decomposition.graph.spreadingActivation.MarkerPassing.MarkerPassingConfig;
import de.tuberlin.spreadalgo.Marker;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import se.lth.cs.srl.CompletePipeline;
import se.lth.cs.srl.corpus.Sentence;
import se.lth.cs.srl.corpus.Word;

import java.util.*;



public class StartActivator {




    public static List<Concept> getAllVerbsAndNouns(List<List<String>> text, CompletePipeline pipeline, StanfordCoreNLP stanPipeline, Map<String, String> nerMap, List<String> anwsers ) {

        Map<List<Concept>, List<String>> roleMap = new HashMap<>();
        ArrayList<String> WordsToActivate = new ArrayList();
        text.addAll(getTokenizedQuestionAnwsers(stanPipeline,anwsers));


        //Für jeden im Schema vorkommenden Satz
        for(int i=0;i<text.size();i++) {
            List<String> sentence = text.get(i);
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
                    if ((w.getPOS().startsWith("N") || w.getPOS().startsWith("V"))&& !isAlreadyinList(WordsToActivate,w.getForm())) {
                        WordsToActivate.add(w.getForm());
                    }
                }
            }

        }
        System.out.println("WordsToActivate VerbsNouns: "+WordsToActivate);
        return getConcepts(WordsToActivate,stanPipeline,nerMap);
    }

    public static List<Concept> getAllVerbsNounsAndAdjectives(List<List<String>> text, CompletePipeline pipeline, StanfordCoreNLP stanPipeline, Map<String, String> nerMap, List<String> anwsers ) {

        Map<List<Concept>, List<String>> roleMap = new HashMap<>();
        ArrayList<String> WordsToActivate = new ArrayList();
        text.addAll(getTokenizedQuestionAnwsers(stanPipeline,anwsers));


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
                    if ((w.getPOS().startsWith("N") || w.getPOS().startsWith("V")|| w.getPOS().startsWith("J"))&& !isAlreadyinList(WordsToActivate,w.getForm())){
                        WordsToActivate.add(w.getForm());
                    }
                }
            }

        }
        System.out.println("WordsToActivate VerbNounsAD: "+WordsToActivate);
        return getConcepts(WordsToActivate,stanPipeline,nerMap);
    }

    public static List<Concept> getHeadVerbAndNouns(List<List<String>> text, CompletePipeline pipeline, StanfordCoreNLP stanPipeline, Map<String, String> nerMap,List<String> anwsers ) {

        Map<List<Concept>, List<String>> roleMap = new HashMap<>();
        ArrayList<String> WordsToActivate = new ArrayList();
        text.addAll(getTokenizedQuestionAnwsers(stanPipeline,anwsers));


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

            Word head = getHeadWord(s);
            if(head!=null&&!isAlreadyinList(WordsToActivate,head.getForm())) {
                WordsToActivate.add(head.getForm());
            }
            if(s!=null) {
                for (Word w : s) {
                    if ((w.getPOS().startsWith("N") || w.getPOS().startsWith("V")) && w.getHead().equals(head) && !isAlreadyinList(WordsToActivate,w.getForm())) {
                        WordsToActivate.add(w.getForm());
                    }
                }
            }

        }
        System.out.println("WordsToActivate head: "+WordsToActivate);
        return getConcepts(WordsToActivate,stanPipeline,nerMap);
    }


    public static List<List<String>> getTokenizedQuestionAnwsers(StanfordCoreNLP stanPipeline,List<String> anwsers){
        List<List<String>> result = new LinkedList<>();
        for(String a: anwsers) {
            Annotation annotation = AnalyseUtil.getAnnotation(a, stanPipeline);
            result.addAll(AnalyseUtil.tokenizeText(annotation));
        }

        return result;
    }

    public static boolean isAlreadyinList(List<String> strings, String string){
        for(String s : strings){
            if(s.equals(string)){
                return true;
            }
        }
        return false;
    }



    public static Word getHeadWord(Sentence s){
        if(s!=null) {

            for (Word w : s) {
                if (w.getHeadId() == 0) {
                    return w;
                }
            }
        }
        return null;
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

    public static List<Concept> getConcepts(List<String> wordsToActivate, StanfordCoreNLP stanPipeline, Map<String, String> nerMap){
        List<Concept> conceptsToActivate = new LinkedList<>();
        for (String answer : wordsToActivate) {
            if (answer.contains(" ")) {
                for (String word : answer.split(" ")) {
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
        return conceptsToActivate;
    }

    public static List<Map<Concept, List<? extends Marker>>> setStartActivation(PronConcept pron) {
        List<Map<Concept, List<? extends Marker>>> startActivation = new ArrayList<>();
        Map<Concept, List<? extends Marker>> conceptMarkerMap = new HashMap<>();
        List<Marker> markers1 = new ArrayList<>();
        DoubleMarkerWithOrigin startMarker = new DoubleMarkerWithOrigin();
        startMarker.setActivation(MarkerPassingConfig.getStartActivation());

        startMarker.setOrigin(pron);
        markers1.add(startMarker);
        conceptMarkerMap.put(pron, markers1);
        startActivation.add(conceptMarkerMap);

        return startActivation;

    }

    public static List<Map<Concept, List<? extends Marker>>> setStartActivation(Concept pron) {
        List<Map<Concept, List<? extends Marker>>> startActivation = new ArrayList<>();
        Map<Concept, List<? extends Marker>> conceptMarkerMap = new HashMap<>();
        List<Marker> markers1 = new ArrayList<>();
        DoubleMarkerWithOrigin startMarker = new DoubleMarkerWithOrigin();
        startMarker.setActivation(MarkerPassingConfig.getStartActivation());

        startMarker.setOrigin(pron);
        markers1.add(startMarker);
        conceptMarkerMap.put(pron, markers1);
        startActivation.add(conceptMarkerMap);

        return startActivation;

    }

    public static List<Map<Concept, List<? extends Marker>>> setStartActivation(PronConcept pron, List<Concept> answers) {
        List<Map<Concept, List<? extends Marker>>> startActivation = new ArrayList<>();
        Map<Concept, List<? extends Marker>> conceptMarkerMap = new HashMap<>();
        List<Marker> markers1 = new ArrayList<>();
        DoubleMarkerWithOrigin startMarker = new DoubleMarkerWithOrigin();
        //TODO: Discuss Why thousand times Markerpassing.getStartActivation?
//        startMarker.setActivation(1000 * MarkerPassingConfig.getStartActivation());
        startMarker.setActivation(MarkerPassingConfig.getStartActivation());

        startMarker.setOrigin(pron);
        startMarker.setAnswers(answers);
        markers1.add(startMarker);
        conceptMarkerMap.put(pron, markers1);
        startActivation.add(conceptMarkerMap);

        return startActivation;

    }

    public static List<Map<Concept, List<? extends Marker>>> setStartActivation(QuestionConcept pron, List<Concept> answers) {
        List<Map<Concept, List<? extends Marker>>> startActivation = new ArrayList<>();
        Map<Concept, List<? extends Marker>> conceptMarkerMap = new HashMap<>();
        List<Marker> markers1 = new ArrayList<>();
        DoubleMarkerWithOrigin startMarker = new DoubleMarkerWithOrigin();
        //TODO: Discuss Why thousand times Markerpassing.getStartActivation?
//        startMarker.setActivation(1000 * MarkerPassingConfig.getStartActivation());
        startMarker.setActivation(MarkerPassingConfig.getStartActivation());

        startMarker.setOrigin(pron);
        startMarker.setAnswers(answers);
        markers1.add(startMarker);
        conceptMarkerMap.put(pron, markers1);
        startActivation.add(conceptMarkerMap);

        return startActivation;

    }

    public static List<Map<Concept, List<? extends Marker>>> setStartActivation(List<Concept> conceptsToActivate) {
        List<Map<Concept, List<? extends Marker>>> startActivation = new ArrayList<>();
        for (Concept c : conceptsToActivate) {
            Map<Concept, List<? extends Marker>> conceptMarkerMap = new HashMap<>();
            List<Marker> markers = new ArrayList<>();
            DoubleMarkerWithOrigin startMarker = new DoubleMarkerWithOrigin();
            //TODO: Discuss Why thousand times Markerpassing.getStartActivation?
//            startMarker.setActivation(1000 * MarkerPassingConfig.getStartActivation());
            startMarker.setActivation(MarkerPassingConfig.getStartActivation());
            startMarker.setOrigin(c);
            markers.add(startMarker);
            conceptMarkerMap.put(c, markers);
            startActivation.add(conceptMarkerMap);
        }

        return startActivation;

    }
}
