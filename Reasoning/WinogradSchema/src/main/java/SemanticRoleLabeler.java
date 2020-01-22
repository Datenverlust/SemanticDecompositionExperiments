/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

import de.kimanufaktur.nsm.decomposition.Concept;
import de.kimanufaktur.nsm.decomposition.Decomposition;
import de.kimanufaktur.nsm.decompostion.Dictionaries.DictUtil;
import se.lth.cs.srl.CompletePipeline;
import se.lth.cs.srl.corpus.Predicate;
import se.lth.cs.srl.corpus.Sentence;
import se.lth.cs.srl.corpus.Word;
import se.lth.cs.srl.options.CompletePipelineCMDLineOptions;
import se.lth.cs.srl.options.FullPipelineOptions;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by Hannes on 27.03.2017.
 */
public class SemanticRoleLabeler {

    private static String language="eng";
    private static String lemma="lemmatizer-eng-4M-v36.mdl";
    private static String tagger="tagger-eng-4M-v36.mdl";
    private static String parser="parser-eng-12M-v36.mdl";
    private static String srl="CoNLL2009-ST-English-ALL.anna-3.3.srl-4.1.srl.model";
    private static String[] pipelineOptions=null;
    static CompletePipeline pipeline=null;
    private static String modelsPath = System.getProperty("user.home").toString() + File.separator + ".decomposition" + File.separator + "Models";

    private static String source = null;
    static String lemmaPath = null ;
    static String taggerPath = null ;
    static String parserPath = null ;
    static String srlPath = null ;

    public SemanticRoleLabeler(){
        init();
    }

    public static void init(){
        if (pipelineOptions == null) {
            source = "http://dainas.dai-labor.de/~faehndrich@dai/NLP/Models/";
            lemma="lemmatizer-eng-4M-v36.mdl";
            tagger="tagger-eng-4M-v36.mdl";
            parser="parser-eng-12M-v36.mdl";
            srl="CoNLL2009-ST-English-ALL.anna-3.3.srl-4.1.srl.model";
            modelsPath = System.getProperty("user.home").toString() + File.separator + ".decomposition" + File.separator + "Models";

            lemmaPath = CheckFileAndDL(modelsPath, lemma);
            taggerPath = CheckFileAndDL(modelsPath, tagger);
            parserPath = CheckFileAndDL(modelsPath, parser);
            srlPath = CheckFileAndDL(modelsPath, srl);
        }
    }

    private static String CheckFileAndDL(String path, String filename) {
        // Connect get pre tained vectos or create them form a corpus
        File model = new File(path + File.separator + filename);
        // If not availiable downlod the pre trained vector

        if (model.exists() == false) {
            new File(modelsPath).mkdirs();
            DictUtil.downloadFileParalell(source + filename, model.getPath());
            model = new File(path + File.separator + filename);
        }

        return  model.getPath();
    }

    private static String[] getOptions(){
        init();
        List<String> optionList=new ArrayList<>();
        optionList.add(language);
        optionList.add("-lemma");
        String lPath = lemmaPath;
        optionList.add(lPath);
        optionList.add("-tagger");
        String tPath = taggerPath;
        optionList.add(tPath);
        optionList.add("-parser");
        String pPath = parserPath;
        optionList.add(pPath);
        optionList.add("-srl");
        String sPath = srlPath;
        optionList.add(sPath);
        return optionList.toArray(new String[optionList.size()]);
    }
    //private static CompletePipeline pipeline;


    public static CompletePipeline getPipeline() {

        FullPipelineOptions options = new CompletePipelineCMDLineOptions();
        if(pipelineOptions == null){
            pipelineOptions = getOptions();
            options.parseCmdLineArgs(pipelineOptions);

            try {
                pipeline = CompletePipeline.getCompletePipeline(options);
            } catch (Exception e){
                e.printStackTrace();
            }
            return pipeline;
        }
        else{
            return pipeline;
        }

    }

