/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

import de.dailab.nsm.semanticDistanceMeasures.DataExample;
import de.dailab.nsm.semanticDistanceMeasures.SimilarityPair;
import de.dailab.nsm.semanticDistanceMeasures.data.Rubenstein1965Dataset;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NfoldCrossValidation {
        private static NfoldCrossValidation instance;
        static List<DataExample> training = new ArrayList<>(65);
        static List<DataExample> test = new ArrayList<>(65);
        Rubenstein1965Dataset dataset = new Rubenstein1965Dataset();
        //    WordSim353DataSet dataset = new WordSim353DataSet();
        private Random rand = new Random();

        private NfoldCrossValidation() {
        }

        public NfoldCrossValidation(int folds) {
            this();
            training = dataset.ReadExampleDataSet();
            if (folds > training.size()) {
                folds = training.size();
            }
            if (folds < 1) {
                folds = 1;
            }
            int subsamplesCount = Math.round(training.size() / folds);
            for (int i = 0; i < subsamplesCount; i++) {
                int index = rand.nextInt(training.size());
                SimilarityPair change = (SimilarityPair) training.get(index);
                test.add(change);
                training.remove(change);
            }
        }


        public static synchronized NfoldCrossValidation getInstance(int folds) {
            if(instance == null){
                instance = new NfoldCrossValidation(folds);
            }
            return instance;
        }


    public List<SimilarityPair> getTrainingPairs() {
        ArrayList<SimilarityPair> clone = new ArrayList<>(training.size());

            for (DataExample pair:training) {
                clone.add((SimilarityPair) pair.clone());
            }
            return clone;
        }
    }


