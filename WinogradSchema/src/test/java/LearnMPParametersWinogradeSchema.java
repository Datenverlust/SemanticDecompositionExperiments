import de.dailab.nsm.decomposition.DecompositionConfig;
import de.dailab.nsm.decomposition.graph.spreadingActivation.MarkerPassing.MarkerPassingConfig;
import de.dailab.nsm.decomposition.graph.spreadingActivation.MarkerPassing.ParameterLearner.ParamerterLearningExperimentGeneticAlgorithm;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.*;

/**
 * Created by faehndrich on 09.04.16.
 */
public class LearnMPParametersWinogradeSchema {
    static Double goal = 1000000.0;
    static int stagnationCount = 0;
    static int stagnationCountLimit = 10;
    static int generationSize = 16;
    static String evalutationResultFile = System.getProperty("user.home") + File.separator + ".decomposition" + File.separator + "WinogradSchemaEvaluation.csv";
    MarkerPassingConfig bestConfiguration = null;
    Random random = null;
    Double bestResult = 0.0;
    ExecutorService cachedThreadPool = Executors.newFixedThreadPool(DecompositionConfig.getThreadCount());
    WSMarkerPassingConfigurationEvaluator evaluator = new WSMarkerPassingConfigurationEvaluator();

    public static void main(String[] args) {
        LearnMPParametersWinogradeSchema test = new LearnMPParametersWinogradeSchema();
        test.init();
        Double result = 0.0d;
        long generationCount = 0;
        long resetCount = 0;
        FileWriter fw = null;
        try {
            fw = new FileWriter(evalutationResultFile, true);
            fw.write("StartActivation;Thresholds;DefinitionLinkWeight;SynonymLinkWeight;AntonymLinkWeight;HypernymLinkWeight;HyponymLinkWeight;TerminationPulsCount;DoubleActivationLimit;DecompositionDepth;Folds;CurrentResult");
            BufferedWriter bw = new BufferedWriter(fw);
            bw.newLine();
            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        while (result < goal) {
            if (stagnationCount < stagnationCountLimit) {
                result = test.learn();
               System.out.print('\r');
               System.out.print("Generation: " + ++generationCount + " Resets: " + resetCount + " Best Result: " + test.bestResult + " Current Result: " + result);

            } else {
                test.init();
                stagnationCount = 0;
                System.out.print('\r');
                resetCount++;
            }
        }
//        System.out.println(test.bestConfiguration);
    }

    private Double learn() {

        //MarkerPassingConfig mutation = mutateConfiguration(bestConfiguration);
        //double result = evaluator.evaluateIndividual(mutation);
        Collection<Future<IndividualResult>> futures = new LinkedList<>();

        for (int i = 0; i < generationSize; i++) {
            futures.add(startEvaluationThread());
        }

        double result = 0;
        //Wait for evaluation to be done.
        for (Future<IndividualResult> future : futures)
            try {
                synchronized (bestResult) {
                    IndividualResult individualResult = future.get();
                    result = individualResult.getEvaluation();

                    FileWriter fw = new FileWriter(evalutationResultFile, true);
                    fw.write(individualResult.getConfig().toString() + ";" + result);
                    BufferedWriter bw = new BufferedWriter(fw);
                    bw.newLine();
                    bw.flush();
                    bw.close();
                    System.out.println(individualResult.getConfig().toString() + ";" + result);
                    if (result > bestResult) {
                        bestConfiguration = individualResult.getConfig();
                        stagnationCount = 0;
                        System.out.println(bestConfiguration);
                        System.out.print((result / (goal / 100)) + "% Best Result: " + result + " with delat: " + (result - bestResult));
                        bestResult = result;
                    } else {
                        stagnationCount++;
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        return result;
    }


    private Future<IndividualResult> startEvaluationThread() {
        Future<IndividualResult> future = cachedThreadPool.submit(new Callable<IndividualResult>() {
            @Override
            public IndividualResult call() throws Exception {
                MarkerPassingConfig mutation = mutateConfiguration(bestConfiguration);
                IndividualResult result = new IndividualResult(mutation, evaluator.evaluateIndividual(mutation));
                return result;
            }
        });
        return future;
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
        markerPassingConfigCandidate.setAntonymLinkWeight(ParamerterLearningExperimentGeneticAlgorithm.getRandomDoubleMutation(bestConfiguration.getAntonymLinkWeight(), -1, 1));
        markerPassingConfigCandidate.setHypernymLinkWeight(ParamerterLearningExperimentGeneticAlgorithm.getRandomDoubleMutation(bestConfiguration.getHypernymLinkWeight(), -1, 1));
        markerPassingConfigCandidate.setHyponymLinkWeight(ParamerterLearningExperimentGeneticAlgorithm.getRandomDoubleMutation(bestConfiguration.getHyponymLinkWeight(), -1, 1));
        markerPassingConfigCandidate.setDefinitionLinkWeight(ParamerterLearningExperimentGeneticAlgorithm.getRandomDoubleMutation(bestConfiguration.getDefinitionLinkWeight(), -1, 1));

        for (String relationName : markerPassingConfigCandidate.getArbitraryRelationWeights().keySet()) {
            double currentWeight = markerPassingConfigCandidate.getLinkWeightForRelationWithName(relationName);
            markerPassingConfigCandidate.setArbitraryRelationLinkWeight(relationName, ParamerterLearningExperimentGeneticAlgorithm.getRandomDoubleMutation(currentWeight, -1, 1));
        }

        return markerPassingConfigCandidate;
    }

    private void init() {
        random = new SecureRandom(); //Random();
        bestConfiguration = ParamerterLearningExperimentGeneticAlgorithm.createNewMarkerpassingConfig(random);
        //run once to create all definitions
        bestResult = evaluator.evaluateIndividual(bestConfiguration);
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
