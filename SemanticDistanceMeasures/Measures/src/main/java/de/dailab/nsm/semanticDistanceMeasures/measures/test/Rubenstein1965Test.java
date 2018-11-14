/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

package de.dailab.nsm.decompostion.graph.test;

import de.dailab.nsm.decomposition.Concept;
import de.dailab.nsm.decomposition.WordType;
import de.dailab.nsm.decomposition.graph.Evaluation;
import de.dailab.nsm.decomposition.graph.spreadingActivation.MarkerPassing.MarkerPassingConfig;
import de.dailab.nsm.decomposition.graph.spreadingActivation.MarkerPassing.ParameterLearner.MarkerPassingSemanticDistanceMeasure;
import de.dailab.nsm.semanticDistanceMeasures.DataExample;
import de.dailab.nsm.semanticDistanceMeasures.SynonymPair;
import de.dailab.nsm.semanticDistanceMeasures.data.Rubenstein1965Dataset;
import de.dailab.nsm.semanticDistanceMeasures.data.WordSimilarityDataSet;
import de.dailab.nsm.semanticDistanceMeasures.measures.BDOS;
import de.dailab.nsm.semanticDistanceMeasures.measures.ELKB;
import org.apache.log4j.Logger;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by faehndrich on 16.11.15.
 */
public class Rubenstein1965Test {

    static final Logger logger = Logger.getLogger(Rubenstein1965Test.class);
    Collection<WordSimilarityDataSet> datasets = new ArrayList<>(5);
    public Collection<DataExample> testSynonymPairs = new ArrayList<>();
    ELKB elkb = null;
    BDOS bdos = null;
    //Word2VecSemanticDistanceMeasure word2VecSemanticDistanceMeasure = null;
    MarkerPassingSemanticDistanceMeasure semanticDistanceMarkerPassing = null;
    ExecutorService threadPoolExecutor = Executors.newFixedThreadPool(1);//Executors.newCachedThreadPool(); //.newFixedThreadPool(12);//Runtime.getRuntime().availableProcessors());
    private double spearmanCorrelation;

    private double pearsonCorrelation;
    private MarkerPassingConfig markerPassingConfig = new MarkerPassingConfig();

    public static void main(String args[]) {
        System.out.println("Welcome to rubenstein1965Dataset Comparison Test");
        Rubenstein1965Test test = new Rubenstein1965Test();
        test.init();
        test.testSynonyms();
        //test.calculateCorrelationCoefficient();
    }

    public double getPearsonCorrelation() {
        return pearsonCorrelation;
    }

    public void setPearsonCorrelation(double pearsonCorrelation) {
        this.pearsonCorrelation = pearsonCorrelation;
    }

    public double getSpearmanCorrelation() {
        return spearmanCorrelation;
    }

    public void setSpearmanCorrelation(double spearmanCorrelation) {
        this.spearmanCorrelation = spearmanCorrelation;
    }

    public Collection<DataExample> getTestSynonymPairs() {
        return testSynonymPairs;
    }

    public void setTestSynonymPairs(Collection<DataExample> testSynonymPairs) {
        this.testSynonymPairs = testSynonymPairs;
    }

    public MarkerPassingSemanticDistanceMeasure getSemanticDistanceMarkerPassing() {
        return semanticDistanceMarkerPassing;
    }

    public void setSemanticDistanceMarkerPassing(MarkerPassingSemanticDistanceMeasure semanticDistanceMarkerPassing) {
        this.semanticDistanceMarkerPassing = semanticDistanceMarkerPassing;
    }

    public void init() {
        if (semanticDistanceMarkerPassing == null) {
            semanticDistanceMarkerPassing = new MarkerPassingSemanticDistanceMeasure();
        }
        //Load de.dailab.nsm.semanticDistanceMeasures.data sets
        //1
//        WordSim353DataSet wordSim353DataSet = new WordSim353DataSet();
//        datasets.add(wordSim353DataSet);
        //2
        Rubenstein1965Dataset rubenstein1965Dataset = new Rubenstein1965Dataset();
        datasets.add(rubenstein1965Dataset);
        //3
//        MENDataSet menDataSet = new MENDataSet();
//        datasets.add(menDataSet);
        //4
//        MtrukDataSet mtrukDataSet = new MtrukDataSet();
//        datasets.add(mtrukDataSet);
        //5
        //StanfordRareWordSimilarityDataset stanfordRareWordSimilarityDataset = new StanfordRareWordSimilarityDataset();
        //datasets.add(stanfordRareWordSimilarityDataset);

        for (WordSimilarityDataSet dataSet : datasets) {
            testSynonymPairs.addAll(dataSet.ReadExampleDataSet());
        }

    }

