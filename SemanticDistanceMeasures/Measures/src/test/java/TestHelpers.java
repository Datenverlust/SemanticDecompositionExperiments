/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

import de.dailab.nsm.semanticDistanceMeasures.SimilarityPair;

import java.util.*;

/**
 * Created by faehndrich on 14.09.15.
 */
public class TestHelpers {
    //static Collection<SynonymPair> testSynonymPairs = new ArrayList<>();


    public static Collection<SimilarityPair> filterFar(Collection<SimilarityPair> testSimilarityPairs){
        for (Iterator iterator = testSimilarityPairs.iterator(); iterator.hasNext(); ) {
            SimilarityPair pair = (SimilarityPair) iterator.next();
            if ((pair.getDistance() >= 0.3)) {
                iterator.remove();
            }
        } return testSimilarityPairs;
    }

    public static void filterMiddle(Collection<SimilarityPair> testSimilarityPairs){
        for (Iterator iterator = testSimilarityPairs.iterator(); iterator.hasNext(); ) {
            SimilarityPair pair = (SimilarityPair) iterator.next();
            if ((pair.getDistance() < 0.3) || pair.getDistance()>= 0.6) {
                iterator.remove();
            }
        }
    }

    public static void filterNear(Collection<SimilarityPair> testSimilarityPairs){
        for (Iterator iterator = testSimilarityPairs.iterator(); iterator.hasNext(); ) {
            SimilarityPair pair = (SimilarityPair) iterator.next();
            if ((pair.getDistance() < 0.6)) {
                iterator.remove();
            }
        }
    }

    public static void calculateCorrelation(Collection<SimilarityPair> testSimilarityPairs){

    }

    public static void filterNulls (Collection<SimilarityPair> testSimilarityPairs){
        for (Iterator iterator = testSimilarityPairs.iterator(); iterator.hasNext(); ) {
            SimilarityPair pair = (SimilarityPair) iterator.next();
            if ((pair.getResult() == 0)) {
                iterator.remove();
            }
        }
    }

    // filter words that are not contained in the dictionary
    public static Collection<SimilarityPair> filterSynonymPairs(Collection<SimilarityPair> testSimilarityPairs) {

        List<String> wordsToFilter = new ArrayList<String>(
                Arrays.asList("media", "warning", "children", "planning", "earning", "fighting", "Maradona", "dragonfly", "rodent", "stocking", "rose", "racing", "swimming", "orchid"));

        for (Iterator iterator = testSimilarityPairs.iterator(); iterator.hasNext(); ) {
            SimilarityPair pair = (SimilarityPair) iterator.next();
            if (wordsToFilter.contains(pair.getString1()) || wordsToFilter.contains(pair.getString2())) {
                iterator.remove();
            }
        }return testSimilarityPairs;
    }
}
