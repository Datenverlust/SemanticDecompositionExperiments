/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import de.dailab.nsm.decomposition.Concept;
import de.dailab.nsm.decomposition.Decomposition;
import de.dailab.nsm.decomposition.Definition;
import de.dailab.nsm.decomposition.graph.edges.WeightedEdge;
import de.dailab.nsm.decomposition.graph.entities.links.*;
import de.dailab.nsm.decomposition.graph.spreadingActivation.MarkerPassing.MarkerPassingConfig;
import de.tuberlin.spreadalgo.*;
import edges.NerEdge;
import edges.RoleEdge;
import edges.SyntaxEdge;
import links.NerLink;
import links.RoleLink;
import links.SyntaxLink;
import org.jgrapht.Graph;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Created by Hannes on 30.03.2017.
 */
public class WinogradDoubleMarkerPassing extends SpreadingAlgorithm{


    int pulsecount = 0;

    double currentDoubleActivation = 0.0;
    double maximalDoubleActivation = 0.0;

    Map<Concept, Double> doubleActivation = new HashMap<>();
    BiMap<Concept, Node> nodes = HashBiMap.create();
    List<Marker> originMarkerClasses = new ArrayList<>();

    public  <T extends Node> WinogradDoubleMarkerPassing(Graph graph, Class<T> nodeType, Graph syntaxGraph, Graph nerGraph, Graph roleGraph, MarkerPassingConfig config) {
        //set active nodes
        final HashSet<Node> activenodes = new HashSet<Node>();
        this.setActiveNodes(activenodes);
        //set firing nodes
        HashSet<Node> firingnodes = new HashSet<>();
        this.setFiringNodes(firingnodes);

        //fill node network with nodes;
        fillNodes(graph, nodeType);
        fillSyntax(syntaxGraph, nodeType);
        fillNerEdges(nerGraph,nodeType);
        fillRoleEdges(roleGraph,nodeType);

        setNegatives();



        //Create termination condition
        TerminationCondition terminationCondition = new TerminationCondition() {
            @Override
            public boolean compute() {
                double lastDoubleActivation = currentDoubleActivation;
                currentDoubleActivation = 0.0;

                for (Node activeNode : getActiveNodes()) {
                    if (activeNode instanceof WinogradDoubleNode) {
                        //TODO: Discuss getDoubleActivation is always 0.0 Is this intentioned?
                        currentDoubleActivation += ((WinogradDoubleNode) activeNode).getDoubleActivation();
                    }
                }
                if (currentDoubleActivation > maximalDoubleActivation) {
                    maximalDoubleActivation = currentDoubleActivation;
                }
                if (currentDoubleActivation >= config.getDoubleActivationLimit()) {
                    return true;
                } /*else if (lastDoubleActivation != 0.0 && lastDoubleActivation == currentDoubleActivation) {
                    return true;
                }*/ else {
                    return ++pulsecount >= config.getTerminationPulsCount();
                }


            }
        };
        this.setTerminationCondition(terminationCondition);


        //Create a SelectFiringNodesFunction which add all nodes which are over the threshold
        SelectFiringNodesFunction selectFiringNodesFunction = new SelectFiringNodesFunction() {
            @Override
            public Collection<Node> compute(Collection<Node> list) {
                List<Node> firingNodes = new ArrayList<>();
                for (Node node : list) {
                    if (node != null) {
                        if (node.
                                checkThresholds(originMarkerClasses)) {
                            firingNodes.add(node);
                        }
                    }
                }
                return firingNodes;
            }
        };
        this.setSelectFiringNodes(selectFiringNodesFunction);


        //Create in-function
        InFunction inFunction = new InFunction() {
            @Override
            public void compute(Collection<SpreadingStep> list, Node node) {
                if (node instanceof WinogradDoubleNode) {
                    ((WinogradDoubleNode) node).in(list);
                    getActiveNodes().add(node);
                }
                //Node has gotton input, so it is a active node. The decision if the node will fire in the
                //next pulse, depends on the selection in the SelectFiringNodes function.
            }
        };
        this.setIn(inFunction);
        //Create out-function
        OutFunction outFunction = new OutFunction() {
            @Override
            public List<SpreadingStep> compute(Node node) {
                List<SpreadingStep> activationOutput = new ArrayList<>();
                if (node instanceof WinogradDoubleNode) {
                    activationOutput.addAll(((WinogradDoubleNode) node).out());
                }
                return activationOutput;
            }
        };
        this.setOut(outFunction);

        //Create pre-processing steps
        List<ProcessingStep> preProcessing = new ArrayList<>();
        this.setPreprocessingSteps(preProcessing);

        //Create post-processing steps
        List<ProcessingStep> postProcessing = new ArrayList<>();
        this.setPostprocessingSteps(postProcessing);
    }

