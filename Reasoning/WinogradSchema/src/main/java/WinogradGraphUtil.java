/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

import de.dailab.nsm.decomposition.Concept;
import de.dailab.nsm.decomposition.Definition;
import de.dailab.nsm.decomposition.graph.conceptCache.GraphUtil;
import de.dailab.nsm.decomposition.graph.edges.WeightedEdge;
import edges.NerEdge;
import edges.RoleEdge;
import edges.SyntaxEdge;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import org.jgrapht.Graph;
import org.jgrapht.graph.ListenableDirectedGraph;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by Hannes on 04.04.2017.
 */
public class WinogradGraphUtil {

    public static Graph mergeDecGraphs (List<Graph> graphs) {
        Graph mergedGraph = new ListenableDirectedGraph(WeightedEdge.class);
        for (Graph wordGraph :
                graphs) {
            mergedGraph = GraphUtil.mergeGraph(mergedGraph, wordGraph);
        }
        return mergedGraph;
    }

    public static List<Graph> synEdgesToGraphs(List<List<SemanticGraphEdge>> edgeList, PronConcept pron){
        List<Graph> graphs=new ArrayList<>();

        //Für jeden Satz, i ist SatzNr
        for(int i=0;i<edgeList.size();i++) {
            List<SemanticGraphEdge> sentenceEdges=edgeList.get(i);

            for (SemanticGraphEdge edge : sentenceEdges) {
                if (!edge.getRelation().getShortName().equals("punct")) {
                    Concept source;
                    if(edge.getRelation().getShortName().equals("neg")){
                        source=new NegatedConcept(edge.getSource().lemma());
                    } else {
                        source = new Concept(edge.getSource().lemma());
                    }
                    Concept target = new Concept(edge.getTarget().lemma());

                    int sourceNr = edge.getSource().index() - 1;
                    if (pron.isPron(i, sourceNr)) {
                        source = new PronConcept(pron.getOriginalName(), pron.getWortNr(), pron.getSatzNr());
                    }
                    int targetNr = edge.getTarget().index() - 1;
                    if (pron.isPron(i, targetNr)) {
                        target = new PronConcept(pron.getOriginalName(), pron.getWortNr(), pron.getSatzNr());
                    }

                    String relation = edge.getRelation().getShortName();
                    String specific = edge.getRelation().getSpecific();

                    ListenableDirectedGraph graph = new ListenableDirectedGraph(SyntaxEdge.class);
                    graph.addVertex(source);
                    graph.addVertex(target);

                    SyntaxEdge synEdge = new SyntaxEdge();
                    synEdge.setSource(source);
                    synEdge.setTarget(target);

                    if (specific != null)
                        synEdge.setRelationName(specific);
                    else
                        synEdge.setRelationName(relation);

                    graph.addEdge(source, target, synEdge);
                    graphs.add(graph);

                }
            }
        }
        return graphs;
    }

    public static Graph mergeAllSynGraphs(List<Graph> graphs){
        Graph mergedGraph = new ListenableDirectedGraph(SyntaxEdge.class);

        for (Graph wordGraph : graphs) {
            mergedGraph = mergeSynGraphs(mergedGraph, wordGraph);
        }
        return mergedGraph;
    }

    public static Graph mergeSynGraphs(Graph graph1, Graph graph2){
        ListenableDirectedGraph result = null;
        if(result == null) {
            result = new ListenableDirectedGraph(SyntaxEdge.class);
            // Get all vertex from the graph1
            Iterator iterator = graph1.vertexSet().iterator();
            AddVertexesOfGraph(result, iterator);

            // Get all edges from graph1
            iterator = graph1.edgeSet().iterator();
            AddSynEdgesOfGraph(result, iterator);

            // Get all vertex from the graph2
            iterator = graph2.vertexSet().iterator();
            AddVertexesOfGraph(result, iterator);
            // Get all edges from graph2
            iterator = graph2.edgeSet().iterator();
            AddSynEdgesOfGraph(result, iterator);
        }

        return result;
    }

