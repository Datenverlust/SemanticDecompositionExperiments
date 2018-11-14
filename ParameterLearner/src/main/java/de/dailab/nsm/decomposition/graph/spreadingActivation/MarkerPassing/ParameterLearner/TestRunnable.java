/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

package de.dailab.nsm.decomposition.graph.spreadingActivation.MarkerPassing.ParameterLearner;

import de.dailab.nsm.semanticDistanceMeasures.DataExample;
import de.dailab.nsm.semanticDistanceMeasures.SynonymPair;
import junit.framework.Test;

/**
 * Created by faehndrich on 25.11.15.
 */
public abstract class TestRunnable implements Runnable {
    DataExample pair = null;

    public TestRunnable() {


    }

    public DataExample getPair() {
        return pair;
    }
    public void setPair(DataExample pair) {
        this.pair = pair;
    }

    public TestRunnable(DataExample pair){
        this.pair = pair;
    }
}
