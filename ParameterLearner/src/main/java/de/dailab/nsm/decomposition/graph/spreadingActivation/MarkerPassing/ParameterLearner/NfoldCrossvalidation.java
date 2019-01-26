/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

package de.dailab.nsm.decomposition.graph.spreadingActivation.MarkerPassing.ParameterLearner;

import de.dailab.nsm.semanticDistanceMeasures.DataExample;
import de.dailab.nsm.semanticDistanceMeasures.data.DataSet;
import de.dailab.nsm.semanticDistanceMeasures.data.Rubenstein1965Dataset;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by faehndrich on 03.04.16.
 */
public class NfoldCrossvalidation {
    static List<DataExample> training = new ArrayList<>(65);
    static List<DataExample> test = new ArrayList<>(65);
    private static NfoldCrossvalidation instance;
    Rubenstein1965Dataset dataset = new Rubenstein1965Dataset();
    //    WordSim353DataSet dataset = new WordSim353DataSet();
    private Random rand = new Random();

    private NfoldCrossvalidation() {
    }

    public NfoldCrossvalidation(int folds, DataSet dataset) {
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
            DataExample change = training.get(index);
            test.add(change);
            training.remove(change);
        }
    }


    public static synchronized NfoldCrossvalidation getInstance(int folds, DataSet dataset) {
        if (instance == null) {
            instance = new NfoldCrossvalidation(folds, dataset);
        }
        return instance;
    }


    public List<DataExample> getTrainingPairs() {
        ArrayList<DataExample> clone = new ArrayList<>(training.size());
        for (DataExample pair : training) {
            clone.add((DataExample) pair.clone());
        }
        return clone;
    }
}
