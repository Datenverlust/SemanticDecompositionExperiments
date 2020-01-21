/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */


import de.dailab.nsm.decomposition.Concept;
import de.dailab.nsm.decomposition.WordType;
import de.dailab.nsm.decomposition.graph.conceptCache.GraphUtil;
import de.dailab.nsm.decomposition.graph.edges.WeightedEdge;
import org.jgrapht.Graph;
import org.jgrapht.graph.ListenableDirectedGraph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


/**
 * Created by faehndrich on 09.03.16.
 */

public class OCEANTest extends SemanticNetworkTest {

    public static void main(String[] args) {
        OCEANTest test = new OCEANTest();
        //test.decomposeOCEAN();
        Graph oderGraph = test.oneWord();
        GraphUtil.saveGraph(oderGraph, (Concept) oderGraph.vertexSet().iterator().next(),2);
    }


public Graph oneWord(){
    return GraphUtil.getGraph("Order", WordType.NN, 2);
}

    private void decomposeOCEAN() {
        HashMap<String,List<String>> ocean = new HashMap<>(10);
//        ocean.put("Openness", Arrays.asList("Fantasy", "Aesthetics", "Feelings", "Actions", "Ideas", "Values"));
//        ocean.put("Conscientiousness", Arrays.asList("Competence", "Order", "Dutifulness", "Achievement Striving", "Self-Discipline", "Deliberation"));
//        ocean.put("Extraversion", Arrays.asList("Warmth", "Gregariosness", "Assertiveness", "Activity", "Excitement Seeking", "Positive Emotions"));
//        ocean.put("Agreeableness", Arrays.asList("Trust", "Straightforwardness", "Altruism", "Compliance", "Modesty", "Tender-Mindedness"));
//        ocean.put("Neuroticism", Arrays.asList("Anxiety", "Hostility", "Depression", "Self-Consciousness", "Impulsiveness", "Vulnerability"));
        ocean.put("O-Big", Arrays.asList("complex", "learning", "ambiguous", "subtle","ideas", "Impulsiveness", "understand","novelty","change","exploring","new","art","poetry","liberal","ethics","tolerance","abstractions","imagination"));
        ocean.put("o-Small", Arrays.asList("conventional", "traditional", "plain", "straightforward", "obvious", "familiarity", "conservative"));
        ocean.put("C-Big", Arrays.asList("self-discipline", "dutifully", "achievement", "competition", "control", "regulate","direct","planned","prepared","details","order","schedule","deliberation"));
        ocean.put("c-Small", Arrays.asList("spontaneous", "mess"));
        ocean.put("E-Big", Arrays.asList("external", "engagement", "contact", "interacting", "energetic", "enthusiastic","action-oriented","talk","group","party","attention","conversations","crowds"));
        ocean.put("e-Small", Arrays.asList("quiet", "low-key", "deliberate", "nconnected", "preclude", "exclude", "alone","loner","ponder"));
        ocean.put("A-Big", Arrays.asList("considerate", "kind", "generous", "trusting", "trustworthy", "helpful","compromise","optimistic","philanthropist","donor","sponsor","altruistic","sacrificing","selfless","generous"));
        ocean.put("a-Small", Arrays.asList("self-interest", "self-serving", "selfish", "egoistic", "Impulsiveness", "inconsiderate","mean","greedy"));
        ocean.put("N-Big", Arrays.asList("emotional", "stress", "threatening", "frustration", "hopeless", "moody","pessimistic","anxiety","depression","disturbed","irritated","upset","worry"));
        ocean.put("n-Small", Arrays.asList("decisions", "calm", "stable", "relaxed", "cheerful", "happy", "equable"));
        Concept oceanConcept = new Concept("OCEAN");
       Graph totalMergedGraph = new ListenableDirectedGraph(WeightedEdge.class);
        for(String dimention : ocean.keySet()){
            System.out.println("Decomposing " + dimention);
            List<Graph> graphs = new ArrayList<>();
            Graph mergedGraph = new ListenableDirectedGraph(WeightedEdge.class);

            for(String word : ocean.get(dimention)){
                Graph result = GraphUtil.getGraph(word, WordType.NN, 2);
                mergedGraph = GraphUtil.mergeGraph(mergedGraph,result);
            }
            Concept dimConcept = new Concept(dimention);
            GraphUtil.saveGraph(mergedGraph,dimConcept,2);

            totalMergedGraph = GraphUtil.mergeGraph(totalMergedGraph,mergedGraph);

        }
        GraphUtil.saveGraph(totalMergedGraph,oceanConcept,2);
    return;
    }

}

