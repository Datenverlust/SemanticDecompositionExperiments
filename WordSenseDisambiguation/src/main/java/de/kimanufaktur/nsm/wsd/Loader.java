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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by root on 01.04.16.
 */
public class Loader {

    private String path = "logging/"; // "/home/jkz/Documents/Uni/Bachelor/calculations/";
    private int count = 1;
    public Loader() {
        String first = "gen_372.txt";
        String second = "gen_120.txt";
        String third = "gen_15.txt";
        String file = path + first;//"gen_400.txt";
        TypedMarkerPassingConfig config = loadConfig(file);
        int size = SensevalData.get().getSize();
        for (int i = 0; i < size; i++) {
            double fitness = testSentence(config, i);
            Logger.logSolution(i, fitness, "gen_372");
        }
        file = path + second;
        config = loadConfig(file);
        size = SensevalData.get().getSize();
        for (int i = 0; i < size; i++) {
            double fitness = testSentence(config, i);
            Logger.logSolution(i, fitness, "gen_120");
        }
        file = path + third;
        config = loadConfig(file);
        size = SensevalData.get().getSize();
        for (int i = 0; i < size; i++) {
            double fitness = testSentence(config, i);
            Logger.logSolution(i, fitness, "gen_15");
        }
    }

    public static void main(String[] args) {
        Loader load = new Loader();
    }

    public void old() {
        for (int i = 0; i < count; i++) {
            try {
                String fPath = path + "/gen_" + i + ".obj";
                FileInputStream fi = new FileInputStream(fPath);
                ObjectInputStream obj = new ObjectInputStream(fi);
                Object object = obj.readObject();
                if (object instanceof TypedMarkerPassingConfig) {
                    TypedMarkerPassingConfig conf = (TypedMarkerPassingConfig) object;
                    System.out.println(conf.toString());
                }
                //TypedMarkerPassingConfig conf = (TypedMarkerPassingConfig) obj.readObject();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public TypedMarkerPassingConfig loadConfig(String path) {
        TypedMarkerPassingConfig config = new TypedMarkerPassingConfig();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            int lineC = 0;
            String line = null;
            while ((line = br.readLine()) != null) {
                lineC++;
                switch (lineC) {
                    case 5:
                        config.threshold = Double.parseDouble(getAfterEqual(line));
                        break;
                    case 6:
                        config.synThreshold = Double.parseDouble(getAfterEqual(line));
                        break;
                    case 7:
                        config.hypoThreshold = Double.parseDouble(getAfterEqual(line));
                        break;
                    case 8:
                        config.hyperThreshold = Double.parseDouble(getAfterEqual(line));
                        break;
                    case 9:
                        config.antoThreshold = Double.parseDouble(getAfterEqual(line));
                        break;
                    case 10:
                        config.defThreshold = Double.parseDouble(getAfterEqual(line));
                        break;
                    case 12:
                        config.synWeight = Double.parseDouble(getAfterEqual(line));
                        break;
                    case 13:
                        config.hyperWeight = Double.parseDouble(getAfterEqual(line));
                        break;
                    case 14:
                        config.hypoWeight = Double.parseDouble(getAfterEqual(line));
                        break;
                    case 15:
                        config.antoWeight = Double.parseDouble(getAfterEqual(line));
                        break;
                    case 16:
                        config.defWeight = Double.parseDouble(getAfterEqual(line));
                        break;
                    default:
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return config;
    }

    public String getAfterEqual(String line) {
        return line.split("=")[1];
    }

    public double testSentence(TypedMarkerPassingConfig config, int sentenceNumber) {
        double fitness = 0.d;
        try {
            Decomposition decomposition = new Decomposition();
            decomposition.init();
            SensevalData data = SensevalData.get();
            SVSentence sentence = data.getSentence(sentenceNumber); //data.getRandomSentence();
            // Create the Semantic network
            HashSet<Concept> concepts = new HashSet<>();
            HashMap<Concept, SVWord> decompositions = new HashMap<>();
            Graph sentenceGraph = null;
            boolean first = true;
            int counter = 0;
            for (SVWord word : sentence.getWords()) {
                //Concept concept = decomposition.multiThreadedDecompose(word.getDisambiguationSubject(),word.getWordType(),config.decompositionDepth);
               Concept concept = decomposition.decompose(word.getDisambiguationSubject(),word.getWordType(),config.decompositionDepth);
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
            // SentenceGraph holds our network, continue by passing it to SAMP that does the spreading activation
            SAMP samp = new SAMP(config, sentenceGraph, concepts);
            HashMap<Concept, Definition> definitions = samp.execute();

            // Calculate fitness
            float senseCount = 0;
            for (Map.Entry<Concept, Definition> e : definitions.entrySet()) {
                System.out.printf("[Fitness]:\n");
                Concept c = e.getKey();
                if (c == null) {
                    continue;
                }
                Definition d = e.getValue();
                if (d == null)
                    continue;
                SVWord w = decompositions.get(c);
                if (d.getSensekey() == null || w == null) {
                    continue;
                }
                senseCount++;
                for (SVSense sense : w.getSenses()) {
                    System.out.printf("\t%s : %s\n", d.getSensekey(), sense.getID());
                    if (d.getSensekey().equals(sense.getID()))
                        fitness += sense.getConfidence();
                }
            }
            fitness = (1.d * fitness) / senseCount;
            System.out.println("[Fitness] " + fitness + " : " + senseCount + " : " + definitions.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fitness;
    }

}
