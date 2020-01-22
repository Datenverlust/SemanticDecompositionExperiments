/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

import de.kimanufaktur.nsm.decomposition.graph.Evaluation;
import de.kimanufaktur.nsm.semanticDistanceMeasures.DataExample;
import de.kimanufaktur.nsm.semanticDistanceMeasures.SimilarityPair;
import de.kimanufaktur.nsm.semanticDistanceMeasures.data.WordSim353DataSet;
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


    private List<SimilarityPair> createPearsonExampleDataCorrelated() {
        List<SimilarityPair> testpairs = new ArrayList<>(5);
        SimilarityPair p1 = new SimilarityPair("", "", 1);
        p1.setResult(0.11);
        testpairs.add(p1);
        SimilarityPair p2 = new SimilarityPair("", "", 2);
        p2.setResult(0.12);
        testpairs.add(p2);
        SimilarityPair p3 = new SimilarityPair("", "", 3);
        p3.setResult(0.13);
        testpairs.add(p3);
        SimilarityPair p4 = new SimilarityPair("", "", 5);
        p4.setResult(0.15);
        testpairs.add(p4);
        SimilarityPair p5 = new SimilarityPair("", "", 8);
        p5.setResult(0.18);
        testpairs.add(p5);
        return testpairs;
    }

    /**
     * Method: SpearmanCorrelation(Collection<SynonymPair> experimantResult)
     */
    @Test
    public void testSpearmanCorrelation() {
        List<SimilarityPair> testpairs = CreateSpearmanExampleData();
        double spearmanCorrelation = Evaluation.SpearmanCorrelation(testpairs);
        double realsSpearson = (-29d / 165d);
        Assert.assertEquals(realsSpearson, spearmanCorrelation, 0.00000000001);
    }

    private List<SimilarityPair> CreateSpearmanExampleData() {
        List<SimilarityPair> testpairs = new ArrayList<>(10);
        SimilarityPair p1 = new SimilarityPair("", "", 86);
        p1.setResult(0);
        testpairs.add(p1);
        SimilarityPair p2 = new SimilarityPair("", "", 97);
        p2.setResult(20);
        testpairs.add(p2);
        SimilarityPair p3 = new SimilarityPair("", "", 99);
        p3.setResult(28);
        testpairs.add(p3);
        SimilarityPair p4 = new SimilarityPair("", "", 100);
        p4.setResult(27);
        testpairs.add(p4);
        SimilarityPair p5 = new SimilarityPair("", "", 101);
        p5.setResult(50);
        testpairs.add(p5);
        SimilarityPair p6 = new SimilarityPair("", "", 103);
        p6.setResult(29);
        testpairs.add(p6);
        SimilarityPair p7 = new SimilarityPair("", "", 106);
        p7.setResult(7);
        testpairs.add(p7);
        SimilarityPair p8 = new SimilarityPair("", "", 110);
        p8.setResult(17);
        testpairs.add(p8);
        SimilarityPair p9 = new SimilarityPair("", "", 112);
        p9.setResult(6);
        testpairs.add(p9);
        SimilarityPair p10 = new SimilarityPair("", "", 113);
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
    public void averageFailureTestForFar() {

        Assert.assertTrue(ELKTest.averageFailureTest() < 0.5);
        Assert.assertTrue(ELKTest.averageFailureTestForFar() < 0.8);

        Assert.assertTrue(ELKTest.averageFailureTestForMiddle() < 0.5);
        Assert.assertTrue(ELKTest.averageFailureTestforNear() < 0.5);
    }

    @Test
    public void TestELKBRG65() {
        Double result = ELKTest.testRG65();
        Assert.assertTrue(result <= 0.24);
    }

    @Test
    public void TestELKBMEN() {
        Double result = ELKTest.testMEN();
        Assert.assertTrue(result <= 0.3);
    }

    @Test
    public void TestELKBMtruk() {
        Double result = ELKTest.testMtruk();
        Assert.assertTrue(result <= 0.4);
    }

    @Test
    public void TestELKWordSim353() {
        Double result = ELKTest.testWordSim353();
        Assert.assertTrue(result <= 0.3);
    }

    @Test
    public void TestELKBStanfordRareWord() {
        Double result = ELKTest.testStanfordRareWordSimilarity();
        Assert.assertTrue(result <= 0.44);
    }


}
