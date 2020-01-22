

/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

import com.opencsv.CSVReader;
import de.kimanufaktur.nsm.decomposition.Concept;
import de.kimanufaktur.nsm.decomposition.graph.spreadingActivation.MarkerPassing.MarkerPassingConfig;
import edu.mit.jwi.RAMDictionary;
import se.lth.cs.srl.CompletePipeline;
import se.lth.cs.srl.corpus.Sentence;
import se.lth.cs.srl.corpus.Word;
import se.lth.cs.srl.options.CompletePipelineCMDLineOptions;
import se.lth.cs.srl.options.FullPipelineOptions;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

//import de.kimanufaktur.nsm.decomposition.graph.spreadingActivation.MarkerPassing.MarkerPassingConfig;

/**
     * Created by linus on 31.03.18.
     */
    public class DataLoader {

    private static String language="eng";
    private static String lemma="/models/lemmatizer-eng-4M-v36.mdl";
    private static String tagger="/models/tagger-eng-4M-v36.mdl";
    private static String parser="models/parser-eng-12M-v36.mdl";
    private static String srl="/models/CoNLL2009-ST-English-ALL.anna-3.3.srl-4.1.srl.model";
    private static String[] pipelineOptions=getOptions();
    private static String[] getOptions(){
        List<String> optionList=new ArrayList<>();
        optionList.add(language);
        optionList.add("-lemma");
        String lemmaPath = SemanticRoleLabeler.class.getResource(lemma).getPath();
        optionList.add(lemmaPath);
        optionList.add("-tagger");
        String taggerPath = SemanticRoleLabeler.class.getResource(tagger).getPath();
        optionList.add(taggerPath);
        optionList.add("-parser");
        String parserPath = SemanticRoleLabeler.class.getResource(parser).getPath();
        optionList.add(parserPath);
        optionList.add("-srl");
        String srlPath = SemanticRoleLabeler.class.getResource(srl).getPath();
        optionList.add(srlPath);

        return optionList.toArray(new String[optionList.size()]);
    }
    private static Random randomGenerator = new Random();


        public static ArrayList<Question> ReadExampleDataSet() {
            String u = "/Users/linusschacht/Desktop/ai2-science-questions/MiddleSchool-NDMC-Train.csv";
            //String u = System.getProperty("user.home") + File.separator + ".decomposition" + File.separator + "DataSet" + File.separator+ "Rubenstein1965.csv";
            ArrayList<Question> result = new ArrayList<>(70);
            BufferedReader reader = null;
            ArrayList<Question> randomResult = new ArrayList<>(70);

            try {
                reader = new BufferedReader(new FileReader(u));
                CSVReader reader2 = new CSVReader(new FileReader(u),',', '"');
                String[] nextLine;
                int i = 0;
                reader2.readNext();

                while ((nextLine = reader2.readNext())  != null) {

                        System.out.println(nextLine[9]);
                        String questionContent = nextLine[9];
                        String[] content = questionContent.split("\\(A\\)");
                        String question = content[0].replace("(","").replace(")","");
                        content = content[1].split("\\(B\\)");
                        String answerA = content[0].replace("(","").replace(")","");
                        content = content[1].split("\\(C\\)");
                        String answerB = content[0].replace("(","").replace(")","");
                        content = content[1].split("\\(D\\)");
                        String answerC = content[0].replace("(","").replace(")","");
                        String answerD = "";
                        if(content.length>1) {
                            answerD = content[1].replace("(", "").replace(")", "");
                        }


                        Question pair = new Question(question, answerA, answerB, answerC, answerD, nextLine[3].replace(",",""));
                        result.add(pair);
                        DataLoader.questionGetCorrectAnswer(pair);


                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            for(int j =0;j<=result.size()-1;j++){
                Question q = result.get(j);
                if(q.questionContent.chars().count()<180) {
                    randomResult.add(anyQuestion(result));
                }
            }
            if(randomResult!=null) {
                QuestionClassifier.classifyData(randomResult);
            }
            return randomResult;

        }

        static MarkerPassingConfig getBestConfig(String name){
            //String u = System.getProperty("user.home") + File.separator + ".decomposition" + File.separator + "DataSet" + File.separator+ "Rubenstein1965.csv";
            ArrayList<Question> result = new ArrayList<>(70);
            BufferedReader reader = null;
            ArrayList<Question> randomResult = new ArrayList<>(70);
            MarkerPassingConfig config = new MarkerPassingConfig();

            try {

                CSVReader reader2 = new CSVReader(new FileReader(name+"3.csv"),';', '"');
                String[] nextLine;
                int i = 0;
                reader2.readNext();
                Double bestResult=-1.0;
                while ((nextLine = reader2.readNext())  != null) {
                        if(Double.parseDouble(nextLine[nextLine.length-1])>bestResult){
                            bestResult = Double.parseDouble(nextLine[nextLine.length-1]);
                        }
                }
                reader2 = new CSVReader(new FileReader(name+"3.csv"),';', '"');
                i = 0;
                reader2.readNext();
                while ((nextLine = reader2.readNext())  != null) {
                    if(Double.parseDouble(nextLine[nextLine.length-1])==bestResult){
                        config.setStartActivation(Double.parseDouble(nextLine[0]));
                        config.setThreshold(Double.parseDouble(nextLine[1]));
                        config.setDefinitionLinkWeight(Double.parseDouble(nextLine[2]));
                        config.setSynonymLinkWeight(Double.parseDouble(nextLine[3]));
                        config.setAntonymLinkWeight(Double.parseDouble(nextLine[4]));
                        config.setHypernymLinkWeight(Double.parseDouble(nextLine[5]));
                        config.setHyponymLinkWeight(Double.parseDouble(nextLine[6]));
                        config.setNerLinkWeight(Double.parseDouble(nextLine[7]));
                        config.setRoleLinkWeight(Double.parseDouble(nextLine[8]));
                        config.setSyntaxLinkWeight(Double.parseDouble(nextLine[9]));
                        config.setTerminationPulsCount(Integer.parseInt(nextLine[10]));
                        config.setDoubleActivationLimit(Double.parseDouble(nextLine[11]));

                    }
                }



            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return config;
        }

    public static void questionGetCorrectAnswer(Question q){
            if(q.rightAwnser.equals("A")){
                q.setRightAwnser(q.getAnwserA());
            }else if(q.rightAwnser.equals("B")){
                q.setRightAwnser(q.getAnwserB());
            }else if(q.rightAwnser.equals("C")){
                q.setRightAwnser(q.getAnwserC());
            }else if(q.rightAwnser.equals("D")){
                q.setRightAwnser(q.getAnwserD());
            }

        }

    public static CompletePipeline getPipeline() {
        FullPipelineOptions options = new CompletePipelineCMDLineOptions();
        options.parseCmdLineArgs(pipelineOptions);
        CompletePipeline pipeline=null;
        try {
            pipeline = CompletePipeline.getCompletePipeline(options);
        } catch (Exception e){
            e.printStackTrace();
        }
        return pipeline;
    }

    public static ArrayList<String> parse(List<List<String>> text, CompletePipeline pipeline) {

        Map<List<Concept>, List<String>> roleMap = new HashMap<>();
        ArrayList<String> WordsToActivate = new ArrayList();


        //Für jeden im Schema vorkommenden Satz

        for(int i=0;i<text.size();i++) {
            List<String> sentence=text.get(i);
            List<String> newText = new ArrayList<>();
            newText.addAll(sentence);
            Sentence s = null;

            try {
                s = pipeline.parse(newText);

            } catch (Exception e) {
                e.printStackTrace();
            }

            for(Word w:s){
                if(w.getPOS().startsWith("N")||w.getPOS().startsWith("V")){
                    WordsToActivate.add(w.getForm());
                }
            }

        }
        return WordsToActivate;
    }

    public static String getVerbs(List<List<String>> text, CompletePipeline pipeline) {

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

            for(Word w:s){
                if(w.getPOS().startsWith("V")){
                    return w.getForm();
                }
            }

        }

        return null;
    }

    public static Map<QuestionClass,List<Question>> getQuestionMap(){
            List<Question> questions = ReadExampleDataSet();
            Map<QuestionClass,List<Question>> map = new HashMap<>();
            map.put(QuestionClass.KNOWLEDGE, new ArrayList<Question>());
            map.put(QuestionClass.APPLICATION, new ArrayList<Question>());
            map.put(QuestionClass.ANALYSIS, new ArrayList<Question>());
            map.put(QuestionClass.SYNTHESIS, new ArrayList<Question>());
            map.put(QuestionClass.COMPREHENSION, new ArrayList<Question>());
            map.put(QuestionClass.EVALUATION, new ArrayList<Question>());
            for(Question q:questions){
                map.get(q.type).add(q);
            }
            return map;
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

    public static Question anyQuestion(ArrayList<Question> questions)
    {
        int index = randomGenerator.nextInt(questions.size());
        Question q = questions.get(index);
        return q;
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

        public static void main(String[] args){

            System.out.println(22);
            getBestConfig("KNOWLEDGE");

        }

    }


