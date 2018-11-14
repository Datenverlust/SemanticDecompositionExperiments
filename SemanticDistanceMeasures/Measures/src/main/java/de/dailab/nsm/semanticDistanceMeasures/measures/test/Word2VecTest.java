/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

package de.dailab.nsm.semanticDistanceMeasures.measures.test;

import de.dailab.nsm.decomposition.Concept;
import de.dailab.nsm.semanticDistanceMeasures.DataExample;
import de.dailab.nsm.semanticDistanceMeasures.SynonymPair;
import de.dailab.nsm.semanticDistanceMeasures.Word2VecCosineSimilarityMeasure;
import de.dailab.nsm.semanticDistanceMeasures.data.WordSim353DataSet;
import de.dailab.nsm.semanticDistanceMeasures.data.WordSimilarityDataSet;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by faehndrich on 15.09.15.
 */
public class Word2VecTest {

    static Collection<WordSimilarityDataSet> datasets = new ArrayList<>(5);
    static Collection<DataExample> testSynonymPairs = new ArrayList<>();
    Word2VecCosineSimilarityMeasure word2VecCosineSimilarityMeasure = null;

    public static void main(String args[]) {
        System.out.println("Welcome to Word2Vec Comparison Test");
        Word2VecTest test = new Word2VecTest();
        test.init();
        test.VecTest();
    }

    public Word2VecTest() {
        this.init();
    }

    public void init() {
        //Load de.dailab.nsm.semanticDistanceMeasures.data sets
        //1
        WordSim353DataSet wordSim353DataSet = new WordSim353DataSet();
        datasets.add(wordSim353DataSet);
        //2
//        Rubenstein1965Dataset rubenstein1965Dataset = new Rubenstein1965Dataset();
//        datasets.add(rubenstein1965Dataset);
//        //3
//        MENDataSet menDataSet = new MENDataSet();
//        datasets.add(menDataSet);
//        //4
//        MtrukDataSet mtrukDataSet = new MtrukDataSet();
//        datasets.add(mtrukDataSet);
//        //5
//        StanfordRareWordSimilarityDataset stanfordRareWordSimilarityDataset = new StanfordRareWordSimilarityDataset();
//        datasets.add(stanfordRareWordSimilarityDataset);

        for (WordSimilarityDataSet dataSet : datasets) {
            testSynonymPairs.addAll(dataSet.ReadExampleDataSet());
        }
        word2VecCosineSimilarityMeasure = new Word2VecCosineSimilarityMeasure();
    }

    public void VecTest() {

        double failure = 0;
        double totalFailure = 0;
        double averageFailure = 0;
        double i = 0;
        for (DataExample pair : testSynonymPairs) {

            Concept word = new Concept(((SynonymPair) pair).getWord());
            Concept synonym = new Concept(((SynonymPair) pair).getSynonym());
            try {
                pair.setResult(word2VecCosineSimilarityMeasure.findSim(word, synonym));
            } catch (Exception e) {
                e.printStackTrace();
            }
            failure = Math.abs(pair.getResult() - pair.getTrueResult());
            System.out.println(((SynonymPair) pair).getWord() + ";" + ((SynonymPair) pair).getSynonym() + ";" + pair.getResult() + ";" + pair.getResult());
            totalFailure = totalFailure + failure;
            i++;
        }
        System.out.println("total Failure " + totalFailure);
        averageFailure = totalFailure / i;
        System.out.println("average Failure " + averageFailure);
        /*for (de.dailab.nsm.semanticDistanceMeasures.SynonymPair pair : testSynonymPairs) {
            System.out.println(pair.getString1() + ";" + pair.getString2() + ";" + pair.getResult() + ";" + pair.getDistance());
        }*/

    }
}