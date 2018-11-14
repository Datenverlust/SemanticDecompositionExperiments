/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

package de.dailab.nsm.wsd;

import de.dailab.nsm.decomposition.graph.spreadingActivation.MarkerPassing.TypedMarkerPassingConfig;
import org.uncommons.maths.random.MersenneTwisterRNG;
import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.framework.*;
import org.uncommons.watchmaker.framework.operators.EvolutionPipeline;
import org.uncommons.watchmaker.framework.selection.RouletteWheelSelection;
import org.uncommons.watchmaker.framework.termination.GenerationCount;
import org.uncommons.watchmaker.framework.termination.TargetFitness;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by root on 27.12.15.
 */
public class Evolution {

    public static void main(String[] args) {
        Evolution evo = new Evolution();
    }

    public Evolution() {
        // Candidate factory
        TypedMarkerFactory factory = new TypedMarkerFactory();
        // Evolutionary operator
        List<EvolutionaryOperator<TypedMarkerPassingConfig>> operators = new LinkedList<>();
        operators.add(new TypedMarkerConfigCrossover());
        operators.add(new TypedMarkerConfigMutation(new Probability(0.02)));
        EvolutionaryOperator<TypedMarkerPassingConfig> pipeline = new EvolutionPipeline<>(operators);
        // Fitness evaluator
        TypedMarkerConfigEvaluator fitness = new TypedMarkerConfigEvaluator();
        // Selection
        RouletteWheelSelection selection = new RouletteWheelSelection();
        // RNG
        MersenneTwisterRNG rng = new MersenneTwisterRNG();


        GenerationalEvolutionEngine<TypedMarkerPassingConfig> engine = new GenerationalEvolutionEngine<TypedMarkerPassingConfig>(
                factory,
                pipeline,
                fitness,
                selection,
                rng
        );

        // Observer
        engine.addEvolutionObserver(new EvolutionObserver<TypedMarkerPassingConfig>() {
            @Override
            public void populationUpdate(PopulationData<? extends TypedMarkerPassingConfig> populationData) {
                //System.out.printf("Generation " + populationData.getGenerationNumber()
                //        + " : fitness "+ populationData.getBestCandidateFitness()+ "\n");

                //de.dailab.nsm.wsd.Logger.logBestCandidate(populationData.getGenerationNumber() ,populationData.getBestCandidate());
                //de.dailab.nsm.wsd.Logger.logMeanGeneration(populationData.getGenerationNumber(),populationData.getMeanFitness(),populationData.getElapsedTime());
                //Logger.logConfig(populationData.getGenerationNumber(),populationData.getBestCandidate(), populationData.getBestCandidateFitness());
                //Logger.logGeneration(populationData.getGenerationNumber(),populationData.getMeanFitness(),populationData.getBestCandidateFitness());

            }
        });

        //set single threaded because memory is limited
        engine.setSingleThreaded(true);

        // RUN!
        engine.evolve(10,1, new TargetFitness(0.9, true));//GenerationCount(500000));
    }
}