    /**
     * pars the given text to determin the seamntic role labes for contained verbs.
     * @param text The text to processs.
     * @param pron The pronoun of interest.
     * @param pipeline the CoreNLP pipeline to use.
     * @return
     */
    public static Map<List<Concept>, List<String>> parse(List<List<String>> text, PronConcept pron, CompletePipeline pipeline) {

        Map<List<Concept>, List<String>> roleMap = new HashMap<>();


        //Für jeden im Schema vorkommenden Satz
        for(int i=0;i<text.size();i++) {
            List<String> sentence=text.get(i);
            List<String> newText = new ArrayList<>();
            newText.add("<root>");
            newText.addAll(sentence);
            Sentence s = null;

            try {
                s = pipeline.parse(newText);
            } catch (Exception e) {
                e.printStackTrace();
            }

            RoleSet roleSet = new FrameReader();
            Map<String, Map<List<Concept>, List<String>>> roleList = new HashMap<>();

            //Für jedes im Satz vorkommende Prädikat
            for (Predicate p : s.getPredicates()) {

                //Hole Framset für Prädikat
                Map<String, List<String>> newRoleSet = roleSet.readRoleSet(p.getSense());

                if (newRoleSet != null && newRoleSet.size()!=0) { //Wenn Verb in frames existiert

                    //Für jedes gefundene Argument von Prädikat
                    for (Word arg : p.getArgMap().keySet()) {

                        String argName = getLongName(p.getArgMap().get(arg));
                        List<String> argList = newRoleSet.get(argName);

                        List<Concept> thisRoles = new ArrayList<>();
                        for (Word w : getFull(arg)) {
                            Concept con = null;
                            if (w.getIdx() - 1 == pron.getWortNr() && i == pron.getSatzNr()) {
                                con = new PronConcept(pron.getOriginalName(), pron.getWortNr(), pron.getSatzNr());
                            } else if (!Decomposition.getConcepts2Ignore().contains(w.getLemma())) {
                                if (w.getLemma().equals(w.getForm().toLowerCase()))
                                    con = new Concept(w.getForm());
                                else
                                    con = new Concept(w.getLemma());
                            }
                            if (con != null) {
                                thisRoles.add(con);
                            }
                        }
                        if (argList == null) {
                            argList = new ArrayList<>();
                            if (getModifier(argName) != null)
                                argList.add(getModifier(argName));
                        }

                        if (argList.size() != 0)
                            roleMap.put(thisRoles, argList);
                    }
                }
            }
        }
        return roleMap;
    }

    private static Set<Word> getFull(Word w) {
        Set<Word> span = new TreeSet<Word>(new Comparator<Word>() {
            @Override
            public int compare(Word o1, Word o2) {
                return o1.getIdx() < o2.getIdx() ? -1
                        : o1.getIdx() > o2.getIdx() ? 1 : 0;
            }
        });
        span.add(w);

        List<Word> children = new LinkedList<Word>();
        children.addAll(w.getChildren());
        while (!children.isEmpty()) {
            Word c = children.remove(0);
            if (span.contains(c))
                continue;

            span.add(c);
            children.addAll(c.getChildren());
        }

        return span;
    }

    private static String getLongName(String name){
        if(Character.isDigit(name.charAt(1))){
            name=name.charAt(0)+"rg"+name.charAt(1);
        }
        return name;
    }

    private static String getModifier(String name){
        String[] nameParts=name.split("-");
        if(nameParts[0].equals("AM")){
            if(nameParts[1].equals("CAU")) name="cause";
            else if(nameParts[1].equals("LOC")) name="location";
            else if(nameParts[1].equals("DIR")) name="direction";
            else if(nameParts[1].equals("GOL")) name="goal";
            else if(nameParts[1].equals("MNR")) name="manner";
            else if(nameParts[1].equals("TMP")) name="time";
            else if(nameParts[1].equals("EXT")) name="extent";
            else if(nameParts[1].equals("PRP")) name="purpose";
            else if(nameParts[1].equals("DIS")) name="discourse";
            else if(nameParts[1].equals("CXN")) name="construction";
            else name=null;
        }
        return name;
    }



}


