/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

package de.kimanufaktur.nsm.wsd;


import de.kimanufaktur.nsm.graph.spreadingActivation.MarkerPassing.TypedMarkerPassingConfig;
import org.uncommons.maths.number.ConstantGenerator;
import org.uncommons.maths.number.NumberGenerator;
import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by root on 27.12.15.
 */
public class TypedMarkerConfigMutation implements EvolutionaryOperator<TypedMarkerPassingConfig> {

    private final NumberGenerator<Probability> mutationProbability;


    public TypedMarkerConfigMutation(Probability probability) {
        mutationProbability = new ConstantGenerator<>(probability);
    }

    @Override
    public List<TypedMarkerPassingConfig> apply(List<TypedMarkerPassingConfig> list, Random random) {
        List<TypedMarkerPassingConfig> result = new ArrayList<>(list.size());
        for(TypedMarkerPassingConfig config : list) {
            result.add(mutate(config,random));
        }
        return result;
    }

    public TypedMarkerPassingConfig mutate(TypedMarkerPassingConfig config, Random rng) {
        TypedMarkerPassingConfig tmp = new TypedMarkerPassingConfig(config);
        int args = TypedMarkerPassingConfig.getNumberOfParameter();
        for(int i=1;i<args;i++) {
            if(mutationProbability.nextValue().nextEvent(rng)) {
                tmp.swap(tmp.get(i) + ((rng.nextDouble()*2) - 1.d),i); // +[-1;+1] to the current value
            }
        }
        return tmp;
    }
}
