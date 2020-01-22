/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

package de.kimanufaktur.nsm.decomposition.graph.spreadingActivation.MarkerPassing.ParameterLearner;

import de.kimanufaktur.nsm.decomposition.graph.spreadingActivation.MarkerPassing.MarkerPassingConfig;
import de.kimanufaktur.nsm.decomposition.graph.spreadingActivation.MarkerPassing.ParameterLearner.ParameterLearner.MarkerPassingConfigurationEvaluator;
import org.uncommons.maths.random.MersenneTwisterRNG;
import org.uncommons.watchmaker.framework.*;
import org.uncommons.watchmaker.framework.factories.AbstractCandidateFactory;
import org.uncommons.watchmaker.framework.operators.EvolutionPipeline;
import org.uncommons.watchmaker.framework.selection.StochasticUniversalSampling;
import org.uncommons.watchmaker.framework.termination.TargetFitness;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Created by faehndrich on 03.04.16.
 */
public class ParamerterLearningExperimentGeneticAlgorithm {
    static Random  random = new Random();
    static EvolutionEngine<MarkerPassingConfig> engine = null;
    AbstractCandidateFactory factory = null;
    //MarkerPassingConfig bestconfig = new MarkerPassingConfig();
    static MarkerPassingConfig markerPassingConfig = new MarkerPassingConfig();
    static MarkerPassingConfigurationEvaluator fitnessEvaluator;
    public static void main(String[] args) {
        try {
            Class<MarkerPassingConfigurationEvaluator> evaluatorClass = (Class<MarkerPassingConfigurationEvaluator>) Class.forName(args[0]);

            fitnessEvaluator = evaluatorClass.getDeclaredConstructor().newInstance();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        ParamerterLearningExperimentGeneticAlgorithm test = new ParamerterLearningExperimentGeneticAlgorithm();
        test.init();
        MarkerPassingConfig result = test.engine.evolve(10000000, 5, new TargetFitness(90.0d, true));
        System.out.println(result);
    }

    public void init() {
        factory = new AbstractCandidateFactory() {
            @Override
            public Object generateRandomCandidate(Random random) {
                return createNewMarkerpassingConfig(random);
            }

        };
        List<EvolutionaryOperator<MarkerPassingConfig>> evolutionaryOperator
                = new LinkedList<>();
        evolutionaryOperator.add(new EvolutionaryOperator() {
            @Override
            public List apply(List list, Random random) {
                List<MarkerPassingConfig> result = new ArrayList<>();
                Iterator iter = list.iterator();
                while (iter.hasNext()) {
                    result.add(createNewMarkerpassingConfig(random));
                }
                return result;
            }
        });

        EvolutionaryOperator<MarkerPassingConfig> pipeline
                = new EvolutionPipeline<>(evolutionaryOperator);
        SelectionStrategy<Object> selectionStrategy = new StochasticUniversalSampling();//new SigmaScaling();//new StochasticUniversalSampling();//new TournamentSelection(new Probability(0.9)); //new RouletteWheelSelection();
        Random rng = new MersenneTwisterRNG();
//        MarkerPassingConfigurationEvaluator fitnessEvaluator = new MarkerPassingConfigurationEvaluator();
//        MarkerPassingConfigurationEvaluation4SentenceSimilarity fitnessEvaluator = new MarkerPassingConfigurationEvaluation4SentenceSimilarity();
        engine = new GenerationalEvolutionEngine<>(factory,
                pipeline,
                fitnessEvaluator,
                selectionStrategy,
                rng);
        //Add evolution observer.
        engine.addEvolutionObserver(data -> System.out.printf("Generation %d: %s\n",
                data.getGenerationNumber(),
                data.getBestCandidate()));
    }

    public static MarkerPassingConfig createNewMarkerpassingConfig(Random random) {
        //Set common parameters
        MarkerPassingConfig markerPassingConfigCandidate = new MarkerPassingConfig();

        //Marker Passing parameters
        markerPassingConfigCandidate.setThreshold(random.nextDouble());
        markerPassingConfigCandidate.setStartActivation(markerPassingConfig.getStartActivation()* random.nextDouble());
        markerPassingConfigCandidate.setTerminationPulsCount(random.nextInt(markerPassingConfig.getTerminationPulsCount()));
        markerPassingConfigCandidate.setDoubleActivationLimit(markerPassingConfig.getDoubleActivationLimit() * random.nextDouble());
        //Set weights
//        markerPassingConfigCandidate.setSynonymLinkWeight(markerPassingConfig.getSynonymLinkWeight()*random.nextDouble());
//        markerPassingConfigCandidate.setAntonymLinkWeight(markerPassingConfig.getAntonymLinkWeight()* random.nextDouble());
//        markerPassingConfigCandidate.setHypernymLinkWeight(markerPassingConfig.getHypernymLinkWeight()* random.nextDouble());
//        markerPassingConfigCandidate.setHyponymLinkWeight(markerPassingConfig.getHyponymLinkWeight()*random.nextDouble());
//        markerPassingConfigCandidate.setDefinitionLinkWeight(markerPassingConfig.getDefinitionLinkWeight()* random.nextDouble());

        //Set arbitrary weights
        for (String relationName : markerPassingConfigCandidate.getArbitraryRelationWeights().keySet()) {
            double currentWeight = markerPassingConfigCandidate.getLinkWeightForRelationWithName(relationName);
            markerPassingConfigCandidate.setArbitraryRelationLinkWeight(relationName, currentWeight * random.nextDouble());
        }

        return markerPassingConfigCandidate;
    }

    public static double getRandomDoubleMutation(double original, double min, double max){
        //Mutate at all?
        if(random.nextBoolean())
        {
            //Mutate positive
            if(random.nextBoolean()){
                return  original + (max - original) * random.nextDouble();
            }else{//Mutate negative
                return  min + (original - min) * random.nextDouble();
            }
        }else{
            return original;
        }
    }
    public static int getRandomIntegerMutation(int original, int min, int max){
        //Mutate at all?
        if(random.nextBoolean())
        {
            //Mutate positive
            int limit = max-original;
            if(limit <1){
                limit =1;
            }
            if(random.nextBoolean()){


                return Math.abs(original +  random.nextInt(limit));
            }else{//Mutate negative
                return Math.abs(original -  (min+original) + random.nextInt(max+original - (min+original) + 1));
            }
        }else{
            return original;
        }
    }
}
