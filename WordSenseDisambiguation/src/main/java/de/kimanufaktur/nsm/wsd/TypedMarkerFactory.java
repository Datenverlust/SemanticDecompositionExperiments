/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

package de.kimanufaktur.nsm.wsd;


import de.kimanufaktur.nsm.graph.spreadingActivation.MarkerPassing.TypedMarkerPassingConfig;
import org.uncommons.watchmaker.framework.CandidateFactory;
import org.uncommons.watchmaker.framework.factories.AbstractCandidateFactory;

import java.util.Random;

/**
 * Created by root on 27.12.15.
 */
public class TypedMarkerFactory extends AbstractCandidateFactory<TypedMarkerPassingConfig> implements CandidateFactory<TypedMarkerPassingConfig> {

    @Override
    public TypedMarkerPassingConfig generateRandomCandidate(Random random) {
        TypedMarkerPassingConfig config = new TypedMarkerPassingConfig();
        // Generate a rnd set up
        // thresholds
        config.terminationPulseCount = random.nextInt(100); // 0-99 pulse count
        config.threshold = random.nextDouble() * 5.d; // 0-5.d concept threhold
        config.synThreshold = random.nextDouble() * 5.d; // 0-5.d synonyms threshold
        config.hypoThreshold = random.nextDouble() * 5.d; // 0-5.d hyponym threshold
        config.hyperThreshold = random.nextDouble() * 5.d; // 0-5.d hypernym threshold
        config.antoThreshold = random.nextDouble() * 5.d; // 0-5.d antonym threshold
        config.defThreshold = random.nextDouble() * 5.d; // 0-5.d defenition treshold

        // link weights
        config.synWeight = (random.nextDouble() * 2.d) - 1.d; // -1.d;1.d synonym weight
        config.hyperWeight = (random.nextDouble() * 2.d) - 1.d; // -1.d;1.d hypernym weight
        config.hypoWeight = (random.nextDouble() * 2.d) - 1.d; // -1.d;1.d hyponym weight
        config.antoWeight = (random.nextDouble() * 2.d) - 1.d; // -1.d;1.d antonym weight
        config.defWeight = (random.nextDouble() * 2.d) - 1.d; // -1.d;1.d defenition weight

       return config;
    }
}
