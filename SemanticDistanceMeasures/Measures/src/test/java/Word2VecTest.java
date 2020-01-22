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
import de.kimanufaktur.nsm.semanticDistanceMeasures.Word2VecCosineSimilarityMeasure;
import de.kimanufaktur.nsm.semanticDistanceMeasures.data.WordSim353DataSet;
import de.kimanufaktur.nsm.semanticDistanceMeasures.data.WordSimilarityDataSet;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by faehndrich on 15.09.15.
 */
public class Word2VecTest {

    static Collection<WordSimilarityDataSet> datasets = new ArrayList<>(5);
    static Collection<DataExample> testSimilarityPairs = new ArrayList<>();
    Word2VecCosineSimilarityMeasure word2VecCosineSimilarityMeasure = null;

    public static void main(String[] args) {
        System.out.println("Welcome to Word2Vec Comparison Test");
        Word2VecTest test = new Word2VecTest();
        test.init();
        test.VecTest();
    }

public Word2VecTest(){
    this.init();
}

    @Before
    public void init() {
        //Load de.kimanufaktur.nsm.semanticDistanceMeasures.data sets
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
            testSimilarityPairs.addAll(dataSet.ReadExampleDataSet());
        }
        word2VecCosineSimilarityMeasure = new Word2VecCosineSimilarityMeasure();
    }

    @Test
    public void VecTest() {

        double failure = 0;
        double totalFailure = 0;
        double averageFailure = 0;
        double i = 0;
        for (DataExample pair : testSimilarityPairs) {

            Concept word = new Concept(((SimilarityPair) pair).getString1());
            Concept synonym = new Concept(((SimilarityPair) pair).getString2());
            try {
                pair.setResult(word2VecCosineSimilarityMeasure.findSim(word, synonym));
            } catch (Exception e) {
                e.printStackTrace();
            }
            failure = Math.abs(pair.getResult() - pair.getTrueResult());
            System.out.println(((SimilarityPair) pair).getString1() + ";" + ((SimilarityPair) pair).getString2() + ";" + pair.getResult() + ";" + pair.getTrueResult());
            totalFailure = totalFailure + failure;
            i++;
        }
        System.out.println("total Failure " + totalFailure);
        averageFailure = totalFailure / i;
        System.out.println("average Failure " + averageFailure);
        /*for (de.kimanufaktur.nsm.semanticDistanceMeasures.SynonymPair pair : testSynonymPairs) {
            System.out.println(pair.getString1() + ";" + pair.getString2() + ";" + pair.getResult() + ";" + pair.getDistance());
        }*/

    }
}