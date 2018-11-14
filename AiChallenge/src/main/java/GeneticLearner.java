/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

import de.dailab.nsm.decomposition.DecompositionConfig;
import de.dailab.nsm.decomposition.graph.spreadingActivation.MarkerPassing.MarkerPassingConfig;
import de.dailab.nsm.decomposition.graph.spreadingActivation.MarkerPassing.MarkerPassingConfig;
import de.dailab.nsm.decomposition.graph.spreadingActivation.MarkerPassing.ParameterLearner.LearnMPParameters;
import de.dailab.nsm.decomposition.graph.spreadingActivation.MarkerPassing.ParameterLearner.MarkerPassingConfigurationEvaluator;
import de.dailab.nsm.decomposition.graph.spreadingActivation.MarkerPassing.ParameterLearner.ParamerterLearningExperimentGeneticAlgorithm;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.*;

public class GeneticLearner {
    static Double goal = 0.6;
    static int stagnationCount = 0;
    static int stagnationCountLimit = 1000;
    static int generationSize = 16;
    static String evalutationResultFile = System.getProperty("user.home") + File.separator + ".decomposition" + File.separator + "SemanticSimilarityEvaluation.csv";
    MarkerPassingConfig bestConfiguration = null;
    Random random = null;
    Double bestResult = -1.0;
    ExecutorService cachedThreadPool = Executors.newFixedThreadPool(DecompositionConfig.getThreadCount());
    DoubleMarkerConfigEvaluator evaluator = new DoubleMarkerConfigEvaluator();


