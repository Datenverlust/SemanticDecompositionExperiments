
/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

import java.io.*;
import java.util.*;

import de.dailab.nsm.decomposition.AnalyseUtil;
import de.dailab.nsm.decomposition.dictionaries.wordnet.WordNetCrawler;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.POS;
import edu.stanford.nlp.classify.*;
import edu.stanford.nlp.ling.Datum;
import edu.stanford.nlp.ling.RVFDatum;
import edu.stanford.nlp.objectbank.ObjectBank;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.Pair;
import se.lth.cs.srl.CompletePipeline;
import se.lth.cs.srl.corpus.Sentence;
import se.lth.cs.srl.corpus.Word;

enum QuestionClass{
    APPLICATION,
    KNOWLEDGE,
    COMPREHENSION,
    ANALYSIS,
    SYNTHESIS,
    EVALUATION
}


class QuestionClassifier {

     String svmLightLearn = "package/svm_learn";
     String svmStructLearn = "package/svm_multiclass_learn";
     String svmPerfLearn = "package/svm_perf_learn";
     String svmLightClassify = "package/svm_classify";
     String svmStructClassify = "package/svm_multiclass_classify";
     String svmPerfClassify = "package/svm_perf_classify";

        private static String where = "";

        public static void main(String[] args) throws Exception {
            QuestionClassifier questionClassifier = new QuestionClassifier();
            if (args.length > 0) {
                where = args[0] + File.separator;
            }
            //createTrainingsData();
            //createCoarseTrainingsData();
            //createData();
            //createDataSet();

            ColumnDataClassifier cdcLoader = new ColumnDataClassifier(where + "questionData.prop");




            GeneralDataset data = cdcLoader.readTrainingExamples(where + "questionData.train");













            System.out.println("Coarse classification Data");
            System.out.println("Training ColumnDataClassifier");
            ColumnDataClassifier cdc = new ColumnDataClassifier(where + "question.prop");
            System.out.println("test");
            cdc.trainClassifier(where + "questionA.train");
            //tried to create Datset with help of the columnDataClassifier(need changes)
           GeneralDataset g = cdc.readTrainingExamples(where + "questionA.train");
            GeneralDataset test = cdc.readTrainingExamples(where + "questionA.test");




            SVMLightClassifierFactory c = new SVMLightClassifierFactory<>(questionClassifier.svmLightLearn, questionClassifier.svmStructLearn, questionClassifier.svmPerfLearn);
            c.setC(1.6);
            c.setUseSigmoid(true);
            SVMLightClassifier cl = c.trainClassifierBasic(g);











            System.out.println();
            System.out.println("Testing accuracy of ColumnDataClassifier");
            Pair performance = cdc.testClassifier(where + "questionA.test");
            System.out.printf("Accuracy: %.3f; macro-F1: %.3f%n", performance.first(), performance.second());
            System.out.println("SVM accuracy: "+cl.evaluateAccuracy(test));

            System.out.println("Testing predictions of ColumnDataClassifier");


            Iterator i = data.iterator();
            while(i.hasNext()){
                RVFDatum d =(RVFDatum)i.next();
                System.out.println("Klasse von "+d.label().toString()+"   " +cdc.classOf(d)+ cdc.scoresOf(d));
            }

            i = test.iterator();
            int f = 0;
            while(i.hasNext()){
                if(f>10)
                    break;
                RVFDatum d =(RVFDatum)i.next();
                System.out.println("Klasse von"+d.label().toString() +cdc.classOf(d)+ cdc.scoresOf(d));
                f++;
            }




        }

