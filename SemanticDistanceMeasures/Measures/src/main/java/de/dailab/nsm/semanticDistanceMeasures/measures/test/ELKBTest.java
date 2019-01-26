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
import de.dailab.nsm.semanticDistanceMeasures.SimilarityPair;
import de.dailab.nsm.semanticDistanceMeasures.data.*;
import de.dailab.nsm.semanticDistanceMeasures.measures.ELKB;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Created by Sabine on 09.07.2015.
 */
public class ELKBTest {
    static Collection<WordSimilarityDataSet> datasets = new ArrayList<>(5);
    static Collection<DataExample> testSimilarityPairs = new ArrayList<>();

    public static double averageFailureTest(){
        System.out.println("average Failure Test");
        init();
        int i = 0;
        double result = 0;
        for (Iterator iterator = datasets.iterator(); iterator.hasNext();) {
            i ++;
            WordSimilarityDataSet dataSet = (WordSimilarityDataSet) iterator.next();
            testSimilarityPairs.addAll(dataSet.ReadExampleDataSet());
            //testSynonymPairs = TestHelpers.filterSynonymPairs(testSynonymPairs);
            System.out.println("Testing Dataset number " + i);
            result = comparisonTest();
            testSimilarityPairs = new ArrayList<>();
        }
    return result;
    }


    public static double averageFailureTestForFar(){
        System.out.println("average Failure Test for near Synonyms");
        init();
        int i = 0;
        double result = 0;
        for (Iterator iterator = datasets.iterator(); iterator.hasNext();) {
            i ++;
            WordSimilarityDataSet dataSet = (WordSimilarityDataSet) iterator.next();
            testSimilarityPairs.addAll(dataSet.ReadExampleDataSet());
            testSimilarityPairs = TestHelpers.filterFar(testSimilarityPairs);
            //System.out.println("Testing Dataset number " + i);
            result = comparisonTest();
            testSimilarityPairs = new ArrayList<>();
        }

        testSimilarityPairs = new ArrayList<>();
        return result;
    }

    public static double averageFailureTestForMiddle(){
        System.out.println("average Failure Test for Middle Synonyms");
        init();
        int i = 0;
        double result = 0;
        for (Iterator iterator = datasets.iterator(); iterator.hasNext();) {
            i ++;
            WordSimilarityDataSet dataSet = (WordSimilarityDataSet) iterator.next();
            testSimilarityPairs.addAll(dataSet.ReadExampleDataSet());
            TestHelpers.filterMiddle(testSimilarityPairs);
            //System.out.println("Testing Dataset number " + i);
            result = comparisonTest();
            testSimilarityPairs = new ArrayList<>();
        }
        testSimilarityPairs = new ArrayList<>();
        return result;
    }

    public static double averageFailureTestforNear(){
        System.out.println("average Failure Test for Word that are no Synonyms");
        init();
        int i = 0;
        double result = 0;
        for (Iterator iterator = datasets.iterator(); iterator.hasNext();) {
            i ++;
            WordSimilarityDataSet dataSet = (WordSimilarityDataSet) iterator.next();
            testSimilarityPairs.addAll(dataSet.ReadExampleDataSet());
            TestHelpers.filterNear(testSimilarityPairs);
            System.out.println("Testing Dataset number " + i);
            comparisonTest();
            testSimilarityPairs = new ArrayList<>();
        }
        //System.out.println("Failure for all Datasets");
        for (WordSimilarityDataSet dataSet : datasets) {
            testSimilarityPairs.addAll(dataSet.ReadExampleDataSet());
        }
        result = comparisonTest();
        testSimilarityPairs = new ArrayList<>();
        return result;
    }


