package de.dailab.nsm.emoji.experiments;

import de.dailab.nsm.decomposition.Concept;
import de.dailab.nsm.decomposition.WordType;
import de.dailab.nsm.decomposition.graph.conceptCache.GraphUtil;
import de.dailab.nsm.decomposition.graph.edges.SynonymEdge;
import de.dailab.nsm.decomposition.graph.entities.marker.DoubleMarkerWithOrigin;
import de.dailab.nsm.decomposition.graph.entities.nodes.DoubleNodeWithMultipleThresholds;
import de.dailab.nsm.decomposition.graph.spreadingActivation.MarkerPassing.DoubleMarkerPassing;
import de.dailab.nsm.decomposition.graph.spreadingActivation.MarkerPassing.MarkerPassingConfig;
import de.tuberlin.spreadalgo.Marker;
import de.tuberlin.spreadalgo.Node;
import org.jgrapht.Graph;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Compare2Graphs {
    private Graph graph1;
    private Graph graph2;

    public Compare2Graphs(Graph graph1, Graph graph2){
        this.graph1 = graph1;
        this.graph2 = graph2;
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Graph g1 = GraphUtil.loadGraph("U+1F499", WordType.UNKNOWN, 2);
        Graph g2 = GraphUtil.loadGraph("U+1F64C", WordType.UNKNOWN, 2);
        Graph graph = GraphUtil.mergeGraph(g1, g2);
        //Compare2Graphs c2g = new Compare2Graphs(g1, g2);

        Map<Concept, List<? extends Marker>> conceptMarkerMap = new HashMap<>();
        Map<Concept, Double> threshold = new HashMap<>();
        List<Concept> originConcepts = new ArrayList<>();
        List<Concept> helperList = new ArrayList<>();
        helperList.addAll(graph.vertexSet());
        originConcepts.add(new Concept("U+1F499"));
        originConcepts.add(new Concept("U+1F64C"));
        System.out.println("Number of origin concepts: " + originConcepts.size() + "(" + originConcepts.get(0) + ", " + originConcepts.get(1) + ")");


        // create start markers
        for (Concept c : originConcepts) {
            System.out.println(c);
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
        List edges = new ArrayList<>(graph.edgeSet());
        List<Node> vertices = new ArrayList<>();
        vertices.addAll(graph.vertexSet());
        //System.out.println(((Node)vertices.get(0)).getLinks());
        System.out.println("Before " + graph.edgeSet().size());

        //List<SynonymEdge> notSynonym = (List<SynonymEdge>) edges.stream().filter(e -> !(e instanceof SynonymEdge)).collect(Collectors.toList());
        //graph.removeAllEdges(notSynonym);

        //List notReached = vertices.stream().filter(v -> ((DoubleNodeWithMultipleThresholds) v).getLinks().isEmpty()).collect(Collectors.toList());
        //graph.removeAllVertices(notReached);

        GraphUtil.saveGraph(graph, new Concept("filtered"), 2);


        //graph.edgeSet().removeAll()
        DoubleMarkerPassing doubleMarkerPassing = new DoubleMarkerPassing(graph, threshold, DoubleNodeWithMultipleThresholds.class);
        DoubleMarkerPassing.doInitialMarking(startActivation, doubleMarkerPassing);
        doubleMarkerPassing.execute();
        List<Node> activeNodes = new ArrayList<>();
        activeNodes.addAll(doubleMarkerPassing.getActiveNodes());

        // Remove this
        System.out.println(activeNodes.size());
        Node test = (DoubleNodeWithMultipleThresholds) activeNodes.get(0);
        Node test2 = (DoubleNodeWithMultipleThresholds) activeNodes.get(1);
        //System.out.println("Marker " + ((DoubleNodeWithMultipleThresholds) test).getConcept().getLitheral() + ", " + ((DoubleNodeWithMultipleThresholds) test).getMarkers());
        //System.out.println("Marker " + ((DoubleNodeWithMultipleThresholds) test2).getConcept().getLitheral() + ", " + test2.getMarkers());

        for (Node node : activeNodes) {
            if (node instanceof DoubleNodeWithMultipleThresholds) {
                System.out.print(
                        ((DoubleNodeWithMultipleThresholds) node).getConcept().getLitheral() + " " +
                                ((DoubleNodeWithMultipleThresholds) node).getActivation() + ", " +
                                ((DoubleNodeWithMultipleThresholds) node).getActivation().size() + ", ");
                System.out.print("Origin: ");
                for (Marker m : ((DoubleNodeWithMultipleThresholds) node).getMarkers()) {
                    DoubleMarkerWithOrigin marker = ((DoubleMarkerWithOrigin) m);
                    System.out.print(marker.getOrigin() + ", ");
                }
                System.out.println(" ");
            }
        }
        //doubleActiveNodes.forEach(x -> System.out.println("doubleActiveNode: " + x.getConcept().getLithe
    }

}