/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

import de.dailab.nsm.decomposition.Concept;
import de.dailab.nsm.decomposition.Decomposition;
import de.dailab.nsm.decomposition.Definition;
import de.dailab.nsm.decomposition.graph.conceptCache.GraphUtil;
import de.dailab.nsm.decomposition.graph.spreadingActivation.MarkerPassing.MarkerPassingConfig;
import de.dailab.nsm.decomposition.graph.spreadingActivation.MarkerPassing.ParameterLearner.LearnMPParameters;
import de.dailab.nsm.decomposition.graph.spreadingActivation.MarkerPassing.ParameterLearner.ParamerterLearningExperimentGeneticAlgorithm;
import de.dailab.nsm.decomposition.graph.spreadingActivation.MarkerPassing.TypedMarkerPassingConfig;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import org.jgrapht.Graph;
import org.uncommons.watchmaker.framework.FitnessEvaluator;
import se.lth.cs.srl.CompletePipeline;

import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * Created by root on 28.12.15.
 */
public class DoubleMarkerConfigEvaluator  {


    public double getFitness(MarkerPassingConfig config, List<GraphCache> questions) {
        // Create the Decomposition graph
//        MarkerPassingConfig mk = new MarkerPassingConfig(config);
        //MarkerPassingConfig mk = config.clone(); //Why does this implement Cloneable and acually has no clone method?s
        double fitness = 0.d;
        double fitnesSumme = 0.0;
        for(GraphCache g : questions) {
            try {
                Evaluator eva = new Evaluator();
                Map<String,Double> map = QuestionAnswer.getAnswerMap(g);
                Question q = g.question;
                if(Evaluator.getAnswerQuestion(map).equals(q.rightAwnser)){
                    if(Evaluator.getRightAnswer(map, q.rightAwnser)!=0) {
                        fitnesSumme = fitnesSumme + ((Evaluator.getRightAnswer(map, q.rightAwnser) - Evaluator.getSecondHighestAnswer(map)) / Evaluator.getRightAnswer(map, q.rightAwnser));
                    }else{
                        fitnesSumme = fitnesSumme + ((Evaluator.getRightAnswer(map, q.rightAwnser) - Evaluator.getSecondHighestAnswer(map)) / 1);
                    }
                }else{
                    if(Evaluator.getHighestAnswer(map)!=0) {
                        fitnesSumme = fitnesSumme + ((Evaluator.getRightAnswer(map, q.rightAwnser) - Evaluator.getHighestAnswer(map)) / Evaluator.getHighestAnswer(map));
                    }else{
                        fitnesSumme = fitnesSumme + ((Evaluator.getRightAnswer(map, q.rightAwnser) - Evaluator.getHighestAnswer(map)) / 1);
                    }
                }




                System.out.println("[Fitness] " + fitness);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        fitness= fitnesSumme/(double) questions.size();

        System.out.println("Finished fitness test: " + fitness);
        return fitness;
    }





}

