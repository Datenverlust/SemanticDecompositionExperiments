/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

import de.dailab.nsm.decomposition.Concept;
import de.dailab.nsm.decomposition.WordType;
import de.dailab.nsm.decomposition.graph.Evaluation;
import de.dailab.nsm.decomposition.graph.spreadingActivation.MarkerPassing.MarkerPassingConfig;
import de.dailab.nsm.decomposition.graph.spreadingActivation.MarkerPassing.ParameterLearner.MarkerPassingSemanticDistanceMeasure;
import de.dailab.nsm.semanticDistanceMeasures.DataExample;
import de.dailab.nsm.semanticDistanceMeasures.SimilarityPair;
import de.dailab.nsm.semanticDistanceMeasures.Word2VecCosineSimilarityMeasure;
import de.dailab.nsm.semanticDistanceMeasures.data.Rubenstein1965Dataset;
import de.dailab.nsm.semanticDistanceMeasures.data.WordSimilarityDataSet;
import de.dailab.nsm.semanticDistanceMeasures.measures.BDOS;
import de.dailab.nsm.semanticDistanceMeasures.measures.ELKB;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by faehndrich on 16.11.15.
 */
public class Rubenstein1965Test {
    private static final Logger logger = Logger.getLogger(Rubenstein1965Test.class);
    private Collection<WordSimilarityDataSet> datasets = new ArrayList<>(5);
    Collection<DataExample> testSimilarityPairs = new ArrayList<>();
    private MarkerPassingSemanticDistanceMeasure semanticDistanceMarkerPassing;
    private ExecutorService threadPoolExecutor = Executors.newFixedThreadPool(1);//Executors.newCachedThreadPool(); //.newFixedThreadPool(12);//Runtime.getRuntime().availableProcessors());
    private double spearmanCorrelation;
    private double pearsonCorrelation;
    private double cumulativeError;
    public Rubenstein1965Test() {
        semanticDistanceMarkerPassing = null;
    }


    double getCumulativeError() {
        return cumulativeError;
    }

    private void setCumulativeError(double cumulativeError) {
        this.cumulativeError = cumulativeError;
    }

    double getPearsonCorrelation() {
        return pearsonCorrelation;
    }

    public void setPearsonCorrelation(double pearsonCorrelation) {
        this.pearsonCorrelation = pearsonCorrelation;
    }

    double getSpearmanCorrelation() {
        return spearmanCorrelation;
    }

    public void setSpearmanCorrelation(double spearmanCorrelation) {
        this.spearmanCorrelation = spearmanCorrelation;
    }

    public Collection<DataExample> getTestSimilarityPairs() {
        return testSimilarityPairs;
    }

    public void setTestSimilarityPairs(Collection<DataExample> testSimilarityPairs) {
        this.testSimilarityPairs = testSimilarityPairs;
    }

    public MarkerPassingSemanticDistanceMeasure getSemanticDistanceMarkerPassing() {
        return semanticDistanceMarkerPassing;
    }

    public void setSemanticDistanceMarkerPassing(MarkerPassingSemanticDistanceMeasure semanticDistanceMarkerPassing) {
        this.semanticDistanceMarkerPassing = semanticDistanceMarkerPassing;
    }

