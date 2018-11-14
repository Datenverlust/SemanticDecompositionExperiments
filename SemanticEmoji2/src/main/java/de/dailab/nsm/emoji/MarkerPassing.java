package de.dailab.nsm.emoji;

import de.dailab.nsm.decomposition.Concept;
import de.dailab.nsm.decomposition.Decomposition;
import de.dailab.nsm.decomposition.WordType;
import de.dailab.nsm.decomposition.graph.conceptCache.GraphUtil;
import de.dailab.nsm.decomposition.graph.edges.DefinitionEdge;
import de.dailab.nsm.decomposition.graph.edges.EdgeType;
import de.dailab.nsm.decomposition.graph.edges.SynonymEdge;
import de.dailab.nsm.decomposition.graph.entities.marker.DoubleMarkerWithOrigin;
import de.dailab.nsm.decomposition.graph.entities.nodes.DoubleNodeWithMultipleThresholds;
import de.dailab.nsm.decomposition.graph.entities.relations.Relation;
import de.dailab.nsm.decomposition.graph.spreadingActivation.MarkerPassing.DoubleMarkerPassing;
import de.dailab.nsm.decomposition.graph.spreadingActivation.MarkerPassing.MarkerPassingConfig;
import de.dailab.nsm.decomposition.graph.spreadingActivation.MarkerPassing.TypedMarkerPassing;
import de.dailab.nsm.decomposition.graph.spreadingActivation.MarkerPassing.TypedMarkerPassingConfig;
import de.tuberlin.spreadalgo.Link;
import de.tuberlin.spreadalgo.Marker;
import de.tuberlin.spreadalgo.Node;
import org.ehcache.core.internal.util.ConcurrentWeakIdentityHashMap;
import org.jgrapht.Graph;

import java.awt.datatransfer.SystemFlavorMap;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


public class MarkerPassing {

    private List<Concept> concepts1 = new ArrayList<Concept>();
    private List<Concept> concepts2 = new ArrayList<Concept>();
    static MarkerPassingConfig config = new MarkerPassingConfig();

    public static void main(String[] args) throws IOException, ClassNotFoundException {

        String fileName = System.getProperty("user.home") + File.separator + ".decomposition" + File.separator + "Depth_2" + File.separator + "U+2665_U+1F602_check" + ".csv";
        BufferedWriter writer2 = new BufferedWriter(new FileWriter(fileName));

        writer2.write("graph1, graph2, litheral, activation");

        writer2.newLine();

        for (String e1 : EmojiGraphBuilder.getEmojisAsString().split(" ")) {
            String input1 = e1;
            Graph graph1 = GraphUtil.loadGraph(input1, WordType.UNKNOWN, 2);

            //for (String emoji : EmojiGraphBuilder.getEmojisAsString().split(" ")) {
            List<Concept> originConcepts = new ArrayList<>();
            originConcepts.add(new Concept(input1));

            String input2 = "U+2665";

            GraphBuilder graphBuilder = new GraphBuilder();
            MarkerPassing markerPassing = new MarkerPassing();

            Graph graph2 = GraphUtil.loadGraph(input2, WordType.UNKNOWN, 2);
            Graph graph = GraphUtil.mergeGraph(graph1, graph2);
            //GraphUtil.saveGraph(graph, new Concept("merged_U+1F60D_U+1F602"), 2);
            //Graph graph = GraphUtil.loadGraph("horse", WordType.UNKNOWN, 2);

            markerPassing.concepts1.addAll(graph1.vertexSet());
            markerPassing.concepts2.addAll(graph2.vertexSet());

            Map<Concept, List<? extends Marker>> conceptMarkerMap = new HashMap<>();
            Map<Concept, Double> threshold = new HashMap<>();
            List<Concept> helperList = new ArrayList<>();
            helperList.addAll(graph.vertexSet());
            originConcepts.add(new Concept(input2));
            System.out.println("Number of origin concepts: " + originConcepts.size() + "(" + originConcepts.get(0) + ", " + originConcepts.get(1) + ")");


            // create start markers
            for (Concept con : originConcepts) {
                //System.out.println(c);
                List<Marker> markers = new ArrayList<>();
                Concept activeNode = con;
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

            fileName = System.getProperty("user.home") + File.separator + ".decomposition" + File.separator + "Depth_2" + File.separator + input1 + "_" + input2 + "_new.txt";

            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
            writer.write("PROTOCOL " + input1 + "| " + input2);
            writer.newLine();
            writer.write("Count Edges: " + graph.edgeSet().size());
            writer.newLine();
            writer.write("Count Vertices: " + graph.vertexSet().size());
            writer.newLine();

            DoubleMarkerPassing doubleMarkerPassing = new DoubleMarkerPassing(graph, threshold, DoubleNodeWithMultipleThresholds.class);
            DoubleMarkerPassing.doInitialMarking(startActivation, doubleMarkerPassing);
            doubleMarkerPassing.execute();
            List<Node> activeNodes = new ArrayList<>();
            activeNodes.addAll(doubleMarkerPassing.getActiveNodes());

            /**
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
             **/


            MarkerPassing mp = new MarkerPassing();
            List<DoubleNodeWithMultipleThresholds> dan = markerPassing.getDoubleActivatedNodes(activeNodes);

            writer.write("Count active nodes: " + activeNodes.size());
            writer.newLine();
            writer.write("Count double activated nodes: " + dan.size());
            writer.newLine();

            dan.sort(Comparator.comparing(DoubleNodeWithMultipleThresholds::getDoubleActivation));
            System.out.println("Before " + dan.size());


            List<DoubleNodeWithMultipleThresholds> filtered = dan.stream().filter(n -> n.getDoubleActivation() >= 4).collect(Collectors.toList());
            System.out.println("After" + filtered.size());


            filtered.stream().forEach(node -> {
                try {
                    writer.write(node.getConcept() + ": " + node.getActivation() + " [" + node.getDoubleActivation() + "]");
                    writer.newLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });


            filtered.sort(Comparator.comparing(DoubleNodeWithMultipleThresholds::getDoubleActivation));
            filtered.stream().forEach(node -> {
                try {
                    writer2.write(input1 + "," + input2 + "," + node.getConcept() + "," + node.getDoubleActivation() + "," + node.getActivation());
                    writer2.newLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });


            double avgActivation;
            double sum = 0.0;
            int length = 0;
            for (DoubleNodeWithMultipleThresholds node : dan) {
                sum += (double) node.getDoubleActivation();
                length += node.getActivation().values().size();
            }

            avgActivation = sum / length;
            writer.write("AVERAGE ACTIVATION: " + avgActivation);
            writer.close();
        }
        writer2.newLine();
        //}
        writer2.close();
    }

    public List<DoubleNodeWithMultipleThresholds> getDoubleActivatedNodes(Collection<Node> activeNodes) {
        List<DoubleNodeWithMultipleThresholds> doubleActiveNodes = new ArrayList<>();
        for (Node node : activeNodes) {
            if (node instanceof DoubleNodeWithMultipleThresholds) {
                DoubleNodeWithMultipleThresholds doubleNodeWithMultipleThresholds = (DoubleNodeWithMultipleThresholds) node;
                Concept concept = doubleNodeWithMultipleThresholds.getConcept();
                if (doubleNodeWithMultipleThresholds.getActivation().size() >= 2 && !Decomposition.getConcepts2Ignore().contains(concept.getLitheral())) {
                    if (concepts1.contains(concept) && concepts2.contains(concept)) {
                        doubleActiveNodes.add(doubleNodeWithMultipleThresholds);
                    }
                }
            }
        }
        return doubleActiveNodes;
    }

}