    public static void classifyData(List<Question> questions){
        List<String> list = new ArrayList<>();
        ColumnDataClassifier cdc = new ColumnDataClassifier(where + "question.prop");
        System.out.println("test");
        try {
            cdc.trainClassifier(where + "questionA.train");
        } catch (IOException e) {
            e.printStackTrace();
        }


        StanfordCoreNLP stanPipeline = AnalyseUtil.tokenizePipeline();
        CompletePipeline srlPipeline = SemanticRoleLabeler.getPipeline();
        WordNetCrawler wordNetCrawler = new WordNetCrawler();
        wordNetCrawler.initWordNet();



        for (Question u : questions) {


            String hypernyms = "";
            String questionWord = " ";
            List<String> hypernymList = new ArrayList<>();

            Annotation annotation = AnalyseUtil.getAnnotation(u.questionContent, stanPipeline);

            List<List<String>> tokenized = AnalyseUtil.tokenizeText(annotation);
            hypernymList = AnalyseUtil.getHeadWord(annotation);


            for (int j = 0; j < tokenized.size(); j++) {
                List<String> sentence = tokenized.get(j);
                List<String> newText = new ArrayList<>();
                newText.add("<root>");
                newText.addAll(sentence);
                Sentence sen = null;

                try {
                    sen = srlPipeline.parse(newText);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                for (Word w : sen) {
                    if (w.getPOS().startsWith("W")) {
                        questionWord = w.getForm();
                    }
                    if (AnalyseUtil.listContainsHead(hypernymList, w.getForm())) {
                        Set<IWord> words = wordNetCrawler.getHypernyms(w.getLemma(), POS.NOUN, 3);
                        for (IWord word : words) {
                            hypernyms = hypernyms + (word.getLemma() + " ");
                        }
                        hypernymList.add(w.getForm());
                    }


                }
            }
            String s = u.questionContent+"\t" +u.questionContent + "\t" + questionWord + "\t" +hypernyms;
            String questionClass = cdc.classOf(cdc.makeDatumFromLine(s));
            System.out.println(questionClass);
            if(questionClass.toLowerCase().equals(QuestionClass.ANALYSIS.name().toLowerCase()))
                u.setType(QuestionClass.ANALYSIS);
            if(questionClass.toLowerCase().equals(QuestionClass.SYNTHESIS.name().toLowerCase()))
                u.setType(QuestionClass.SYNTHESIS);
            if(questionClass.toLowerCase().equals(QuestionClass.APPLICATION.name().toLowerCase()))
                u.setType(QuestionClass.APPLICATION);
            if(questionClass.toLowerCase().equals(QuestionClass.COMPREHENSION.name().toLowerCase()))
                u.setType(QuestionClass.COMPREHENSION);
            if(questionClass.toLowerCase().equals(QuestionClass.EVALUATION.name().toLowerCase()))
                u.setType(QuestionClass.EVALUATION);
            if(questionClass.toLowerCase().equals(QuestionClass.KNOWLEDGE.name().toLowerCase()))
                u.setType(QuestionClass.KNOWLEDGE);

        }

        System.out.println();

        //trainingData.add(makeQuestionFeatures(list,strings[1].split(" ")[0]));



    }

        public static void createData(){
            List<Question> questions = DataLoader.ReadExampleDataSet().subList(0,20);
            List<String> trainingData = new ArrayList<>();
            List<String> testData = new ArrayList<>();
            List<Datum<String,String>> question = new ArrayList<>();
            List<String> list = new ArrayList<>();


            StanfordCoreNLP stanPipeline = AnalyseUtil.getFullPipeline();
            CompletePipeline srlPipeline = SemanticRoleLabeler.getPipeline();
            WordNetCrawler wordNetCrawler = new WordNetCrawler();
            wordNetCrawler.initWordNet();
            questions.add(new Question("What city would you be in if you were feeding the pigeons in the Piazza San Marco","","","","",""));


                for (Question u : questions) {


                    String hypernyms = "";
                    String questionWord = " ";
                    List<String> hypernymList = new ArrayList<>();

                    Annotation annotation = AnalyseUtil.getAnnotation(u.questionContent, stanPipeline);

                    List<List<String>> tokenized = AnalyseUtil.tokenizeText(annotation);
                    hypernymList = AnalyseUtil.getHeadWord(annotation);


                    for (int j = 0; j < tokenized.size(); j++) {
                        List<String> sentence = tokenized.get(j);
                        List<String> newText = new ArrayList<>();
                        newText.add("<root>");
                        newText.addAll(sentence);
                        Sentence sen = null;

                        try {
                            sen = srlPipeline.parse(newText);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        for (Word w : sen) {
                            if (w.getPOS().startsWith("W")) {
                                questionWord = w.getForm();
                            }
                            if (AnalyseUtil.listContainsHead(hypernymList, w.getForm())) {
                                Set<IWord> words = wordNetCrawler.getHypernyms(w.getLemma(), POS.NOUN, 3);
                                for (IWord word : words) {
                                    hypernyms = hypernyms + (word.getLemma() + " ");
                                }
                                hypernymList.add(w.getForm());
                            }


                        }
                    }
                    list.add(u.questionContent+"\t" +u.questionContent + "\t" + questionWord + "\t" +hypernyms);

                }

                //trainingData.add(makeQuestionFeatures(list,strings[1].split(" ")[0]));


                File file = new File("questionData.train");

                // creates the file
                try {
                    file.createNewFile();

                } catch (IOException e) {
                    e.printStackTrace();
                }

                // creates a FileWriter Object
                try {
                    FileWriter writer = new FileWriter(file);
                    for (String q : list) {
                        writer.write(q + "\n");
                    }
                    writer.flush();
                    writer.close();


                } catch (IOException e) {
                    e.printStackTrace();
                }

        }

    public static void createDataSet(){
        // Create a training set
        List<String> dataUrls = new ArrayList<>();
        dataUrls.add("Knowledge.txt");
        dataUrls.add("Comprehension.txt");
        dataUrls.add("Application.txt");
        dataUrls.add("Analysis.txt");
        dataUrls.add("Synthesis.txt");
        dataUrls.add("Evaluation.txt");
        List<String> trainingData = new ArrayList<>();
        List<String> testData = new ArrayList<>();
        List<Datum<String,String>> question = new ArrayList<>();
        List<String> list = new ArrayList<>();


        StanfordCoreNLP stanPipeline = AnalyseUtil.getFullPipeline();
        CompletePipeline srlPipeline = SemanticRoleLabeler.getPipeline();
        WordNetCrawler wordNetCrawler = new WordNetCrawler();
        wordNetCrawler.initWordNet();

        try {
            for(String u :dataUrls) {

                BufferedReader reader = new BufferedReader(new FileReader(u));
                String nextLine;
                int i = 0;
                System.out.println(u);
                String type = u.split("\\.")[0];
                System.out.println(type);


                while ((nextLine = reader.readLine()) != null) {
                    String hypernyms = "";
                    String questionWord = " ";
                    List<String> hypernymList = new ArrayList<>();

                    Annotation annotation = AnalyseUtil.getAnnotation(nextLine, stanPipeline);

                    List<List<String>> tokenized = AnalyseUtil.tokenizeText(annotation);
                    hypernymList = AnalyseUtil.getHeadWord(annotation);


                    for (int j = 0; j < tokenized.size(); j++) {
                        List<String> sentence = tokenized.get(j);
                        List<String> newText = new ArrayList<>();
                        newText.add("<root>");
                        newText.addAll(sentence);
                        Sentence sen = null;

                        try {
                            sen = srlPipeline.parse(newText);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        for (Word w : sen) {
                            if (w.getPOS().startsWith("W")) {
                                questionWord = w.getForm();
                            }
                            if (AnalyseUtil.listContainsHead(hypernymList, w.getForm())) {
                                Set<IWord> words = wordNetCrawler.getHypernyms(w.getLemma(), POS.NOUN, 3);
                                for (IWord word : words) {
                                    hypernyms = hypernyms + (word.getLemma() + " ");
                                }
                                hypernymList.add(w.getForm());
                            }


                        }
                    }
                    list.add(type + "\t" + nextLine+ "\t" + questionWord + "\t" + hypernyms);

                }
            }
            //trainingData.add(makeQuestionFeatures(list,strings[1].split(" ")[0]));
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        Collections.shuffle(list);






        testData = list.subList(list.size()-100,list.size()-1);
        trainingData = list.subList(0,list.size()-(100));
        File file = new File("questionA.train");
        File file2 = new File("questionA.test");

        // creates the file
        try {
            file.createNewFile();
            file2.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // creates a FileWriter Object
        try {
            FileWriter writer = new FileWriter(file);
            for(String q:trainingData){
                writer.write(q+"\n");
            }
            writer.flush();
            writer.close();

            writer = new FileWriter(file2);
            for(String q:testData){
                writer.write(q+"\n");
            }

            writer.flush();
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    /**
     *
     */
        private static void createTrainingsData (){
            // Create a training set
            List<String> dataUrls = new ArrayList<>();
            dataUrls.add("QuestionClassificationData/train_1000.label.txt");
            dataUrls.add("QuestionClassificationData/train_4000.label.txt");
            dataUrls.add("QuestionClassificationData/train_5500.label.txt");
            List<String> trainingData = new ArrayList<>();
            List<String> testData = new ArrayList<>();
            List<Datum<String,String>> question = new ArrayList<>();
            List<String> list = new ArrayList<>();


            StanfordCoreNLP stanPipeline = AnalyseUtil.getFullPipeline();
            CompletePipeline srlPipeline = SemanticRoleLabeler.getPipeline();
            WordNetCrawler wordNetCrawler = new WordNetCrawler();
            wordNetCrawler.initWordNet();

            try {
                for(String u :dataUrls) {

                    BufferedReader reader = new BufferedReader(new FileReader(u));
                    String nextLine;
                    int i = 0;


                    while ((nextLine = reader.readLine()) != null) {
                        String[] strings = nextLine.split(":");
                        String[] s = strings[1].split(" ", 2);
                        String hypernyms = "";
                        String questionWord = " ";
                        List<String> hypernymList = new ArrayList<>();

                        Annotation annotation = AnalyseUtil.getAnnotation(s[1], stanPipeline);

                        List<List<String>> tokenized = AnalyseUtil.tokenizeText(annotation);
                        hypernymList = AnalyseUtil.getHeadWord(annotation);


                        for (int j = 0; j < tokenized.size(); j++) {
                            List<String> sentence = tokenized.get(j);
                            List<String> newText = new ArrayList<>();
                            newText.add("<root>");
                            newText.addAll(sentence);
                            Sentence sen = null;

                            try {
                                sen = srlPipeline.parse(newText);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            for (Word w : sen) {
                                if (w.getPOS().startsWith("W")) {
                                    questionWord = w.getForm();
                                }
                                if (AnalyseUtil.listContainsHead(hypernymList, w.getForm())) {
                                    Set<IWord> words = wordNetCrawler.getHypernyms(w.getLemma(), POS.NOUN, 3);
                                    for (IWord word : words) {
                                        hypernyms = hypernyms + (word.getLemma() + " ");
                                    }
                                    hypernymList.add(w.getForm());
                                }


                            }
                        }
                        list.add(s[0] + "\t" + s[1]); //"\t" + questionWord + "\t" + hypernyms);

                    }
                }
                    //trainingData.add(makeQuestionFeatures(list,strings[1].split(" ")[0]));
                } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }



            testData = list.subList(list.size()-500,list.size()-1);
            trainingData = list.subList(0,list.size()-500);
            File file = new File("question.train");
            File file2 = new File("question.test");

            // creates the file
            try {
                file.createNewFile();
                file2.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // creates a FileWriter Object
            try {
                FileWriter writer = new FileWriter(file);
                for(String q:trainingData){
                    writer.write(q+"\n");
                }
                writer.flush();
                writer.close();

                writer = new FileWriter(file2);
                for(String q:testData){
                    writer.write(q+"\n");
                }

                writer.flush();
                writer.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    /**
     *
     */
    private static void createCoarseTrainingsData (){
        // Create a training set
        List<String> dataUrls = new ArrayList<>();
        dataUrls.add("QuestionClassificationData/train_1000.label.txt");
        dataUrls.add("QuestionClassificationData/train_4000.label.txt");
        dataUrls.add("QuestionClassificationData/train_5500.label.txt");
        List<String> trainingData = new ArrayList<>();
        List<String> testData = new ArrayList<>();
        List<Datum<String,String>> question = new ArrayList<>();
        List<String> list = new ArrayList<>();


        StanfordCoreNLP stanPipeline = AnalyseUtil.getFullPipeline();
        CompletePipeline srlPipeline = SemanticRoleLabeler.getPipeline();
        WordNetCrawler wordNetCrawler = new WordNetCrawler();
        wordNetCrawler.initWordNet();

        try {
            for(String u :dataUrls) {

                BufferedReader reader = new BufferedReader(new FileReader(u));
                String nextLine;
                int i = 0;


                while ((nextLine = reader.readLine()) != null) {
                    String[] strings = nextLine.split(":");
                    String[] s = strings[1].split(" ", 2);
                    String hypernyms = " ";
                    String questionWord = " ";
                    List<String> hypernymList = new ArrayList<>();

                    Annotation annotation = AnalyseUtil.getAnnotation(s[1], stanPipeline);

                    List<List<String>> tokenized = AnalyseUtil.tokenizeText(annotation);
                    hypernymList = AnalyseUtil.getHeadWord(annotation);


                    for (int j = 0; j < tokenized.size(); j++) {
                        List<String> sentence = tokenized.get(j);
                        List<String> newText = new ArrayList<>();
                        newText.add("<root>");
                        newText.addAll(sentence);
                        Sentence sen = null;

                        try {
                            sen = srlPipeline.parse(newText);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        for (Word w : sen) {
                            if (w.getPOS().startsWith("W")) {
                                questionWord = w.getForm();
                            }
                            if (AnalyseUtil.listContainsHead(hypernymList, w.getForm())) {
                                Set<IWord> words = wordNetCrawler.getHypernyms(w.getLemma(), POS.NOUN, 3);
                                for (IWord word : words) {
                                    hypernyms = hypernyms + (word.getLemma() + " ");
                                }
                                hypernymList.add(w.getForm());
                            }


                        }
                    }
                    list.add(strings[0] + "\t" + s[1] + "\t" + questionWord + "\t" + hypernyms);

                }
            }
            //trainingData.add(makeQuestionFeatures(list,strings[1].split(" ")[0]));
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }



        testData = list.subList(list.size()-500,list.size()-1);
        trainingData = list.subList(0,list.size()-500);
        File file = new File("CoarseQuestion.train");
        File file2 = new File("CoarseQuestion.test");

        // creates the file
        try {
            file.createNewFile();
            file2.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // creates a FileWriter Object
        try {
            FileWriter writer = new FileWriter(file);
            for(String q:trainingData){
                writer.write(q+"\n");
            }
            writer.flush();
            writer.close();

            writer = new FileWriter(file2);
            for(String q:testData){
                writer.write(q+"\n");
            }

            writer.flush();
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    }


