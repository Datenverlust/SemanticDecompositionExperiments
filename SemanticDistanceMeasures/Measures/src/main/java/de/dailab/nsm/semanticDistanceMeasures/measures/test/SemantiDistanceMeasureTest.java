/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

package de.dailab.nsm.semanticDistanceMeasures.measures.test;


import de.dailab.nsm.semanticDistanceMeasures.data.Rubenstein1965Dataset;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class SemantiDistanceMeasureTest {

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
    public void testRG65() {

        Rubenstein1965Test markerPassingTest = new Rubenstein1965Test();

        //init the semantic distance measure
        markerPassingTest.init();
        //remove all the loaded data sets
        markerPassingTest.testSynonymPairs.clear();
        //Add the data set we want to test
        Rubenstein1965Dataset dataset = new Rubenstein1965Dataset();
        markerPassingTest.testSynonymPairs.addAll(dataset.ReadExampleDataSet());
        //Run the test
        markerPassingTest.testSynonyms();
        Assert.assertTrue(markerPassingTest.getCumulativeResultError() < 31);
        Assert.assertTrue(markerPassingTest.getPearsonCorrelation() > 0.78);
        Assert.assertTrue(markerPassingTest.getSpearmanCorrelation() > 0.62);
    }


}
