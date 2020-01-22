package de.kimanufaktur.nsm.emoji;

import de.kimanufaktur.nsm.decomposition.Concept;
import de.kimanufaktur.nsm.decomposition.graph.conceptCache.GraphUtil;
import org.jgrapht.Graph;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EmojiGraphBuilder {

    private static GraphBuilder graphBuilder = new GraphBuilder();

    public static Graph getEmojiGraph() throws IOException {
        String emojis = getEmojisAsString();
        Graph graph = graphBuilder.buildGraph(emojis); // use extended GraphBuilder to build special Emoji Graph
        //graphBuilder.saveToGraphML(graph, path);
        return graph;
    }


    public static String getEmojisAsString(){
        /**
         *  reference: http://emojitracker.com/, visited: 20/09/2018, 11:26
         *  #1 face with tears of joy U+1F602
         *  #2 red heart U+2764
         *  #3 recycle U+267B
         *  #4 smiling face with heart eyes U+1F60D
         *  #5 heart symbol emoji, which is used in card games U+2665
         *  #6 sad face with tears streaming down U+1F62D
         *  #7 smiling face with smiling eyes  U+1F60A
         *  #8 face is not amused U+1F612
         *  #9 Two pink love hearts U+1F495
         *  #10 An emoji face blowing a kiss U+1F618
         **/

        return "U+1F62D U+2764 U+267B U+1F60D U+2665 U+1F62D U+1F60A U+1F612 U+1F495 U+1F618";
    }


    public static List<Concept> getEmojisAsConcepts(){
        String emojis = getEmojisAsString();
        List<Concept> concepts = new ArrayList<>();

        for(String e: emojis.split(" ")){
            concepts.add(new Concept(e));
        }
        return concepts;
    }
}