    public void setNegatives(){
        List<NegatedConcept> toNegate=new ArrayList<>();
        for(Concept con:nodes.keySet()){
            if(con instanceof NegatedConcept){
                toNegate.add((NegatedConcept) con);
            }
        }

        for(NegatedConcept neg:toNegate){
            Concept negCon=new Concept(neg.getOriginalName());
            Node node=nodes.get(negCon);
            List<Link> toAdd=new ArrayList<>();
            List<Link> toRemove=new ArrayList<>();
            List<Link> toReAdd=new ArrayList<>();
            for(Link link:node.getLinks()){
                if(link instanceof SynonymLink){
                    AntonymLink newLink=new AntonymLink();
                    newLink.setSource(link.getSource());
                    newLink.setTarget(link.getTarget());
                    AntonymLink newReLink=new AntonymLink();
                    newReLink.setSource(link.getTarget());
                    newReLink.setTarget(link.getSource());
                    toAdd.add(newLink);
                    toReAdd.add(newReLink);
                    toRemove.add(link);
                }
                else if(link instanceof AntonymLink){
                    SynonymLink newLink=new SynonymLink();
                    newLink.setSource(link.getSource());
                    newLink.setTarget(link.getTarget());
                    SynonymLink newReLink=new SynonymLink();
                    newReLink.setSource(link.getTarget());
                    newReLink.setTarget(link.getSource());
                    toAdd.add(newLink);
                    toReAdd.add(newReLink);
                    toRemove.add(link);
                }
            }
            for(Link remove:toRemove) {
                node.removeLink(remove);
                WinogradDoubleNode reNode = (WinogradDoubleNode) remove.getTarget();
                Concept reCon=reNode.getConcept();
                reNode=(WinogradDoubleNode) nodes.get(reCon);
                remove.setTarget(node);
                remove.setSource(reNode);
                if(reNode.getLinks().contains(remove))
                    reNode.removeLink(remove);
                nodes.put(reCon,reNode);
            }

            for(Link add:toAdd){
                node.addLink(add);
                WinogradDoubleNode reNode = (WinogradDoubleNode) add.getTarget();
                Concept reCon=reNode.getConcept();
                reNode=(WinogradDoubleNode) nodes.get(reCon);
                Link relink=null;
                if(add instanceof SynonymLink)
                    relink=new SynonymLink();
                if(add instanceof AntonymLink)
                    relink=new AntonymLink();

                relink.setTarget(node);
                relink.setSource(reNode);
                if(!reNode.getLinks().contains(relink))
                    reNode.addLink(relink);
                nodes.put(reCon,reNode);
            }
        }
        for(NegatedConcept neg: toNegate) {
            for(Link unused:nodes.get(neg).getLinks()){
                WinogradDoubleNode target=(WinogradDoubleNode) unused.getTarget();
                nodes.remove(target.getConcept());
            }
            nodes.remove(neg);
        }
    }

