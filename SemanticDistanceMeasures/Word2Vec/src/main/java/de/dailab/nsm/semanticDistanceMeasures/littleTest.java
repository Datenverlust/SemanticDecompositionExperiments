/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

package de.dailab.nsm.semanticDistanceMeasures;


import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.word2vec.SynonymPair;

import java.io.File;
import java.util.*;

/**
 * Created by faehndrich on 09.09.15.
 */
public class littleTest {

    static Collection<SynonymPair> testSynonymPairs = new ArrayList<>();

    public static void main(String... aArgs) {


        try {
            WordVectors wordVectors = WordVectorSerializer.loadTxtVectors(new File("words.txt"));
            //Collection<String> lst = wordVectors.wordsNearest("cat", 10);
            //System.out.println("10 closest to cat:"+lst);
            Collection<SynonymPair> myPairs = SynonymPair.fillTestOnlyFarWords();
            //Collection<SynonymPair> myPairs = filterSynonymPairs(myPairs1);
            for (SynonymPair pair : myPairs) {
                String word = pair.getWord();
                String synonym = pair.getSynonym();
                pair.setResult(wordVectors.similarity(word, synonym));
                System.out.println(pair.getResult());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public static Collection<SynonymPair> filterSynonymPairs(Collection<SynonymPair> testSynonymPairs) {

        List<String> wordsToFilter = new ArrayList<String>(
                Arrays.asList("media", "warning", "children", "planning", "earning", "fighting", "Maradona", "dragonfly", "rodent", "stocking", "rose", "racing", "swimming", "orchid"));

        for (Iterator iterator = testSynonymPairs.iterator(); iterator.hasNext(); ) {
            SynonymPair pair = (SynonymPair) iterator.next();
            if (wordsToFilter.contains(pair.getWord()) || wordsToFilter.contains(pair.getSynonym())) {
                iterator.remove();
            }
        }
        return testSynonymPairs;
    }
}