    public static void init() {
        //Load de.dailab.nsm.semanticDistanceMeasures.data sets
        //1
        WordSim353DataSet wordSim353DataSet = new WordSim353DataSet();
        datasets.add(wordSim353DataSet);
        //2
        Rubenstein1965Dataset rubenstein1965Dataset = new Rubenstein1965Dataset();
        datasets.add(rubenstein1965Dataset);
        //3
        MENDataSet menDataSet = new MENDataSet();
        datasets.add(menDataSet);
        //4
        MtrukDataSet mtrukDataSet = new MtrukDataSet();
        datasets.add(mtrukDataSet);
        //5
        StanfordRareWordSimilarityDataset stanfordRareWordSimilarityDataset = new StanfordRareWordSimilarityDataset();
        datasets.add(stanfordRareWordSimilarityDataset);

        /*for (WordSimilarityDataSet dataSet : datasets) {
            testSynonymPairs.addAll(dataSet.ReadExampleDataSet());
        }*/

    }

    /**
     * Test woedSim353 DataSet
     * @return average error
     */
    public static double testWordSim353(){
        init();
        int i = 0;
        double result = 0;
        WordSim353DataSet dataSet = new WordSim353DataSet();
            testSimilarityPairs.addAll(dataSet.ReadExampleDataSet());
            result = comparisonTest();
            testSimilarityPairs = new ArrayList<>();
        return result;
    }


    /**
     * Test RG65 DataSet
     * @return average error
     */
    public static double testRG65(){
        init();
        int i = 0;
        double result = 0;
        Rubenstein1965Dataset dataSet = new Rubenstein1965Dataset();
        testSimilarityPairs.addAll(dataSet.ReadExampleDataSet());
        result = comparisonTest();
        testSimilarityPairs = new ArrayList<>();
        return result;
    }


    /**
     * Test MEN DataSet
     * @return average error
     */
    public static double testMEN(){
        init();
        int i = 0;
        double result = 0;
        MENDataSet dataSet = new MENDataSet();
        testSimilarityPairs.addAll(dataSet.ReadExampleDataSet());
        result = comparisonTest();
        testSimilarityPairs = new ArrayList<>();
        return result;
    }
    /**
     * Test Mtruk DataSet
     * @return average error
     */
    public static double testMtruk(){
        init();
        int i = 0;
        double result = 0;
        MtrukDataSet dataSet = new MtrukDataSet();
        testSimilarityPairs.addAll(dataSet.ReadExampleDataSet());
        result = comparisonTest();
        testSimilarityPairs = new ArrayList<>();
        return result;
    }
    /**
     * Test StanfordRareWordSimilarity DataSet
     * @return average error
     */
    public static double testStanfordRareWordSimilarity(){
        init();
        int i = 0;
        double result = 0;
        StanfordRareWordSimilarityDataset dataSet = new StanfordRareWordSimilarityDataset();
        testSimilarityPairs.addAll(dataSet.ReadExampleDataSet());
        result = comparisonTest();
        testSimilarityPairs = new ArrayList<>();
        return result;
    }



    public static double comparisonTest() {
        ELKB conceptCompare = new ELKB();
        conceptCompare.init();
        /*if (testSynonymPairs.size() < 1) {
            init();
        }*/
        double failure = 0;
        double totalFailure = 0;
        double averageFailure = 0;
        double i = 0;
        for (DataExample pair : testSimilarityPairs) {

            Concept word = new Concept(((SimilarityPair) pair).getString1());
            Concept synonym = new Concept(((SimilarityPair) pair).getString2());
            pair.setResult(conceptCompare.compareConcepts(word, synonym));
            failure = Math.abs(pair.getResult() -pair.getTrueResult());
            //System.out.println(pair.getDistance());
            //System.out.println(pair.getResult());
            //System.out.println(i);
            //System.out.print("\""+ pair.getString1() + "\"" + ","+ "\""+ pair.getString2()+"\""+",");
            //System.out.println(pair.getString1() + ";" + pair.getString2() + ";" + pair.getResult() + ";" + pair.getDistance());
            //System.out.println("failure " + failure);
            totalFailure = totalFailure + failure;
            i ++;
        }
        //System.out.println("total Failure " + totalFailure);
        averageFailure = totalFailure/i;
        //System.out.println("average Failure " + averageFailure);
        /*for (de.dailab.nsm.semanticDistanceMeasures.SynonymPair pair : testSynonymPairs) {
            System.out.println(pair.getString1() + ";" + pair.getString2() + ";" + pair.getResult() + ";" + pair.getDistance());
        }*/
    return averageFailure;
    }
}