    public static void doInitialMarking(List<Map<Concept, List<? extends Marker>>> startActivation, WinogradDoubleMarkerPassing doubleMarkerPassingAlgo) {
        for (Map<Concept, List<? extends Marker>> m : startActivation) {
            for (Map.Entry<Concept, List<? extends Marker>> e : m.entrySet()) {

                // 4 testing
//            	System.out.print("doInitialMarking startActivation concept: " + e.getKey().getLitheral());

                for (Marker marker : e.getValue()) {
                    doubleMarkerPassingAlgo.addMarkerToNode(e.getKey(), marker);

                    // 4 testing
//                    DoubleMarkerWithOrigin tmp = (DoubleMarkerWithOrigin)marker;
//                    System.out.println("; set marker activation: " + tmp.getActivation());

                    //add start markers to threshold
                    doubleMarkerPassingAlgo.getOriginMarkerClasses().add(marker);
                }
            }
        }
        // 4 testing
//        List<Marker> tmpmarker = doubleMarkerPassingAlgo.getOriginMarkerClasses();
//        for(Marker asdf : tmpmarker)
//        	System.out.println("doInitialMarking getOriginMarkerClasses are: " + ((DoubleMarkerWithOrigin)asdf).getOrigin().getLitheral());

    }

    public double getCurrentDoubleActivation() {
        return currentDoubleActivation;
    }

    public List<Marker> getOriginMarkerClasses() {
        return this.originMarkerClasses;
    }

    public double getMaximalDoubleActivation() {
        return maximalDoubleActivation;
    }

    public void setMaximalDoubleActivation(double maximalDoubleActivation) {
        this.maximalDoubleActivation = maximalDoubleActivation;
    }

    public Map<Concept, Double> getDoubleActivation() {
        return doubleActivation;
    }

    public void setDoubleActivation(Map<Concept, Double> doubleActivation) {
        this.doubleActivation = doubleActivation;
    }

    public BiMap<Concept, Node> getNodes() {
        return nodes;
    }

    public int getPulsecount() {
        return pulsecount;
    }

    public void setPulsecount(int pulsecount) {
        this.pulsecount = pulsecount;
    }

    public Node getNodeForConcept(Concept concept) {
        return nodes.get(concept);
    }

    public Concept getConceptForNode(Node node) {
        for (Map.Entry entry : nodes.entrySet()) {
            if (entry.getValue().equals(node)) {
                return (Concept) entry.getKey();
            }
        }
        return null;
    }


    public <T extends Node> void fillNodes(Graph graph, Class<T> nodeType) {

        for (Concept concept : (Set<Concept>) graph.vertexSet()) {
            if (Decomposition.getConcepts2Ignore().contains(concept)) {
                continue;
            } else {
                Node node = nodes.get(concept);
                if (node == null || nodes.inverse().get(node).getDecompositionElementCount() < concept.getDecompositionElementCount()) {
                    Node tmpnode = addConceptRecursivly(concept, nodeType);
                    nodes.put(concept, tmpnode);
                }
            }
        }
        return;
    }

    public <T extends Node> void fillSyntax(Graph graph, Class<T> nodeType){
        for(SyntaxEdge edge:(Set<SyntaxEdge>) graph.edgeSet()){

            Concept sourceCon = (Concept) edge.getSource();
            Node source = nodes.get(sourceCon);
            if(source==null){
                source=addConcept(sourceCon, nodeType);
                nodes.put(sourceCon,source);
            }

            Concept targetCon = (Concept) edge.getTarget();
            Node target = nodes.get(targetCon);
            if(target==null){
                target=addConcept(targetCon,nodeType);
                nodes.put(targetCon,target);
            }

            SyntaxLink link=new SyntaxLink(edge.getRelationName());
            link.setSpecific(edge.getSpecific());
            link.setSource(source);
            link.setTarget(target);
            source.addLink(link);

            SyntaxLink relink = new SyntaxLink(edge.getRelationName());
            relink.setSpecific(edge.getSpecific());
            relink.setSource(target);
            relink.setTarget(source);
            target.addLink(relink);
        }
    }

    public <T extends Node> void fillNerEdges(Graph graph, Class<T> nodeType){
        for(NerEdge edge:(Set< NerEdge>) graph.edgeSet()){
            Concept sourceCon = (Concept) edge.getSource();
            Node source = nodes.get(sourceCon);
            Concept targetCon = (Concept) edge.getTarget();
            Node target = nodes.get(targetCon);
            if(source!=null && target!=null) {
                NerLink link = new NerLink();
                link.setSource(source);
                link.setTarget(target);
                source.addLink(link);
                NerLink relink = new NerLink();
                relink.setSource(target);
                relink.setTarget(source);
                target.addLink(relink);
            }
        }
    }

