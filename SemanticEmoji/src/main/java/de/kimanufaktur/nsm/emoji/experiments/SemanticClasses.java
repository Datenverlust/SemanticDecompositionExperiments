package de.kimanufaktur.nsm.emoji.experiments;

import de.kimanufaktur.nsm.decomposition.Concept;
import de.kimanufaktur.nsm.decomposition.Decomposition;
import de.kimanufaktur.nsm.decomposition.WordType;
import de.kimanufaktur.nsm.decomposition.graph.conceptCache.GraphUtil;
import de.kimanufaktur.nsm.decomposition.graph.entities.marker.DoubleMarkerWithOrigin;
import de.kimanufaktur.nsm.decomposition.graph.entities.nodes.DoubleNodeWithMultipleThresholds;
import de.kimanufaktur.nsm.decomposition.graph.spreadingActivation.MarkerPassing.DoubleMarkerPassing;
import de.kimanufaktur.nsm.decomposition.graph.spreadingActivation.MarkerPassing.MarkerPassingConfig;
import de.kimanufaktur.nsm.emoji.EmojiGraphBuilder;
import de.kimanufaktur.nsm.emoji.GraphBuilder;
import de.tuberlin.spreadalgo.Marker;
import de.tuberlin.spreadalgo.Node;
import org.jgrapht.Graph;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class SemanticClasses {

    private Graph graph;
    private List<Concept> originConcepts = new ArrayList<>();
    private MarkerPassingConfig config = new MarkerPassingConfig();

    SemanticClasses() throws IOException, ClassNotFoundException {

        EmojiGraphBuilder emojiGraphBuilder = new EmojiGraphBuilder();
        /**
        try{
            String first_emoji = EmojiGraphBuilder.getEmojisAsString().split(" ")[0];
            this.graph =  GraphUtil.loadGraph(first_emoji, WordType.UNKNOWN, 2);
        }catch (Exception e){
            e.printStackTrace();
            this.graph = emojiGraphBuilder.getEmojiGraph();
        }
        **/
        //this.graph = emojiGraphBuilder.getEmojiGraph();
        GraphBuilder graphBuilder = new GraphBuilder();
        String emotions = "U+1F612";
        this.graph = graphBuilder.buildGraph(emotions);
        GraphUtil.saveGraph(this.graph, new Concept(emotions), 1);
        this.originConcepts = emojiGraphBuilder.getEmojisAsConcepts();

    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        SemanticClasses semanticClasses = new SemanticClasses();
        //semanticClasses.doMarkerPassing();
    }

    private void doMarkerPassing() {
        Map<Concept, List<? extends Marker>> conceptMarkerMap = new HashMap<>();
        Map<Concept, Double> threshold = new HashMap<>();

        // create start markers
        for (Concept c : originConcepts) {
            //System.out.println(c);
            List<Marker> markers = new ArrayList<>();
            Concept activeNode = c;
            DoubleMarkerWithOrigin startMarker = new DoubleMarkerWithOrigin(); // change to MarkerWithHistory
            startMarker.setActivation(MarkerPassingConfig.getStartActivation());
            startMarker.setOrigin(activeNode);
            markers.add(startMarker);
            conceptMarkerMap.put(activeNode, markers);
            threshold.put(activeNode, MarkerPassingConfig.getThreshold());
        }

        //set start markers
        List<Map<Concept, List<? extends Marker>>> startActivation = new ArrayList<>();
        startActivation.add(conceptMarkerMap);

        //create marker passing algorithm
        DoubleMarkerPassing doubleMarkerPassing = new DoubleMarkerPassing(graph, threshold, DoubleNodeWithMultipleThresholds.class);
        DoubleMarkerPassing.doInitialMarking(startActivation, doubleMarkerPassing);
        doubleMarkerPassing.execute();
        Collection<Node> activeNodes = doubleMarkerPassing.getActiveNodes();
        System.out.println(activeNodes.size());

        for (Node node : activeNodes) {
            if (node instanceof DoubleNodeWithMultipleThresholds) {
                System.out.print(
                        ((DoubleNodeWithMultipleThresholds) node).getActivationHistory() + " " +
                        ((DoubleNodeWithMultipleThresholds) node).getConcept().getLitheral() + " " +
                        ((DoubleNodeWithMultipleThresholds) node).getActivation() + ", " +
                        ((DoubleNodeWithMultipleThresholds) node).getActivation().size() + ", ");
                System.out.print("Origin: ");
                for (Marker m : node.getMarkers()) {
                    DoubleMarkerWithOrigin marker = ((DoubleMarkerWithOrigin) m);
                    System.out.print(marker.getOrigin() + ", ");
                }
                System.out.println();
            }
        }
        //doubleActiveNodes.forEach(x -> System.out.println("doubleActiveNode: " + x.getConcept().getLithe
    }
}
