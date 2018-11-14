package de.dailab.nsm.emoji.experiments;

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
import de.dailab.nsm.emoji.EmojiGraphBuilder;
import de.dailab.nsm.emoji.GraphBuilder;
import de.dailab.nsm.emoji.MarkerPassing;
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


public class EmotionExperiment {

    private List<Concept> concepts1 = new ArrayList<Concept>();
    private List<Concept> concepts2 = new ArrayList<Concept>();
    static MarkerPassingConfig config = new MarkerPassingConfig();

    public static void main(String[] args) throws IOException, ClassNotFoundException {

        String emojis = "U+1F602 U+2764 U+267B U+1F60D U+2665 U+1F62D U+1F60A U+1F612 U+1F495 U+1F618";
        String emotions = "anger disgust fear happiness sadness surprise";
        Graph graph2 = GraphUtil.loadGraph("basic_emotions", WordType.UNKNOWN, 2);

        /**
        String fileName = System.getProperty("user.home") + File.separator + ".decomposition" + File.separator + "Depth_2" + File.separator + "emotions" + File.separator + "emotions_summary" + ".csv";
        BufferedWriter writer2 = new BufferedWriter(new FileWriter(fileName));

        writer2.write(" ,");
        writer2.write(emotions.replaceAll(" ", ","));
        writer2.newLine();
         **/

        for(String e1 : emojis.split(" ")) {
        String input1 = e1;
        Graph graph1 = GraphUtil.loadGraph(input1, WordType.UNKNOWN, 2);

        //writer2.write(input1 + ",");

        for (String emotion : emotions.split(" ")) {
            List<Concept> originConcepts = new ArrayList<>();
            originConcepts.add(new Concept(input1));

            String input2 = emotion;
            //writer2.write(emoji + ",");
            //Concept c = new Concept(input2);

            GraphBuilder graphBuilder = new GraphBuilder();
            EmotionExperiment emo = new EmotionExperiment();
            /**
             // Build graph for each Emoji


             Graph graph1 = graphBuilder.buildGraph(input1);
             Graph graph2 = graphBuilder.buildGraph(input2);
             Graph graph = GraphUtil.mergeGraph(graph1, graph2);


             System.out.println("Graph 1: " + graph1.vertexSet().size());
             System.out.println("Graph 2: " + graph2.vertexSet().size());
             System.out.println("Graph common: " + graph.vertexSet().size());

             GraphUtil.saveGraph(graph1, new Concept("U+1F60D"), 2);
             GraphUtil.saveGraph(graph2, new Concept("U+1F602"), 2);

             **/
            Graph graph = GraphUtil.mergeGraph(graph1, graph2);
            //GraphUtil.saveGraph(graph, new Concept("merged_U+1F60D_U+1F602"), 2);
            //Graph graph = GraphUtil.loadGraph("horse", WordType.UNKNOWN, 2);

            emo.concepts1.addAll(graph1.vertexSet());
            emo.concepts2.addAll(graph2.vertexSet());
            //List<Concept> concepts2 = emojiNetCrawler.decompose(input2);

            // Graph graph = GraphUtil.loadGraph(input2, WordType.UNKNOWN, 2);
            //System.out.println("Graph geladen: " + graph.vertexSet().size());

            Map<Concept, List<? extends Marker>> conceptMarkerMap = new HashMap<>();
            Map<Concept, Double> threshold = new HashMap<>();
            List<Concept> helperList = new ArrayList<>();
            helperList.addAll(graph.vertexSet());
            //originConcepts.add(helperList.get(0));
            //originConcepts.add(helperList.get(1));
            //originConcepts.add(helperList.get(2));
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

            /**
            fileName = System.getProperty("user.home") + File.separator + ".decomposition" + File.separator + "Depth_2" + File.separator + input1 + "_" + input2 + ".txt";

            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
            writer.write("PROTOCOL " + input1 + "| " + input2);
            writer.newLine();
            writer.write("Count Edges: " + graph.edgeSet().size());
            writer.newLine();
            writer.write("Count Vertices: " + graph.vertexSet().size());
            writer.newLine();
             **/

            //List<SynonymEdge> notSynonym = (List<SynonymEdge>) edges.stream().filter(e -> !(e instanceof SynonymEdge)).collect(Collectors.toList());
            //graph.removeAllEdges(notSynonym);

            //System.out.println(doubleMarkerPassing.getNodeForConcept(new Concept("lit")).getLinks());
            //List<Node> notReached = vertices.stream().filter(v -> ((DoubleNodeWithMultipleThresholds) v).getLinks().isEmpty()).collect(Collectors.toList());
            //graph.removeAllVertices(notReached);

            //vertices.stream().forEach(v -> System.out.println(doubleMarkerPassing.getConceptForNode(v).getLitheral() + (v.getLinks().stream().forEach(v2 -> System.out.print("hey")))));
            /**
             for(Node node : vertices){
             for(Link link: node.getLinks()){
             System.out.println(link.getClass());
             }
             }
             **/

            //System.out.println("Removed " + graph.edgeSet().size());
            //System.out.println("Vertices " + graph.vertexSet().size());
            //GraphUtil.saveGraph(graph, new Concept("filtered"), 2);

            DoubleMarkerPassing doubleMarkerPassing = new DoubleMarkerPassing(graph, threshold, DoubleNodeWithMultipleThresholds.class);
            DoubleMarkerPassing.doInitialMarking(startActivation, doubleMarkerPassing);
            doubleMarkerPassing.execute();
            //System.out.println(doubleMarkerPassing.getNodeForConcept(new Concept("lit")).getLinks());
            List<Node> activeNodes = new ArrayList<>();
            activeNodes.addAll(doubleMarkerPassing.getActiveNodes());

            for (Node node : activeNodes) {
                if (node instanceof DoubleNodeWithMultipleThresholds) {
                    System.out.print(
                            ((DoubleNodeWithMultipleThresholds) node).getConcept().getLitheral() + " " +
                                    ((DoubleNodeWithMultipleThresholds) node).getActivation() + ", " +
                                    ((DoubleNodeWithMultipleThresholds) node).getActivation().size() + ", ");
                    System.out.println(" ");
                }
            }


            MarkerPassing mp = new MarkerPassing();
            List<DoubleNodeWithMultipleThresholds> dan = emo.getDoubleActivatedNodes(activeNodes);

            /**
            writer.write("Count active nodes: " + activeNodes.size());
            writer.newLine();
            writer.write("Count double activated nodes: " + dan.size());
            writer.newLine();
             **/
            dan.sort(Comparator.comparing(DoubleNodeWithMultipleThresholds::getDoubleActivation));
            /**
            dan.stream().forEach(node -> {
                try {
                    writer.write(node.getConcept() + ": " + node.getActivation());
                    writer.newLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

             **/

            double avgActivation;
            double sum = 0.0;
            int length = 0;
            for (DoubleNodeWithMultipleThresholds node : dan) {
                sum += (double) node.getDoubleActivation();
                length += node.getActivation().values().size();
            }

            avgActivation = sum / length;
            /**
            writer.write("AVERAGE ACTIVATION: " + avgActivation);
            writer.close();
            writer2.write(String.valueOf(avgActivation) + ", ");
             **/
            //dan.stream().filter(n -> n.getActivation().values().removeIf(v -> v <0));
            //dan.stream().forEach(node -> System.out.println(node.getConcept() + ": " + node.getDoubleActivation()));

            /**
             List toRemove = new ArrayList();
             for(DoubleNodeWithMultipleThresholds node: dan){
             System.out.println(node.getConcept() + ", " + node.getActivation().values());
             node.getActivation().values().removeIf(v -> v<0);
             if(node.getActivation().values().size() < 2){
             toRemove.add(node);
             }
             }
             dan.removeAll(toRemove);
             **/
            //dan.stream().forEach(node -> System.out.println(node.getConcept() + ": " + node.getDoubleActivation()));
            //doubleActiveNodes.forEach(x -> System.out.println("doubleActiveNode: " + x.getConcept().getLithe
        }
        //writer2.newLine();
        }
        //writer2.close();
    }

