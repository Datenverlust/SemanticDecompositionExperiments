/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

package de.dailab.nsm.semanticDistanceMeasures.tests;

import de.dailab.nsm.decomposition.Concept;
import de.dailab.nsm.semanticDistanceMeasures.DataExample;
import de.dailab.nsm.semanticDistanceMeasures.SynonymPair;
import de.dailab.nsm.semanticDistanceMeasures.data.*;
import de.dailab.nsm.semanticDistanceMeasures.measures.BDOS;
import de.dailab.nsm.semanticDistanceMeasures.measures.test.TestHelpers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Created by faehndrich on 03.09.15.
 */
public class BDOSTests {

    static Collection<WordSimilarityDataSet> datasets = new ArrayList<>(5);
    static Collection<DataExample> testSynonymPairs = new ArrayList<DataExample>();
    BDOS bdos = null;

    public static void main(String args[]) {
        System.out.println("welcome to BDOS Tests");
        BDOSTests test = new BDOSTests();
        test.init();
        test.comparisonTest();
        //averageFailureOnebyOne();
        //onePairTest();

    }

    public void init() {
        bdos = new BDOS();
        //Load de.dailab.nsm.semanticDistanceMeasures.data sets
        //1
        Rubenstein1965Dataset rubenstein1965Dataset = new Rubenstein1965Dataset();
        datasets.add(rubenstein1965Dataset);
        //2
//        MENDataSet menDataSet = new MENDataSet();
//        datasets.add(menDataSet);
//        //3
//        WordSim353DataSet wordSim353DataSet = new WordSim353DataSet();
//        datasets.add(wordSim353DataSet);
//        //4
//        MtrukDataSet mtrukDataSet = new MtrukDataSet();
//        datasets.add(mtrukDataSet);
//        //5
//        StanfordRareWordSimilarityDataset stanfordRareWordSimilarityDataset = new StanfordRareWordSimilarityDataset();
//        datasets.add(stanfordRareWordSimilarityDataset);
        for (WordSimilarityDataSet dataSet : datasets) {
            testSynonymPairs.addAll(dataSet.ReadExampleDataSet());
        }
    }



    public void comparisonTest() {
        double failure = 0;
        double totalFailure = 0;
        double averageFailure = 0;
        double i = 0;

        for (DataExample pair : testSynonymPairs) {
            Concept word = new Concept(((SynonymPair)pair).getWord());
            Concept synonym = new Concept(((SynonymPair)pair).getSynonym());
            pair.setResult(bdos.compareConcepts(word, synonym));
            System.out.println(pair.getResult());
            failure = Math.abs(pair.getTrueResult() - pair.getResult());
            totalFailure = totalFailure + failure;
            i++;
        }
        averageFailure = totalFailure / i;
        System.out.println("average Failure " + averageFailure);
    }

    public  void onePairTest() {

        Concept day = new Concept("gem");
        Concept night = new Concept("jewel");
        double result = bdos.compareConcepts(day, night);
        System.out.println("the result is " + result);

    }

    public void averageFailureOnebyOne() {
        System.out.println("average Failure one by one");
        comparisonTest();
    }

    public void averageFailureTest() {
        System.out.println("average Failure Test");
        int i = 0;
        for (Iterator iterator = datasets.iterator(); iterator.hasNext(); ) {
            i++;
            WordSimilarityDataSet dataSet = (WordSimilarityDataSet) iterator.next();
            testSynonymPairs.addAll(dataSet.ReadExampleDataSet());
            TestHelpers.filterFar(testSynonymPairs);
            //System.out.println("Testing Dataset number " + i);
            comparisonTest();
            testSynonymPairs = new ArrayList<de.dailab.nsm.semanticDistanceMeasures.DataExample>();
        }
        /*System.out.println("Failure for all Datasets");
        for (WordSimilarityDataSet dataSet : datasets) {
            testSynonymPairs.addAll(dataSet.ReadExampleDataSet());
        }
        comparisonTest();*/
        testSynonymPairs = new ArrayList<de.dailab.nsm.semanticDistanceMeasures.DataExample>();
    }

}
