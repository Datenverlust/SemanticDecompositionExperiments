/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

package de.kimanufaktur.nsm.wsd;

import de.kimanufaktur.nsm.graph.spreadingActivation.MarkerPassing.TypedMarkerPassingConfig;
import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.framework.operators.AbstractCrossover;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by root on 27.12.15.
 */
public class TypedMarkerConfigCrossover extends AbstractCrossover<TypedMarkerPassingConfig> {

    public TypedMarkerConfigCrossover() {
        super(1);
    }

    public TypedMarkerConfigCrossover(int crossoverPoints) {
        super(crossoverPoints);
    }

    public TypedMarkerConfigCrossover(int crossoverPoints, Probability crossoverProbability) {
        super(crossoverPoints, crossoverProbability);
    }


    @Override
    protected List<TypedMarkerPassingConfig> mate(TypedMarkerPassingConfig parent1, TypedMarkerPassingConfig parent2, int numberOfCrossoverPoints, Random random) {
        TypedMarkerPassingConfig offspring1 = new TypedMarkerPassingConfig(parent1);
        TypedMarkerPassingConfig offspring2 = new TypedMarkerPassingConfig(parent2);
        for(int i=0;i<numberOfCrossoverPoints;i++) {
            int crossOverIndex = (1 + random.nextInt(TypedMarkerPassingConfig.getNumberOfParameter() - 1));
            int itmp = offspring1.terminationPulseCount;
            offspring1.terminationPulseCount = offspring2.terminationPulseCount;
            offspring2.terminationPulseCount = itmp;
            if(crossOverIndex>1) {
                for(int index=1;index<crossOverIndex;index++) {
                    offspring1.swap(offspring2.swap(offspring1.get(index),index),index);
                }
            }

        }
        List<TypedMarkerPassingConfig> result = new ArrayList<>(2);
        result.add(offspring1);
        result.add(offspring2);
        return result;
    }


}