    public static Graph mergeAllRoleGraphs(List<Graph> graphs){
        Graph mergedGraph = new ListenableDirectedGraph(RoleEdge.class);
        for (Graph wordGraph : graphs) {
            mergedGraph = mergeRoleGraphs(mergedGraph, wordGraph);
        }
        return mergedGraph;
    }

    public static Graph mergeRoleGraphs(Graph graph1, Graph graph2){
        ListenableDirectedGraph result = null;
        if(result == null) {
            result = new ListenableDirectedGraph(RoleEdge.class);
            // Get all vertex from the graph1
            Iterator iterator = graph1.vertexSet().iterator();
            AddVertexesOfGraph(result, iterator);

            // Get all edges from graph1
            iterator = graph1.edgeSet().iterator();
            AddRoleEdgesOfGraph(result, iterator);

            // Get all vertex from the graph2
            iterator = graph2.vertexSet().iterator();
            AddVertexesOfGraph(result, iterator);
            // Get all edges from graph2
            iterator = graph2.edgeSet().iterator();
            AddRoleEdgesOfGraph(result, iterator);
        }

        return result;
    }

    public static Graph mergeAllNerGraphs(List<Graph> graphs){
        Graph mergedGraph = new ListenableDirectedGraph(NerEdge.class);
        for (Graph wordGraph : graphs) {
            mergedGraph = mergeNerGraphs(mergedGraph, wordGraph);
        }
        return mergedGraph;
    }

    public static Graph mergeNerGraphs(Graph graph1, Graph graph2){
        ListenableDirectedGraph result = null;
        if(result == null) {
            result = new ListenableDirectedGraph(RoleEdge.class);
            // Get all vertex from the graph1
            Iterator iterator = graph1.vertexSet().iterator();
            AddVertexesOfGraph(result, iterator);

            // Get all edges from graph1
            iterator = graph1.edgeSet().iterator();
            AddNerEdgesOfGraph(result, iterator);

            // Get all vertex from the graph2
            iterator = graph2.vertexSet().iterator();
            AddVertexesOfGraph(result, iterator);
            // Get all edges from graph2
            iterator = graph2.edgeSet().iterator();
            AddNerEdgesOfGraph(result, iterator);
        }

        return result;

    }

    private static void AddVertexesOfGraph(ListenableDirectedGraph result, Iterator iterator) {
        while (iterator.hasNext()) {
            Concept sourceVertex = (Concept) iterator.next();
            if (sourceVertex instanceof Concept) {
                result.addVertex(sourceVertex);
            }
        }
    }

    private static void AddSynEdgesOfGraph(ListenableDirectedGraph result, Iterator iterator) {
        while (iterator.hasNext())
        {
            Object sourceEdge = iterator.next();
            if (sourceEdge instanceof SyntaxEdge) {

                result.addEdge(((SyntaxEdge) sourceEdge).getSource(),((SyntaxEdge) sourceEdge).getTarget(), sourceEdge);

            }
        }
    }

    private static void AddRoleEdgesOfGraph(ListenableDirectedGraph result, Iterator iterator) {
        while (iterator.hasNext())
        {
            Object sourceEdge = iterator.next();
            if (sourceEdge instanceof RoleEdge) {

                result.addEdge(((RoleEdge) sourceEdge).getSource(),((RoleEdge) sourceEdge).getTarget(), sourceEdge);

            }
        }
    }

    private static void AddNerEdgesOfGraph(ListenableDirectedGraph result, Iterator iterator){
        while (iterator.hasNext())
        {
            Object sourceEdge = iterator.next();
            if (sourceEdge instanceof NerEdge) {

                result.addEdge(((NerEdge) sourceEdge).getSource(),((NerEdge) sourceEdge).getTarget(), sourceEdge);

            }
        }
    }
}
