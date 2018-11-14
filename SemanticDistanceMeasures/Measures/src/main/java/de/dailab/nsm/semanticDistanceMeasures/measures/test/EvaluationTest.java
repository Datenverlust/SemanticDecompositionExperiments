/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

package de.dailab.nsm.semanticDistanceMeasures.measures.test;


import de.dailab.nsm.decomposition.graph.Evaluation;
import de.dailab.nsm.semanticDistanceMeasures.DataExample;
import de.dailab.nsm.semanticDistanceMeasures.SynonymPair;
import de.dailab.nsm.semanticDistanceMeasures.data.WordSim353DataSet;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Evaluation Tester.
 *
 * @author faehndrich
 * @version 1.0
 * @since <pre>Apr 28, 2016</pre>
 */
public class EvaluationTest {

    @Before
    public void before() {
    }

    @After
    public void after() {
    }

    /**
     * Method: PearsonCorrelation(Collection<SynonymPair> experimantResult)
     */
    @Test
    public void testPearsonCorrelation() {
        List<SynonymPair> testpairs = createPearsonExampleDataCorrelated();
        double pearsonCorrelation = Evaluation.PearsonCorrelation(testpairs);
        double realPearson = (1);
        Assert.assertEquals(realPearson, pearsonCorrelation, 0.00000000001);

    }

    private List<SynonymPair> createPearsonExampleDataCorrelated() {
        List<SynonymPair> testpairs = new ArrayList<>(5);
        SynonymPair p1 = new SynonymPair("", "", 1);
        p1.setResult(0.11);
        testpairs.add(p1);
        SynonymPair p2 = new SynonymPair("", "", 2);
        p2.setResult(0.12);
        testpairs.add(p2);
        SynonymPair p3 = new SynonymPair("", "", 3);
        p3.setResult(0.13);
        testpairs.add(p3);
        SynonymPair p4 = new SynonymPair("", "", 5);
        p4.setResult(0.15);
        testpairs.add(p4);
        SynonymPair p5 = new SynonymPair("", "", 8);
        p5.setResult(0.18);
        testpairs.add(p5);
        return testpairs;
    }

    /**
     * Method: SpearmanCorrelation(Collection<SynonymPair> experimantResult)
     */
    @Test
    public void testSpearmanCorrelation() {
        List<SynonymPair> testpairs = CreateSpearmanExampleData();
        double spearmanCorrelation = Evaluation.SpearmanCorrelation(testpairs);
        double realsSpearson = (-29d / 165d);
        Assert.assertEquals(realsSpearson, spearmanCorrelation, 0.00000000001);
    }

    private List<SynonymPair> CreateSpearmanExampleData() {
        List<SynonymPair> testpairs = new ArrayList<>(10);
        SynonymPair p1 = new SynonymPair("", "", 86);
        p1.setResult(0);
        testpairs.add(p1);
        SynonymPair p2 = new SynonymPair("", "", 97);
        p2.setResult(20);
        testpairs.add(p2);
        SynonymPair p3 = new SynonymPair("", "", 99);
        p3.setResult(28);
        testpairs.add(p3);
        SynonymPair p4 = new SynonymPair("", "", 100);
        p4.setResult(27);
        testpairs.add(p4);
        SynonymPair p5 = new SynonymPair("", "", 101);
        p5.setResult(50);
        testpairs.add(p5);
        SynonymPair p6 = new SynonymPair("", "", 103);
        p6.setResult(29);
        testpairs.add(p6);
        SynonymPair p7 = new SynonymPair("", "", 106);
        p7.setResult(7);
        testpairs.add(p7);
        SynonymPair p8 = new SynonymPair("", "", 110);
        p8.setResult(17);
        testpairs.add(p8);
        SynonymPair p9 = new SynonymPair("", "", 112);
        p9.setResult(6);
        testpairs.add(p9);
        SynonymPair p10 = new SynonymPair("", "", 113);
        p10.setResult(12);
        testpairs.add(p10);
        return testpairs;
    }

    /**
     * Method: normalize(Collection<SynonymPair> experimantResult)
     */
    @Test
    public void testNormalize() {
        Random rand = new Random();
        WordSim353DataSet wordSim353DataSet = new WordSim353DataSet();
        List<DataExample> pairs = wordSim353DataSet.ReadExampleDataSet();
        for (DataExample pair : pairs) pair.setResult(rand.nextDouble());
        Evaluation.normalize(pairs);
        for (DataExample pair : pairs) {
            Assert.assertTrue(pair.getResult() <= 1);
        }
    }


    @Test
    public void averageFailureTestForFar(){

        Assert.assertTrue(ELKBTest.averageFailureTest() < 0.5);
        Assert.assertTrue(ELKBTest.averageFailureTestForFar() < 0.8);

        Assert.assertTrue(ELKBTest.averageFailureTestForMiddle()< 0.5);
        Assert.assertTrue(ELKBTest.averageFailureTestforNear()< 0.5);
    }


    @Test
    public void TestELKBRG65() {

        Double result = ELKBTest.testRG65();
        Assert.assertTrue(result <= 0.24);


    }


    @Test
    public void TestELKBMEN() {


        Double result = ELKBTest.testMEN();
        Assert.assertTrue(result <= 0.3);


    }

    @Test
    public void TestELKBMtruk() {

        Double result = ELKBTest.testMtruk();
        Assert.assertTrue(result <= 0.4);

    }
    @Test
    public void TestELKWordSim353() {

        Double result = ELKBTest.testWordSim353();
        Assert.assertTrue(result <= 0.3);

    }
    @Test
    public void TestELKBStanfordRareWord() {



        Double result = ELKBTest.testStanfordRareWordSimilarity();
        Assert.assertTrue(result <= 0.44);


    }



} 
