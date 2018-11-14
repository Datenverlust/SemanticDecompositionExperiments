package de.dailab.nsm.emoji;

import de.dailab.nsm.decomposition.Concept;
import de.dailab.nsm.decomposition.Decomposition;
import de.dailab.nsm.decomposition.Definition;
import de.dailab.nsm.decomposition.WordType;
import de.dailab.nsm.decomposition.graph.conceptCache.GraphUtil;
import org.jgrapht.Graph;


import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
//import org.apache.tinkerpop.*;

/**
 * Extends Graph Module
 */
public class GraphBuilder {

    public Graph buildGraph(String input) {

        Graph graph = null;
        Decomposition decomposition = new Decomposition();
        decomposition.init();

        // Initialize EmojinNet if input contains Emojis
        EmojiNetCrawler emojiNetCrawler = null;
        if (input.matches(".*U\\+\\w+.*")) {
            emojiNetCrawler = new EmojiNetCrawler();
            emojiNetCrawler.init();
        }

        Concept c;
        for (String word : input.split(" ")) {
            c = null;
            c = new Concept(word);

            if (word.matches("U\\+\\w+")) {
                System.out.println("Current word is an Emoji. Decompose Emoji " + word + ".");
                Graph g2 = GraphUtil.createJGraph(c);
                // Nochmal checken
                HashSet<Concept> conceptList = new HashSet<>();
                List<Concept> concepts = emojiNetCrawler.decompose(word);
                System.out.println(concepts);
                conceptList.addAll(concepts); // List to Hash Set
                c.setSynonyms(conceptList); // connect keywords to emoji TODO: use definition instead?
                /**
                 Definition definition = new Definition(emojiNetCrawler.getDefinition(word));
                 HashSet<Definition> definitions = new HashSet<>();
                 definitions.add(definition);
                 c.setDefinitions(definitions);
                 **/
            } else {
                System.out.println("Current word is NO Emoji. Decompose " + word + ".");
                c = null;
                c = decomposition.decompose(word, WordType.UNKNOWN, 1);
            }

            try {
                Graph g = GraphUtil.createJGraph(c); // graph for single emoji
                GraphUtil.saveGraph(g, c, 3);

                // merge with main graph
                if (graph == null) {
                    graph = g;
                } else {
                    graph = GraphUtil.mergeGraph(graph, g);
                }
            } catch (Exception exc) {
                exc.printStackTrace();
            }
        }

        // Save graph
        //String first_word = input.split(" ")[0];
        //GraphUtil.saveGraph(graph, new Concept(first_word), 2);

        System.out.println("Finished.");
        return graph;
    }


}
