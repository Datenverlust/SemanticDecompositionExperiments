/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

package de.dailab.nsm.semanticDistanceMeasures.measures.test;

import de.dailab.nsm.semanticDistanceMeasures.DataExample;
import de.dailab.nsm.semanticDistanceMeasures.SynonymPair;

import java.util.*;

/**
 * Created by faehndrich on 14.09.15.
 */
public class TestHelpers {
    //static Collection<SynonymPair> testSynonymPairs = new ArrayList<>();


    public static Collection<DataExample> filterFar(Collection<DataExample> testSimilarityPairs){
        for (Iterator iterator = testSimilarityPairs.iterator(); iterator.hasNext(); ) {
            DataExample pair = (DataExample) iterator.next();
            if ((pair.getResult() >= 0.3)) {
                iterator.remove();
            }
        } return testSimilarityPairs;
    }

    public static void filterMiddle(Collection<DataExample> testSimilarityPairs){
        for (Iterator iterator = testSimilarityPairs.iterator(); iterator.hasNext(); ) {
            DataExample pair = (DataExample) iterator.next();
            if ((pair.getResult() < 0.3) || pair.getResult()>= 0.6) {
                iterator.remove();
            }
        }
    }

    public static void filterNear(Collection<DataExample> testSimilarityPairs){
        for (Iterator iterator = testSimilarityPairs.iterator(); iterator.hasNext(); ) {
            DataExample pair = (DataExample) iterator.next();
            if ((pair.getResult() < 0.6)) {
                iterator.remove();
            }
        }
    }

    public static void calculateCorrelation(Collection<DataExample> testSimilarityPairs){

    }

    public static void filterNulls (Collection<DataExample> testSimilarityPairs){
        for (Iterator iterator = testSimilarityPairs.iterator(); iterator.hasNext(); ) {
            DataExample pair = (DataExample) iterator.next();
            if ((pair.getResult() == 0)) {
                iterator.remove();
            }
        }
    }

    // filter words that are not contained in the dictionary
    public static Collection<DataExample> filterSynonymPairs(Collection<DataExample> testSimilarityPairs) {

        List<String> wordsToFilter = new ArrayList<String>(
                Arrays.asList("media", "warning", "children", "planning", "earning", "fighting", "Maradona", "dragonfly", "rodent", "stocking", "rose", "racing", "swimming", "orchid"));

        for (Iterator iterator = testSimilarityPairs.iterator(); iterator.hasNext(); ) {
            DataExample pair = (DataExample) iterator.next();
            if (wordsToFilter.contains(((SynonymPair)pair).getWord()) || wordsToFilter.contains(((SynonymPair)pair).getSynonym())) {
                iterator.remove();
            }
        }return testSimilarityPairs;
    }
}