    public double testSynonyms() {
        // elkb = new ELKB();
        // bdos = new BDOS();
//        word2VecSemanticDistanceMeasure = new Word2VecSemanticDistanceMeasure();
        //List<Graph> wordGraphs = Collections.synchronizedList(new ArrayList());
        //final List<Graph> synonymGraphs = Collections.synchronizedList(new ArrayList());
        //List<Graph> commonGraphs = Collections.synchronizedList(new ArrayList());
        //wordGraphs= getWordGraphs(decompositionDepth, testSet);
        //synonymGraphs = getSynonymGraphs(decompositionDepth,testSet);
        System.out.println("Word" + ";" + "Synonym" + ";" + "Humans" + ";" + "MarkerPassing");
        double cumulativeResultError = 0.0;
        for (DataExample pair : testSynonymPairs) {

            Concept word = new Concept(((SynonymPair) pair).getWord(), WordType.UNKNOWN); //TODO: OK, for Rubenstein1965 but for other thest the POS is not fix
            Concept synonym = new Concept(((SynonymPair) pair).getSynonym(), WordType.UNKNOWN); //TODO: OK, for Rubenstein1965 but for other thest the POS is not fix
            long bevorTest = System.nanoTime();
//            double bdosResult = bdos.compareConcepts(word, synonym);
            long afterTest = System.nanoTime();
//            logger.info("BODS " + Long.toString(afterTest-bevorTest) + "ns to compare " + word.getLitheral() + " and " + synonym.getLitheral() + "to: " + bdosResult );
//            bevorTest = System.nanoTime();
//            double elkBresult = elkb.compareConcepts(word, synonym);
//             afterTest = System.nanoTime();
//            logger.info("ELDB took " + Long.toString(afterTest-bevorTest) + "ns to compare " + word.getLitheral() + " and " + synonym.getLitheral() + "to: " + elkBresult );
//            bevorTest = System.nanoTime();
//            double word2VecResult = word2VecSemanticDistanceMeasure.compareConcepts(word, synonym);
//            afterTest = System.nanoTime();
//            logger.info("W2V took " + Long.toString(afterTest-bevorTest) + "ns to compare " + word.getLitheral() + " and " + synonym.getLitheral() + "to: " + word2VecResult );
            bevorTest = System.nanoTime();
            double markerPassingResult = semanticDistanceMarkerPassing.compareConcepts(word, synonym);
            afterTest = System.nanoTime();
            logger.info("MP took " + Long.toString(afterTest - bevorTest) + "ns to compare " + word.getLitheral() + " and " + synonym.getLitheral() + "to: " + markerPassingResult);
            pair.setResult(markerPassingResult);
//            System.out.println(pair.getWord() + ";" + pair.getSynonym() + ";" + pair.getTrueResult() + ";" + bdosResult + ";" + elkBresult + ";" + word2VecResult + ";" + pair.getResult());
            System.out.println(((SynonymPair) pair).getWord() + ";" + ((SynonymPair) pair).getSynonym() + ";" + pair.getTrueResult() + ";" + pair.getResult());
            cumulativeResultError += (Math.abs(pair.getTrueResult() - pair.getResult()));
        }
        Evaluation.normalize((List<DataExample>) testSynonymPairs);
        System.out.println("SpearmanCorrelation: " + Evaluation.SpearmanCorrelation(testSynonymPairs));
        System.out.println("PearsonCorrelation: " + Evaluation.PearsonCorrelation(testSynonymPairs));
        return cumulativeResultError;
    }

    /**
     * Calculates Searman und Pearson correlation coefficient.
     *
     * @return the cumulative distance from the test data (human prediction of semantic similarity) to the
     * results of the algorithm.
     */
    public double calculateCorrelationCoefficient() {
        double cumulativeResultError = getCumulativeResultError();
        spearmanCorrelation = Evaluation.SpearmanCorrelation(testSynonymPairs);
        pearsonCorrelation = Evaluation.PearsonCorrelation(testSynonymPairs);
        System.out.println("SpearmanCorrelation: " + spearmanCorrelation);
        System.out.println("PearsonCorrelation: " + pearsonCorrelation);
        return cumulativeResultError;
    }

