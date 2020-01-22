/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

package de.kimanufaktur.nsm.decomposition.graph.spreadingActivation.MarkerPassing.ParameterLearner;

import de.kimanufaktur.nsm.semanticDistanceMeasures.DataExample;

/**
 * Created by faehndrich on 25.11.15.
 */
public abstract class TestRunnable implements Runnable {
    DataExample pair = null;

    public TestRunnable() {


    }

    public TestRunnable(DataExample pair) {
        this.pair = pair;
    }

    public DataExample getPair() {
        return pair;
    }

    public void setPair(DataExample pair) {
        this.pair = pair;
    }
}