    public <T extends Node> void fillRoleEdges(Graph graph, Class<T> nodeType){
        for(RoleEdge edge: (Set<RoleEdge>) graph.edgeSet()){
            Concept sourceCon = (Concept) edge.getSource();
            Node source=nodes.get(sourceCon);
            Concept targetCon = (Concept) edge.getTarget();
            Node target=nodes.get(targetCon);
            if(source!=null && target!=null) {

                RoleLink link = new RoleLink(edge.getRoleType());
                link.setSource(source);
                link.setTarget(target);
                source.addLink(link);
                RoleLink relink = new RoleLink(edge.getRoleType());
                relink.setSource(target);
                relink.setTarget(source);
                target.addLink(relink);
            }
        }

    }

    private <T extends Node> Node addConcept(Concept concept, Class<T> nodeType){
        T node = (T) nodes.get(concept);
        if (node == null) {
            try {
                node =  nodeType.getDeclaredConstructor(Concept.class).newInstance(concept);//concept.nodeType.newInstance(); //new WinogradDoubleNode(concept);

            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            nodes.put(concept, node);
        } else if (nodes.inverse().get(node).getDecompositionElementCount() > concept.getDecompositionElementCount()) {
            return node;
        }

        return node;
    }
    /**
     * add the concept to the nodes network recursively
     *
     * @param concept the concept to add.
     */
    private <T extends Node> Node addConceptRecursivly(Concept concept, Class<T> nodeType) {

        if (Decomposition.getConcepts2Ignore().contains(concept)) {
            return null;
        } else {
            T node = (T) nodes.get(concept);
            if (node == null) {
                try {
                    node =  nodeType.getDeclaredConstructor(Concept.class).newInstance(concept);//concept.nodeType.newInstance(); //new WinogradDoubleNode(concept);

                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }

                nodes.put(concept, node);
            } else if (nodes.inverse().get(node).getDecompositionElementCount() > concept.getDecompositionElementCount()) {
                return node;
            }
            for (Concept syn : concept.getSynonyms()) {
                if (syn != null && !Decomposition.getConcepts2Ignore().contains(concept)) {
                    Node synNode = nodes.get(syn);
                    if (synNode == null) {
                        synNode = addConceptRecursivly(syn, nodeType);
                    }
                    if (synNode != null) {
                        SynonymLink link = new SynonymLink();
                        link.setSource(node);
                        link.setTarget(synNode);
                        if (!node.getLinks().contains(link)) {
                            node.addLink(link);
                        }
                        SynonymLink relink = new SynonymLink();
                        relink.setSource(synNode);
                        relink.setTarget(node);
                        if (!synNode.getLinks().contains(relink)) {
                            synNode.addLink(relink);
                        }
                    }
                }
            }

            for (Concept hypo : concept.getHyponyms()) {
                if (hypo != null && !Decomposition.getConcepts2Ignore().contains(concept)) {
                    Node hypoNode = nodes.get(hypo);
                    if (hypoNode == null) {
                        hypoNode = addConceptRecursivly(hypo, nodeType);
                    }
                    if (hypoNode != null) {
                        HyponymLink link = new HyponymLink();
                        link.setSource(node);
                        link.setTarget(hypoNode);
                        if (!node.getLinks().contains(link)) {
                            node.addLink(link);
                        }

                        HypernymLink relink = new HypernymLink();
                        relink.setSource(hypoNode);
                        link.setTarget(node);
                        if (!hypoNode.getLinks().contains(relink)) {
                            hypoNode.addLink(relink);
                        }
                    }
                }
            }
            for (Concept hyper : concept.getHypernyms()) {
                if (hyper != null && !Decomposition.getConcepts2Ignore().contains(concept)) {
                    Node hyperNode = nodes.get(hyper);
                    if (hyperNode == null) {
                        hyperNode = addConceptRecursivly(hyper, nodeType);
                    }
                    if (hyperNode != null) {
                        HypernymLink link = new HypernymLink();
                        link.setSource(node);
                        link.setTarget(hyperNode);
                        if (!node.getLinks().contains(link)) {
                            node.addLink(link);
                        }

                        HyponymLink relink = new HyponymLink();
                        relink.setSource(hyperNode);
                        relink.setTarget(node);
                        if (!hyperNode.getLinks().contains(relink)) {
                            hyperNode.addLink(relink);
                        }
                    }
                }
            }
            for (Definition definition : concept.getDefinitions()) {
                for (Concept def : definition.getDefinition()) {
                    if (def != null && !Decomposition.getConcepts2Ignore().contains(concept)) {
                        Node defnode = nodes.get(def);
                        if (defnode == null && !Decomposition.getConcepts2Ignore().contains(def)) {
                            defnode = addConceptRecursivly(def, nodeType);
                        }
                        if (defnode != null) {
                            DefinitionLink link = new DefinitionLink();
                            link.setSource(node);
                            link.setTarget(defnode);
                            if (!defnode.getLinks().contains(link)) {
                                node.addLink(link);
                            }
                        }
                    }
                }
            }
            for (Concept antonym : concept.getAntonyms()) {
                if (antonym != null) {
                    Node antoNode = nodes.get(antonym);
                    if (antoNode == null) {
                        antoNode = addConceptRecursivly(antonym, nodeType);
                    }
                    if(antoNode!=null) {
                        AntonymLink link = new AntonymLink();
                        link.setSource(node);
                        link.setTarget(antoNode);
                        if (!node.getLinks().contains(link)) {
                            node.addLink(link);
                        }

                        AntonymLink relink = new AntonymLink();
                        relink.setSource(antoNode);
                        relink.setTarget(node);
                        if (!antoNode.getLinks().contains(relink)) {
                            antoNode.addLink(relink);
                        }
                    }
                }
            }
            for (Concept arbitraryRelation : concept.getArbitraryRelations()) {
                if (arbitraryRelation != null) {
                    Node relatedNode = nodes.get(arbitraryRelation);
                    if (relatedNode == null) {
                        relatedNode = addConceptRecursivly(arbitraryRelation, nodeType);
                    }
                    ArbitraryRelationLink link = new ArbitraryRelationLink();
                    link.setSource(node);
                    link.setTarget(relatedNode);
                    link.setRelationName(arbitraryRelation.getOriginatedRelationName());
                    if (!node.getLinks().contains(link)) {
                        node.addLink(link);
                    }
                }
            }
            /*if(node instanceof WinogradDoubleNode) {
                //WinogradDoubleNode newNode = (WinogradDoubleNode) node;
                for (Link link : (Collection<Link>) node.getLinks()) {
                    WinogradDoubleNode source = (WinogradDoubleNode) link.getSource();
                    WinogradDoubleNode target = (WinogradDoubleNode) link.getTarget();
                    if (source.getConcept().equals("symbol") || target.getConcept().equals("symbol")) {
                        //System.out.println(source.getConcept() + " - " + target.getConcept());
                    }
                }
            }*/
            return node;

        }
    }

    /**
     * Example implementation of the Node interface for nodes which have a double threshold.
     */

    public void addMarkerToNode(Concept concept2Activate, Marker activationMarker) {
        //System.out.println(concept2Activate.getLitheral() +"...");
        //this is my hack "'s" solution, there could probably be something better
        if(!concept2Activate.equals("'s")&&concept2Activate.getLitheral().contains("_")){
            String newStr = concept2Activate.getLitheral().split("_")[0];
            System.out.println("NEW STRING: "+newStr);
            Concept concept = new Concept(newStr);
            Node node2activate = nodes.get(concept);
            node2activate.getMarkers().add(activationMarker);
            getActiveNodes().add(node2activate);
        }if(!concept2Activate.equals("'s")&&!concept2Activate.getLitheral().contains("_")){
            Node node2activate = nodes.get(concept2Activate);
            if(node2activate != null) {
                node2activate.getMarkers().add(activationMarker);
                getActiveNodes().add(node2activate);
            }
        }

    }

}