    /**
     * Test the given test collection with a marker passing algorithm and normalizes the results. The calculated
     * semantic distance is stored in the test data set {@see SynonymPair}.
     *
     * @return the cumulative distance from the test data (human prediction of semantic similarity) to the
     * results of the algorithm.
     */
    public double getCumulativeResultError() {
        double cumulativeResultError = 0.0;
        int counter = 0;
        for (DataExample pair : testSynonymPairs) {
            double markerPassingResult = semanticDistanceMarkerPassing.compareConcepts(new Concept(((SynonymPair) pair).getWord(), WordType.NN), new Concept(((SynonymPair) pair).getSynonym(), WordType.NN));
            pair.setResult(markerPassingResult);
            System.out.print('\r');
            System.out.print("Progress: " + ++counter + " of " + testSynonymPairs.size());
        }
        Evaluation.normalize(testSynonymPairs);
        for (DataExample pair : testSynonymPairs) {
            cumulativeResultError += Math.abs(pair.getTrueResult() - pair.getResult());
        }
        System.out.print('\r');
        return cumulativeResultError;
    }


    public double testSynonymsParalell() {
        //List<Graph> wordGraphs = Collections.synchronizedList(new ArrayList());
        //final List<Graph> synonymGraphs = Collections.synchronizedList(new ArrayList());
        //List<Graph> commonGraphs = Collections.synchronizedList(new ArrayList());
        //wordGraphs= getWordGraphs(decompositionDepth, testSet);
        //synonymGraphs = getSynonymGraphs(decompositionDepth,testSet);
        //System.out.println("Word" + ";" + "Synonym" + ";" + "Humans" + ";" + "BDOS" + ";" + "ELKB" + ";" + "Word2Vec" + ";" + "MarkerPassing");
        //The graph cache is not thread save. We want to disable it here.

        markerPassingConfig.setUseGraphCache(false);
        for (DataExample pair : testSynonymPairs) {
            //wordGraphs.add(getGraph(pair.getWord(), WordType.NN, decompositionDepth));
            //synonymGraphs.add(getGraph(pair.getSynonym(), WordType.NN, decompositionDepth));
            //Graph commonGraph = testDistance(pair.getWord(), WordType.NN, pair.getSynonym(), WordType.NN, decompositionDepth);
            threadPoolExecutor.submit(new Runnable() {
                SynonymPair pair;

                @Override
                public void run() {
                    //testDistance(this.word1, this.wordType1, this.word2, this.wordType2, this.decompositionDepth);
                    //spreadActivation(this.word1, this.wordType1, this.word2, this.wordType2, this.decompositionDepth);
                    double bdosResult = 0.0;//bdos.compareConcepts(new Concept(pair.getWord()), new Concept(pair.getSynonym()));
                    double elkBresult = 0.0;//elkb.compareConcepts(new Concept(pair.getWord()), new Concept(pair.getSynonym()));
                    double word2VecResult = 0.0;//word2VecSemanticDistanceMeasure.compareConcepts(new Concept(pair.getWord()), new Concept(pair.getSynonym()));
                    double markerPassingResult = semanticDistanceMarkerPassing.compareConcepts(new Concept(pair.getWord(), WordType.NN), new Concept(pair.getSynonym(), WordType.NN));
                    pair.setResult(markerPassingResult);

                }

                public Runnable init(SynonymPair pair) {
                    this.pair = pair;
                    return (this);
                }
            }.init((SynonymPair) pair));
        }
        threadPoolExecutor.shutdown();
        try {
            while (!threadPoolExecutor.awaitTermination(100000, TimeUnit.SECONDS)) {
                logger.info("Awaiting completion of threads.");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Evaluation.normalize(testSynonymPairs);
        spearmanCorrelation = Evaluation.SpearmanCorrelation(testSynonymPairs);
        pearsonCorrelation = Evaluation.PearsonCorrelation(testSynonymPairs);
        System.out.println("SpearmanCorrelation: " + spearmanCorrelation);
        System.out.println("PearsonCorrelation: " + pearsonCorrelation);

        double cumulativeResultError = 0.0;
        for (DataExample pair : testSynonymPairs) {
            cumulativeResultError += Math.abs(pair.getTrueResult() - pair.getResult());
        }
        //threadPoolExecutor.shutdown();
        return cumulativeResultError;
    }

}