    @Before
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
            testSimilarityPairs.addAll(dataSet.ReadExampleDataSet());
        }

    }

    @Test
    public void testELKB() {
        ELKB elkb = new ELKB();
        System.out.println("Word" + ";" + "Synonym" + ";" + "Humans" + ";" + "MarkerPassing");
        double cumulativeResultError = 0.0;
        for (DataExample pair : testSimilarityPairs) {

            Concept word = new Concept(((SimilarityPair) pair).getString1(), WordType.UNKNOWN); //TODO: OK, for Rubenstein1965 but for other thest the POS is not fix
            Concept synonym = new Concept(((SimilarityPair) pair).getString2(), WordType.UNKNOWN); //TODO: OK, for Rubenstein1965 but for other thest the POS is not fix
            long bevorTest = System.nanoTime();
            double elkBresult = elkb.compareConcepts(word, synonym);
            long afterTest = System.nanoTime();
            logger.info("ELKB took " + (afterTest - bevorTest) + "ns to compare " + word.getLitheral() + " and " + synonym.getLitheral() + "to: " + elkBresult);
            pair.setResult(elkBresult);
            cumulativeResultError += (Math.abs(pair.getTrueResult() - pair.getResult()));
        }
        Evaluation.normalize(testSimilarityPairs);
        this.pearsonCorrelation = Evaluation.PearsonCorrelation(testSimilarityPairs);
        this.spearmanCorrelation = Evaluation.SpearmanCorrelation(testSimilarityPairs);
        this.setCumulativeError(cumulativeResultError);
        System.out.println(this.getCumulativeResultError());
    }


    @Test
    public void testMP() {
        System.out.println("Welcome to rubenstein1965Dataset Comparison Test");
        System.out.println("Word" + ";" + "Synonym" + ";" + "Humans" + ";" + "MarkerPassing");
        double cumulativeResultError = 0.0;
        for (DataExample pair : testSimilarityPairs) {
            Concept word = new Concept(((SimilarityPair) pair).getString1(), WordType.UNKNOWN); //TODO: OK, for Rubenstein1965 but for other thest the POS is not fix
            Concept synonym = new Concept(((SimilarityPair) pair).getString2(), WordType.UNKNOWN); //TODO: OK, for Rubenstein1965 but for other thest the POS is not fix
            long bevorTest = System.nanoTime();
            double markerPassingResult = semanticDistanceMarkerPassing.compareConcepts(word, synonym);
            long afterTest = System.nanoTime();
            logger.info("MP took " + (afterTest - bevorTest) + "ns to compare " + word.getLitheral() + " and " + synonym.getLitheral() + "to: " + markerPassingResult);
            pair.setResult(markerPassingResult);
            cumulativeResultError += (Math.abs(pair.getTrueResult() - pair.getResult()));
        }
        Evaluation.normalize(testSimilarityPairs);
        this.pearsonCorrelation = Evaluation.PearsonCorrelation(testSimilarityPairs);
        this.spearmanCorrelation = Evaluation.SpearmanCorrelation(testSimilarityPairs);
        //System.out.println("SpearmanCorrelation: " + this.spearmanCorrelation);
        //System.out.println("PearsonCorrelation: " + this.pearsonCorrelation);
        this.setCumulativeError(cumulativeResultError);
        Assert.assertTrue(this.spearmanCorrelation > 0.63);
        Assert.assertTrue(this.pearsonCorrelation > 0.80);
    }


    @Test
    public void testBDOS() {
        BDOS bdos = new BDOS();
        double cumulativeResultError = 0.0;
        for (DataExample pair : testSimilarityPairs) {
            Concept word = new Concept(((SimilarityPair) pair).getString1(), WordType.UNKNOWN); //TODO: OK, for Rubenstein1965 but for other thest the POS is not fix
            Concept synonym = new Concept(((SimilarityPair) pair).getString2(), WordType.UNKNOWN); //TODO: OK, for Rubenstein1965 but for other thest the POS is not fix
            long bevorTest = System.nanoTime();
            double bdosResult = bdos.compareConcepts(word, synonym);
            long afterTest = System.nanoTime();

            logger.info("BDOS took " + (afterTest - bevorTest) + "ns to compare " + word.getLitheral() + " and " + synonym.getLitheral() + "to: " + bdosResult);
            pair.setResult(bdosResult);
            cumulativeResultError += (Math.abs(pair.getTrueResult() - pair.getResult()));
        }
        Evaluation.normalize(testSimilarityPairs);
        this.pearsonCorrelation = Evaluation.PearsonCorrelation(testSimilarityPairs);
        this.spearmanCorrelation = Evaluation.SpearmanCorrelation(testSimilarityPairs);
        this.setCumulativeError(cumulativeResultError);
        Assert.assertTrue(this.calculateCumulativeError() > 27);
    }


    @Test
    public void testW2V() throws Exception {
        Word2VecCosineSimilarityMeasure word2VecSemanticDistanceMeasure = new Word2VecCosineSimilarityMeasure();
        double cumulativeResultError = 0.0;
        for (DataExample pair : testSimilarityPairs) {
            Concept word = new Concept(((SimilarityPair) pair).getString1(), WordType.UNKNOWN); //TODO: OK, for Rubenstein1965 but for other thest the POS is not fix
            Concept synonym = new Concept(((SimilarityPair) pair).getString2(), WordType.UNKNOWN); //TODO: OK, for Rubenstein1965 but for other thest the POS is not fix
            double word2VecResult = 0.5;
            try {
                long bevorTest = System.nanoTime();
                word2VecResult = word2VecSemanticDistanceMeasure.findSim(word, synonym);
                long afterTest = System.nanoTime();
                logger.info("W2V took " + (afterTest - bevorTest) + "ns to compare " + word.getLitheral() + " and " + synonym.getLitheral() + "to: " + word2VecResult);

            } catch (NullPointerException e) {
                logger.trace(e.getMessage(), e);
                logger.info("The following concepts where out of vocabulary: " + ((SimilarityPair) pair).getString1() + " and " + ((SimilarityPair) pair).getString2());
            } finally {
                pair.setResult(word2VecResult);
                cumulativeResultError += (Math.abs(pair.getTrueResult() - pair.getResult()));
            }

        }
        Evaluation.normalize(testSimilarityPairs);
        this.pearsonCorrelation = Evaluation.PearsonCorrelation(testSimilarityPairs);
        this.spearmanCorrelation = Evaluation.SpearmanCorrelation(testSimilarityPairs);
        //System.out.println("SpearmanCorrelation: " + this.spearmanCorrelation);
        //System.out.println("PearsonCorrelation: " + this.pearsonCorrelation);
        this.setCumulativeError(cumulativeResultError);
        Assert.assertTrue(this.pearsonCorrelation > 0.45);
        Assert.assertTrue(this.spearmanCorrelation > 0.42);
        Assert.assertTrue(this.getCumulativeError() < 20);
    }


    /**
     * Calculates Searman und Pearson correlation coefficient.
     *
     * @return the cumulative distance from the test data (human prediction of semantic similarity) to the
     * results of the algorithm.
     */
    private double calculateCumulativeError() {
        double cumulativeResultError = getCumulativeResultError();
        return cumulativeResultError;
    }

    /**
     * Test the given test collection with a marker passing algorithm and normalizes the results. The calculated
     * semantic distance is stored in the test data set {@see SynonymPair}.
     *
     * @return the cumulative distance from the test data (human prediction of semantic similarity) to the
     * results of the algorithm.
     */
    private double getCumulativeResultError() {
        double cumulativeResultError = 0.0;
        int counter = 0;
        for (DataExample pair : testSimilarityPairs) {
            double markerPassingResult = semanticDistanceMarkerPassing.compareConcepts(new Concept(((SimilarityPair) pair).getString1(), WordType.NN), new Concept(((SimilarityPair) pair).getString2(), WordType.NN));
            pair.setResult(markerPassingResult);
            System.out.print('\r');
            System.out.print("Progress: " + ++counter + " of " + testSimilarityPairs.size());
        }
        Evaluation.normalize(testSimilarityPairs);
        for (DataExample pair : testSimilarityPairs) {
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

        MarkerPassingConfig.setUseGraphCache(false);
        for (DataExample pair : testSimilarityPairs) {
            //wordGraphs.add(getGraph(pair.getString1(), WordType.NN, decompositionDepth));
            //synonymGraphs.add(getGraph(pair.getString2(), WordType.NN, decompositionDepth));
            //Graph commonGraph = testDistance(pair.getString1(), WordType.NN, pair.getString2(), WordType.NN, decompositionDepth);
            threadPoolExecutor.submit(new Runnable() {
                SimilarityPair pair;

                @Override
                public void run() {
                    //testDistance(this.word1, this.wordType1, this.word2, this.wordType2, this.decompositionDepth);
                    //spreadActivation(this.word1, this.wordType1, this.word2, this.wordType2, this.decompositionDepth);
                    double bdosResult = 0.0;//bdos.compareConcepts(new Concept(pair.getString1()), new Concept(pair.getString2()));
                    double elkBresult = 0.0;//elkb.compareConcepts(new Concept(pair.getString1()), new Concept(pair.getString2()));
                    double word2VecResult = 0.0;//word2VecSemanticDistanceMeasure.compareConcepts(new Concept(pair.getString1()), new Concept(pair.getString2()));
                    double markerPassingResult = semanticDistanceMarkerPassing.compareConcepts(new Concept(pair.getString1(), WordType.NN), new Concept(pair.getString2(), WordType.NN));
                    pair.setResult(markerPassingResult);

                }

                Runnable init(SimilarityPair pair) {
                    this.pair = pair;
                    return (this);
                }
            }.init(((SimilarityPair) pair)));
        }
        threadPoolExecutor.shutdown();
        try {
            while (!threadPoolExecutor.awaitTermination(100000, TimeUnit.SECONDS)) {
                logger.info("Awaiting completion of threads.");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Evaluation.normalize(testSimilarityPairs);
        spearmanCorrelation = Evaluation.SpearmanCorrelation(testSimilarityPairs);
        pearsonCorrelation = Evaluation.PearsonCorrelation(testSimilarityPairs);
        System.out.println("SpearmanCorrelation: " + spearmanCorrelation);
        System.out.println("PearsonCorrelation: " + pearsonCorrelation);

        double cumulativeResultError = 0.0;
        for (DataExample pair : testSimilarityPairs) {
            cumulativeResultError += Math.abs(pair.getTrueResult() - pair.getResult());
        }
        //threadPoolExecutor.shutdown();
        return cumulativeResultError;
    }

}