    public List<DoubleNodeWithMultipleThresholds> getDoubleActivatedNodes(Collection<Node> activeNodes) {
        List<DoubleNodeWithMultipleThresholds> doubleActiveNodes = new ArrayList<>();
        for (Node node : activeNodes) {
            if (node instanceof DoubleNodeWithMultipleThresholds) {
                DoubleNodeWithMultipleThresholds doubleNodeWithMultipleThresholds = (DoubleNodeWithMultipleThresholds) node;
                Concept concept = doubleNodeWithMultipleThresholds.getConcept();
                if (doubleNodeWithMultipleThresholds.getActivation().size() >= 2 && !Decomposition.getConcepts2Ignore().contains(concept.getLitheral())) {
                    //System.out.println("Concept " + concept + " " + doubleNodeWithMultipleThresholds.getActivation().size() + " Mal aktiviert");
                    // marker are not merged within mergeGraph, therefore two identical concepts are not found via marker passing because only one marker is passed
                    // note: word order is ignored here!
                    if (concepts1.contains(concept) && concepts2.contains(concept)) {
                        System.out.println("Concept " + concept + " in beiden Ausgangsconcepten");
                        doubleActiveNodes.add(doubleNodeWithMultipleThresholds);
                    }
                }
            }
        }
        return doubleActiveNodes;
    }

}
