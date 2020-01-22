/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

import de.kimanufaktur.nsm.decomposition.Concept;
import de.kimanufaktur.nsm.semanticDistanceMeasures.DataExample;
import de.kimanufaktur.nsm.semanticDistanceMeasures.SimilarityPair;
import de.kimanufaktur.nsm.semanticDistanceMeasures.data.Rubenstein1965Dataset;
import de.kimanufaktur.nsm.semanticDistanceMeasures.data.WordSimilarityDataSet;
import de.kimanufaktur.nsm.semanticDistanceMeasures.measures.BDOS;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Created by faehndrich on 03.09.15.
 */
public class BDOSTests {

    static Collection<WordSimilarityDataSet> datasets = new ArrayList<>(5);
    static Collection<DataExample> testSimilarityPairs = new ArrayList<DataExample>();
    BDOS bdos = null;

    public static void main(String[] args) {
        System.out.println("welcome to BDOS Tests");
        BDOSTests test = new BDOSTests();
        test.init();
        test.comparisonTest();
        //averageFailureOnebyOne();
        //onePairTest();

    }

    @Before
    public void init() {
        bdos = new BDOS();
        //Load de.kimanufaktur.nsm.semanticDistanceMeasures.data sets
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
            testSimilarityPairs.addAll(dataSet.ReadExampleDataSet());
        }
    }


    @Test
    public void comparisonTest() {
        double failure = 0;
        double totalFailure = 0;
        double averageFailure = 0;
        double i = 0;

        for (DataExample pair : testSimilarityPairs) {
            Concept word = new Concept(((SimilarityPair) pair).getString1());
            Concept synonym = new Concept(((SimilarityPair) pair).getString2());
            pair.setResult(bdos.compareConcepts(word, synonym));
            System.out.println(pair.getResult());
            failure = Math.abs(pair.getTrueResult() - pair.getResult());
            totalFailure = totalFailure + failure;
            i++;
        }
        averageFailure = totalFailure / i;
        System.out.println("average Failure " + averageFailure);
        assert(averageFailure<1);
    }

    @Test
    public  void onePairTest() {

        Concept day = new Concept("gem");
        Concept night = new Concept("jewel");
        double result = bdos.compareConcepts(day, night);
        System.out.println("the result is " + result);

    }


    @Test
    public void averageFailureTest() {
        System.out.println("average Failure Test");
        int i = 0;
        for (Iterator iterator = datasets.iterator(); iterator.hasNext(); ) {
            i++;
            WordSimilarityDataSet dataSet = (WordSimilarityDataSet) iterator.next();
            testSimilarityPairs.addAll(dataSet.ReadExampleDataSet());
            TestHelpers.filterFar(testSimilarityPairs);
            //System.out.println("Testing Dataset number " + i);
            comparisonTest();
            testSimilarityPairs = new ArrayList<de.kimanufaktur.nsm.semanticDistanceMeasures.DataExample>();
        }
        /*System.out.println("Failure for all Datasets");
        for (WordSimilarityDataSet dataSet : datasets) {
            testSynonymPairs.addAll(dataSet.ReadExampleDataSet());
        }
        comparisonTest();*/
        testSimilarityPairs = new ArrayList<de.kimanufaktur.nsm.semanticDistanceMeasures.DataExample>();
    }

}
