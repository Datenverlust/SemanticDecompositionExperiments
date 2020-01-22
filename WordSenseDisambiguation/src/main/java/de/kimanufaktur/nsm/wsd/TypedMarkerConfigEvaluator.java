/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

package de.kimanufaktur.nsm.wsd;

import de.kimanufaktur.nsm.decomposition.Concept;
import de.kimanufaktur.nsm.decomposition.Decomposition;
import de.kimanufaktur.nsm.decomposition.Definition;
import de.kimanufaktur.nsm.decomposition.graph.conceptCache.GraphUtil;

import de.kimanufaktur.nsm.graph.spreadingActivation.MarkerPassing.TypedMarkerPassingConfig;
import org.jgrapht.Graph;
import org.uncommons.watchmaker.framework.FitnessEvaluator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Created by root on 28.12.15.
 */
public class TypedMarkerConfigEvaluator implements FitnessEvaluator<TypedMarkerPassingConfig> {
    Decomposition decomposition = new Decomposition();
    SensevalData data = null;
    TypedMarkerConfigEvaluator(){
        decomposition.init();
        data =  SensevalData.get();
    }
    double best = 0.d;
    @Override
    public double getFitness(TypedMarkerPassingConfig config, List<? extends TypedMarkerPassingConfig> list) {
        // Create the Decomposition graph
        int foldes = 10;
        double avgFittness = 0;
        try {
            for (int i = 0; i < foldes; i++) {

                SVSentence sentence = data.getRandomSentence();
                // Create the Semantic network
                HashSet<Concept> concepts = new HashSet<>();
                HashMap<Concept, SVWord> decompositions = new HashMap<>();
                Graph sentenceGraph = null;
                boolean first = true;
                int counter = 0;
                //get Sentence graph
                sentenceGraph = getGraphOfSentence(config, sentence, concepts, decompositions, sentenceGraph, first);
                // SentenceGraph holds our network, continue by passing it to SAMP that does the spreading activation
                HashMap<Concept, Definition> definitions = getConceptDefinitions(config, concepts, sentenceGraph);
                // Calculate fitness
                avgFittness += getFitness(decompositions, definitions);

            }
            avgFittness = avgFittness/foldes;
            if (avgFittness>best){
                best = avgFittness;
                System.out.println("[Configuration] " + config.toString());
                System.out.println("[Fitness] " + avgFittness);
                Logger.logConfig(best,config,best);

            }
            //System.out.println("[Fitness] " + fitness + " : " + senseCount + " : " + definitions.size());
        } catch(Exception e) {
           e.printStackTrace();
        }

        //System.out.println("Finished fitness test: " + fitness);
        return avgFittness;
    }

    private double getFitness(HashMap<Concept, SVWord> decompositions, HashMap<Concept, Definition> definitions) {
        double fitness = 0.d;
        float senseCount = 0;
        for (Map.Entry<Concept, Definition> e : definitions.entrySet()) {
            //System.out.printf("[Fitness]:\n");
            Concept c = e.getKey();
            if(c==null) {
                continue;
            }
            Definition d = e.getValue();
            if(d==null)
                continue;
            SVWord w = decompositions.get(c);
            if(d.getSensekey()==null || w==null) {
                continue;
            }
            senseCount++;
            for (SVSense sense : w.getSenses()) {
                //System.out.printf("\t%s : %s\n",d.getSensekey(),sense.getID());
                if (d.getSensekey().equals(sense.getID()))
                    fitness += sense.getConfidence();
            }
        }
        fitness = (1.d * fitness) / senseCount;
        return fitness;
    }

    private HashMap<Concept, Definition> getConceptDefinitions(TypedMarkerPassingConfig config, HashSet<Concept> concepts, Graph sentenceGraph) {
        SAMP samp = new SAMP(config, sentenceGraph, concepts);
        return samp.execute();
    }

    private Graph getGraphOfSentence(TypedMarkerPassingConfig config, SVSentence sentence, HashSet<Concept> concepts, HashMap<Concept, SVWord> decompositions, Graph sentenceGraph, boolean first) {
        for (SVWord word : sentence.getWords()) {
            Concept concept = decomposition.multiThreadedDecompose(word.getDisambiguationSubject(),
                    word.getWordType(),
                    config.decompositionDepth);
            Graph graph = GraphUtil.createJGraph(concept);
            concepts.add(concept);
            decompositions.put(concept, word); // for evaluation
            if (first) {
                sentenceGraph = graph;
                first = !first;
            } else {
                sentenceGraph = GraphUtil.mergeGraph(sentenceGraph, graph);
            }
        }
        return sentenceGraph;
    }

    @Override
    public boolean isNatural() {
        return true;
    }
}
