/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

package de.kimanufaktur.nsm.decomposition.graph.spreadingActivation.MarkerPassing.ParameterLearner.test;

import de.kimanufaktur.nsm.decomposition.graph.spreadingActivation.MarkerPassing.ParameterLearner.LearnMPParameters;
import de.kimanufaktur.nsm.decomposition.graph.spreadingActivation.MarkerPassing.ParameterLearner.ParameterLearner.MarkerPassingConfigurationEvaluator;

/**
 * Created by faehndrich on 27.11.15.
 */
public class learningMarkerPassingParameters {


    //ExecutorService threadPoolExecutor = Executors.newFixedThreadPool(8);//Runtime.getRuntime().availableProcessors());
    public static void main(String args[]) {
        System.out.println("Welcome to Marker Passing learning Test");

        //LearnMPParameters learner = new LearnMPParameters();
        String[] arg = new String[1];
        arg[0] = MarkerPassingConfigurationEvaluator.class.toString();
        LearnMPParameters.main(arg);
    }
}