    public static void main(String[] args) {
        List<Question> questions = DataLoader.ReadExampleDataSet().subList(0,2);
        Map<QuestionClass,List<Question>> map = DataLoader.getQuestionMap();
        for(QuestionClass qc : map.keySet()) {
            stagnationCount = 0;
            if (map.get(qc).size() < 8) {
                questions = map.get(qc);
            } else {
                questions = map.get(qc).subList(0, 8);
            }

                List<GraphCache> list = QuestionAnswer.getGraphs(questions);
                GeneticLearner test = new GeneticLearner();
                test.init(list);
                Double result = 0.0d;
                long generationCount = 0;
                long resetCount = 0;
                FileWriter fw = null;
                try {
                    fw = new FileWriter(qc.name() + "3.csv", true);
                    fw.write("StartActivation;Thresholds;DefinitionLinkWeight;SynonymLinkWeight;AntonymLinkWeight;HypernymLinkWeight;HyponymLinkWeight;NerLinkWeight;RoleLinkWeight,SyntaxLinkWeight,TerminationPulsCount;DoubleActivationLimit;DecompositionDepth;Folds;CurrentResult");
                    BufferedWriter bw = new BufferedWriter(fw);
                    bw.newLine();
                    bw.flush();
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                while (stagnationCount < stagnationCountLimit) {
                    result = test.learn(list, qc.name() + "3.csv");
                    System.out.print('\r');
                    System.out.print("Generation: " + ++generationCount + " Resets: " + resetCount + " Best Result: " + test.bestResult + " Current Result: " + result);

                    resetCount++;

                }
        }

//        System.out.println(test.bestConfiguration);
    }



    private Double learn(List<GraphCache> questions,String questionType) {


        //MarkerPassingConfig mutation = mutateConfiguration(bestConfiguration);
        //double result = evaluator.evaluateIndividual(mutation);
        Collection<IndividualResult> futures = new LinkedList<>();


        for (int i = 0; i < generationSize; i++) {
            futures.add(startEvaluationThread(questions));
        }

        double result = 0;
        //Wait for evaluation to be done.
        for (IndividualResult future : futures)
            try {

                IndividualResult individualResult = future;
                result = individualResult.getEvaluation();

                FileWriter fw = new FileWriter(questionType, true);
                fw.write(individualResult.getConfig().toString() + ";" + result);
                BufferedWriter bw = new BufferedWriter(fw);
                bw.newLine();
                bw.flush();
                bw.close();
                System.out.println(individualResult.getConfig().toString() + ";" + result);
                if (result > bestResult && !Double.isNaN(result) && result!=0) {
                    bestConfiguration = individualResult.getConfig();
                    this.stagnationCount = 0;
                    System.out.println(bestConfiguration);
                    System.out.print((result / (goal / 100)) + "% Best Result: " + result + " with delat: " + (result - bestResult));
                    bestResult = result;
                } else {
                    this.stagnationCount++;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        return result;
    }


    private IndividualResult startEvaluationThread(List<GraphCache> questionList) {

        MarkerPassingConfig mutation = mutateConfiguration(bestConfiguration);
        IndividualResult result = new IndividualResult(mutation, evaluator.getFitness(mutation,questionList));
        return result;
    }


    private MarkerPassingConfig mutateConfiguration(MarkerPassingConfig bestConfiguration) {
        MarkerPassingConfig markerPassingConfigCandidate = new MarkerPassingConfig();
        //Marker Passing parameters
        markerPassingConfigCandidate.setThreshold(ParamerterLearningExperimentGeneticAlgorithm.getRandomDoubleMutation(bestConfiguration.getThreshold(), 0, 3));
        markerPassingConfigCandidate.setStartActivation(ParamerterLearningExperimentGeneticAlgorithm.getRandomDoubleMutation(bestConfiguration.getStartActivation(), markerPassingConfigCandidate.getThreshold(), 1000));
        markerPassingConfigCandidate.setTerminationPulsCount(ParamerterLearningExperimentGeneticAlgorithm.getRandomIntegerMutation(bestConfiguration.getTerminationPulsCount(), 1, 100));
        markerPassingConfigCandidate.setDoubleActivationLimit(ParamerterLearningExperimentGeneticAlgorithm.getRandomDoubleMutation(bestConfiguration.getDoubleActivationLimit(), 0, 2000));
        //Set weights
        markerPassingConfigCandidate.setSynonymLinkWeight(ParamerterLearningExperimentGeneticAlgorithm.getRandomDoubleMutation(bestConfiguration.getSynonymLinkWeight(), -1, 1));
        markerPassingConfigCandidate.setAntonymLinkWeight(ParamerterLearningExperimentGeneticAlgorithm.getRandomDoubleMutation(bestConfiguration.getAntonymLinkWeight(), -1, 0));
        markerPassingConfigCandidate.setHypernymLinkWeight(ParamerterLearningExperimentGeneticAlgorithm.getRandomDoubleMutation(bestConfiguration.getHypernymLinkWeight(), -1, 1));
        markerPassingConfigCandidate.setHyponymLinkWeight(ParamerterLearningExperimentGeneticAlgorithm.getRandomDoubleMutation(bestConfiguration.getHyponymLinkWeight(), -1, 1));
        markerPassingConfigCandidate.setDefinitionLinkWeight(ParamerterLearningExperimentGeneticAlgorithm.getRandomDoubleMutation(bestConfiguration.getDefinitionLinkWeight(), -1, 3));
        markerPassingConfigCandidate.setNerLinkWeight(ParamerterLearningExperimentGeneticAlgorithm.getRandomDoubleMutation(bestConfiguration.getHyponymLinkWeight(), -1, 2));
        markerPassingConfigCandidate.setRoleLinkWeight(ParamerterLearningExperimentGeneticAlgorithm.getRandomDoubleMutation(bestConfiguration.getHyponymLinkWeight(), -1, 2));
        markerPassingConfigCandidate.setSyntaxLinkWeight(ParamerterLearningExperimentGeneticAlgorithm.getRandomDoubleMutation(bestConfiguration.getHyponymLinkWeight(), -1, 2));




        return markerPassingConfigCandidate;
    }

    private void init(List<GraphCache> questions) {
        random = new Random(); //Random();
        bestConfiguration = ParamerterLearningExperimentGeneticAlgorithm.createNewMarkerpassingConfig(random);
        //run once to create all definitions
        bestResult = evaluator.getFitness(bestConfiguration,questions);
    }

    private class IndividualResult {
        double evaluation;
        MarkerPassingConfig config;

        public IndividualResult(MarkerPassingConfig mutation, double result) {
            this.evaluation = result;
            this.config = mutation;
        }

        public double getEvaluation() {
            return evaluation;
        }

        public void setEvaluation(double evaluation) {
            this.evaluation = evaluation;
        }

        public MarkerPassingConfig getConfig() {
            return config;
        }

        public void setConfig(MarkerPassingConfig config) {
            this.config = config;
        }


    }




}
