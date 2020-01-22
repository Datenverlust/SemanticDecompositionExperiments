/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

package de.kimanufaktur.nsm.reasoning;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import de.kimanufaktur.markerpassing.*;
import de.kimanufaktur.nsm.decomposition.Concept;
import de.kimanufaktur.nsm.decomposition.Decomposition;
import de.kimanufaktur.nsm.decomposition.Definition;
import de.kimanufaktur.nsm.decomposition.WordType;
import de.kimanufaktur.nsm.graph.entities.links.*;
import org.jgrapht.Graph;

import java.lang.reflect.InvocationTargetException;
import java.util.*;


public class PathMarkerPassing extends SpreadingAlgorithm {
    public static int maximalRoundCount = 80;
    public static int maximalSpreadingSteps = 350;
    int pulsecount = 0;
    double currentDoubleActivation = 0.0D;
    public int nodeCount = 0;
    public int pathNodeCount = 0;
    public int roundCount = 0;
    public int abductiveReasoning = 0;
    public int activationOutputNotEmptyCount = 0;
    private List<InferenceCollision> inferenceCollisions = new ArrayList<>();
    public int correctAnswerNumber = -1;

    BiMap<Concept, Node> nodes = HashBiMap.create();
    List<Marker> originMarkerClasses = new ArrayList<>();
    private boolean bTerminate;
    public boolean bResultIsGuessed = false;
    public boolean bResultIsGuessedOfMultipleOptions = false;

    public int getPulsecount() {
        return pulsecount;
    }

    public void setPulsecount(int pulsecount) {
        this.pulsecount = pulsecount;
    }

    public List<Marker> getOriginMarkerClasses() {
        return this.originMarkerClasses;
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

    public <T extends PathNode> PathMarkerPassing(Graph graph, Map<Concept, Double> threshold, Class<T> nodeType) {
        HashSet<Node> activenodes = new HashSet();
        this.setActiveNodes(activenodes);
        HashSet<Node> firingnodes = new HashSet();
        this.setFiringNodes(firingnodes);
        this.fillNodes(graph, threshold, nodeType);
        TerminationCondition terminationCondition = getTerminationCriterion_v00();
        this.setTerminationCondition(terminationCondition);
        SelectFiringNodesFunction selectFiringNodesFunction = new SelectFiringNodesFunction() {
            public Collection<Node> compute(Collection<Node> list) {
                List<Node> firingNodes = new ArrayList();
                Iterator var3 = list.iterator();
                while (var3.hasNext()) {
                    PathNode pathNode = (PathNode) var3.next();
                    if (pathNode != null && pathNode.checkThresholds(PathMarkerPassing.this.originMarkerClasses)) {
                        firingNodes.add(pathNode);
                    }
                }
                if (firingNodes.size() == 0) {
                    bTerminate = true;
                    System.out.println("no firing nodes in round : " + roundCount);
                }
                roundCount++;
                return firingNodes;
            }
        };
        this.setSelectFiringNodes(selectFiringNodesFunction);
        InFunction inFunction = new InFunction() {
            public void compute(Collection<SpreadingStep> list, Node node) {
                if (node instanceof PathNode && list.size() > 0) {
                    ((PathNode) node).in(list);
                    PathMarkerPassing.this.getActiveNodes().add(node);
                }

            }
        };
        this.setIn(inFunction);
        OutFunction outFunction = new OutFunction() {
            public List<SpreadingStep> compute(Node node) {
                List<SpreadingStep> activationOutput = new ArrayList();
                nodeCount++;
                if (node instanceof PathNode) {
                    pathNodeCount++;
                    activationOutput.addAll(((PathNode) node).out());
                }
                if (activationOutput.size() > 0)
                    activationOutputNotEmptyCount++;
                return activationOutput;
            }
        };
        this.setOut(outFunction);
        List<ProcessingStep> preProcessing = new ArrayList();
        this.setPreprocessingSteps(preProcessing);
        List<ProcessingStep> postProcessing = new ArrayList();
        this.setPostprocessingSteps(postProcessing);

    }

    public static void doInitialMarking(List<Map<Concept, List<? extends Marker>>> startActivation, PathMarkerPassing PathMarkerPassingAlgo) {
        Iterator var2 = startActivation.iterator();
        while (var2.hasNext()) {
            Map<Concept, List<? extends Marker>> m = (Map) var2.next();
            Iterator var4 = m.entrySet().iterator();

            while (var4.hasNext()) {
                Map.Entry<Concept, List<? extends Marker>> e = (Map.Entry) var4.next();
                Iterator var6 = ((List) e.getValue()).iterator();
                while (var6.hasNext()) {
                    PathMarker marker = (PathMarker) var6.next();
                    PathMarkerPassingAlgo.addMarkerToNode((Concept) e.getKey(), marker);
                    PathMarkerPassingAlgo.getOriginMarkerClasses().add(marker);
                }
            }
        }
    }

    public BiMap<Concept, Node> getNodes() {
        return nodes;
    }

    /**
     * Fill the nodes for the activation spreading with the given graph. This graph is used as basis, for the
     * spreading activation. The graph is not altered, but its vertices are used to implement nodes for the spreading.
     *
     * @param graph     the graph to take the vertices and edges from. The vertices are concepts and the relations are links.
     * @param threshold the threshold to set for each node. TODO: have a sprecific threshold for each node type
     */
    public <T extends PathNode> void fillNodes(Graph graph, Map<Concept, Double> threshold, Class<T> nodeType) {

        for (Concept concept : (Set<Concept>) graph.vertexSet()) {
            if (Decomposition.getConcepts2Ignore().contains(concept)) {
                continue;
            } else {
                Node node = nodes.get(concept);
                if (node == null || nodes.inverse().get(node).getDecompositionElementCount() < concept.getDecompositionElementCount()) {
                    Node tmpnode = addConceptRecursivly(concept, threshold, nodeType);
                    nodes.put(concept, tmpnode);
                }
            }
        }
        return;
    }

    /**
     * add the concept to the nodes network recursively
     *
     * @param concept the concept to add.
     */
    private <T extends PathNode> T addConceptRecursivly(Concept concept, Map<Concept, Double> threshold, Class<T> nodeType) {
        if (Decomposition.getConcepts2Ignore().contains(concept)) {
            return null;
        } else {
            T node = (T) nodes.get(concept);
            if (node == null) {
                try {
                    node = nodeType.getDeclaredConstructor(Concept.class).newInstance(concept);//concept.nodeType.newInstance(); //new DoubleNodeWithMultipleThresholds(concept);

                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                ((T) node).setThreshold(threshold);
                nodes.put(concept, node);
            } else if (nodes.inverse().get(node).getDecompositionElementCount() > concept.getDecompositionElementCount()) {
                return node;
            }
            for (Concept syn : concept.getSynonyms()) {
                if (syn != null && !Decomposition.getConcepts2Ignore().contains(concept)) {
                    Node synNode = nodes.get(syn);
                    if (synNode == null) {
                        synNode = addConceptRecursivly(syn, threshold, nodeType); //TODO: have a specific threshold for synonyms
                    }
                    if (synNode != null) {
                        SynonymLink link = new SynonymLink();
                        link.setSource(node);
                        link.setTarget(synNode);
                        if (!node.getLinks().contains(link)) {
                            node.addLink(link);
                        }
                    }
                }
            }
            for (Concept hyper : concept.getHypernyms()) {
                if (hyper != null && !Decomposition.getConcepts2Ignore().contains(concept)) {
                    Node hyperNode = nodes.get(hyper);
                    if (hyperNode == null) {
                        hyperNode = addConceptRecursivly(hyper, threshold, nodeType); //TODO: have a specific threshold for hypernyms
                    }
                    if (hyperNode != null) {
                        HypernymLink link = new HypernymLink();
                        link.setSource(node);
                        link.setTarget(hyperNode);
                        if (!node.getLinks().contains(link)) {
                            node.addLink(link);
                        }
                    }
                }
            }
            for (Concept mero : concept.getMeronyms()) {
                if (mero != null && !Decomposition.getConcepts2Ignore().contains(concept)) {
                    Node meroNode = nodes.get(mero);
                    if (meroNode == null) {
                        meroNode = addConceptRecursivly(mero, threshold, nodeType); //TODO: have a specific threshold for hypernyms
                    }
                    if (meroNode != null) {
                        MeronymLink link = new MeronymLink();
                        link.setSource(node);
                        link.setTarget(meroNode);

                        if (!node.getLinks().contains(link)) {
                            node.addLink(link);
                        }
                    }
                }
            }
            for (Definition definition : concept.getDefinitions()) {
                for (Concept def : definition.getDefinition()) {
                    if (def != null && !Decomposition.getConcepts2Ignore().contains(concept)) {
                        Node defnode = nodes.get(def);
                        if (defnode == null && !Decomposition.getConcepts2Ignore().contains(def)) {
                            defnode = addConceptRecursivly(def, threshold, nodeType); //TODO: have a specific threshold for definitions
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
            for (Concept arbitraryRelation : concept.getArbitraryRelations()) {
                if (arbitraryRelation != null) {
                    Node relatedNode = nodes.get(arbitraryRelation);
                    if (relatedNode == null) {
                        relatedNode = addConceptRecursivly(arbitraryRelation, threshold, nodeType); //TODO: have a specific threshold for arbitrary relationships
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
            //TODO: as hyponyms,antonyms and arbitrary relations are not considered

            for (Concept hypo : concept.getHyponyms()) {
                if (hypo != null && !Decomposition.getConcepts2Ignore().contains(concept)) {
                    Node hypoNode = nodes.get(hypo);
                    if (hypoNode == null) {
                        hypoNode = addConceptRecursivly(hypo, threshold, nodeType); //TODO: have a specific threshold for hyponym
                    }
                    if (hypoNode != null) {
                        HyponymLink link = new HyponymLink();
                        link.setSource(node);
                        link.setTarget(hypoNode);
                        if (!node.getLinks().contains(link)) {
                            node.addLink(link);
                        }
                    }
                }
            }

            for (Concept antonym : concept.getAntonyms()) {
                if (antonym != null) {
                    Node antoNode = nodes.get(antonym);
                    if (antoNode == null) {
                        antoNode = addConceptRecursivly(antonym, threshold, nodeType); //TODO: have a specific threshold for hypernyms
                    }
                    AntonymLink link = new AntonymLink();
                    link.setSource(node);
                    link.setTarget(antoNode);
                    if (!node.getLinks().contains(link)) {
                        node.addLink(link);
                    }
                }
            }


            return node;
        }
    }

    /**
     * Example implementation of the Node interface for nodes which have a double threshold.
     */
    public void addMarkerToNode(Concept concept2Activate, PathMarker activationMarker) {
        PathNode node2activate = (PathNode) nodes.get(concept2Activate);
        node2activate.getMarkers().add(activationMarker);
        //Todo: changed activation concept
        node2activate.addActivation_v2(concept2Activate, activationMarker.getActivation());
        getActiveNodes().add(node2activate);
    }

    private TerminationCondition getTerminationCriterion() {
        TerminationCondition res = new TerminationCondition() {
            //there is no meaningful inference left
            //for each node:
            //  for each marker on node:
            //      no inference possible => node.activation = 0
            //      else                  => node.activation = 1
            public boolean compute() {
                double lastDoubleActivation = PathMarkerPassing.this.currentDoubleActivation;
                PathMarkerPassing.this.currentDoubleActivation = 0.0D;
                Iterator var3 = PathMarkerPassing.this.getActiveNodes().iterator();
                while (var3.hasNext()) {
                    Node activeNode = (Node) var3.next();
                    if (activeNode instanceof PathNode) {
                        PathMarkerPassing.this.currentDoubleActivation += ((PathNode) activeNode).getDoubleActivation();
                    }
                }
                if (PathMarkerPassing.this.currentDoubleActivation <= 1.0D) {
                    return true;
                } else {
                    return ++PathMarkerPassing.this.pulsecount >= PathMarkerPassingConfig.getTerminationPulsCount();
                }
            }
        };
        return res;
    }

    private TerminationCondition getTerminationCriterion_v1() {
        TerminationCondition res = new TerminationCondition() {
            public boolean compute() {
                if (bTerminate || roundCount >= PathMarkerPassingConfig.getTerminationPulsCount())
                    return true;
                else
                    for (Node node : getActiveNodes())
                        if (!((PathNode) node).getNodeTermination())
                            return false;
                return true;
            }
        };
        return res;
    }

    private TerminationCondition getTerminationCriterion_v00() {
        TerminationCondition res = new TerminationCondition() {
            public boolean compute() {
                for (Node node : getActiveNodes())
                    if (!((PathNode) node).inferenceCollisions.isEmpty())
                        return true;
                if (bTerminate || roundCount >= PathMarkerPassingConfig.getTerminationPulsCount())
                    return true;
                return false;
            }
        };
        return res;
    }

    private TerminationCondition getTerminationCriterion_v2() {
        TerminationCondition res = new TerminationCondition() {
            //stop after first meaningful inference is made...
            public boolean compute() {
                Iterator var3 = PathMarkerPassing.this.getActiveNodes().iterator();
                if (bTerminate) {
                    return true;
                }
                while (var3.hasNext()) {
                    Node activeNode = (Node) var3.next();
                    if (activeNode instanceof PathNode) {
                        if (((PathNode) activeNode).getNodeTermination()) {
                            return true;
                        }
                    }
                }
                return false;
            }
        };
        return res;
    }

    public int getCorrectAnswerNummer() {
        int answernumber = -1;
        ArrayList<Integer> counter;
        Integer[] inferenceOnAnswers = new Integer[]{0, 0, 0, 0};
        int tmpCnt;
        int mostAnswers = 0;

        for (Node node : getNodes().values()) {
            for (InferenceCollision colli : ((PathNode) node).getInferenceCollisions()) {
                if (colli.getAnswerNumber() > 0) {
                    inferenceCollisions.add(colli);
                    tmpCnt = inferenceOnAnswers[colli.getAnswerNumber() - 1] + 1;
                    if (PathMarkerPassingConfig.bAbductiveInference) {
                        if (colli.containsWhiteListLink()) {
                            tmpCnt = inferenceOnAnswers[colli.getAnswerNumber() - 1] + 1;
                            abductiveReasoning++;
                        }
                    }
                    inferenceOnAnswers[colli.getAnswerNumber() - 1] = tmpCnt;
                }
            }
        }

        counter = new ArrayList<>(Arrays.asList(inferenceOnAnswers));
        mostAnswers = Collections.max(counter);

        if (mostAnswers > 0) {
            answernumber = counter.indexOf(mostAnswers) + 1;

            if (counter.lastIndexOf(mostAnswers) != counter.indexOf(mostAnswers)) {
                //TODO: vernünftige auswertung
                answernumber = (int) Math.round((Math.random() + counter.lastIndexOf(mostAnswers) - counter.indexOf(mostAnswers)));
                bResultIsGuessedOfMultipleOptions = true;
            }
        }

        if (answernumber == -1) {
            bResultIsGuessed = true;
            answernumber = (int) Math.round((Math.random() * 4));
        }
        this.correctAnswerNumber = answernumber;
        return answernumber;
    }

    public int getCorrectAnswerNummer_v01() {
        int answernumber = -1;
        List<Integer> counter;

        List<Integer> conceptsAnswered = Arrays.asList(0,0,0,0);
        List<Integer> inferencesOnAnswer = Arrays.asList(0,0,0,0);
        List<Integer> totalInferences = Arrays.asList(0,0,0,0);
        int tmpCnt;
        int mostAnswers = 0;
        HashMap<Concept,List<Integer>> inferences = new HashMap<>();

        List<Concept> questionConcepts=new ArrayList<>();
        for (PathNode n: getQuestionNodes()){
            questionConcepts.add(n.getConcept());
            inferences.put(n.getConcept(),Arrays.asList(0,0,0,0));
        }

        Concept tmpConcept;
        int answerNo;
        int previousAnswers;

        for (Node node : getNodes().values()) {
            for (InferenceCollision colli : ((PathNode) node).getInferenceCollisions()) {
                if (colli.getAnswerNumber() > 0 ) {
                    if(!PathMarkerPassing.matchingWordType(colli.getQuestionConcept().getWordType()))
                        break;
                    inferenceCollisions.add(colli);
                    tmpConcept=colli.getQuestionConcept();
                    answerNo=colli.getAnswerNumber();

                    inferencesOnAnswer=inferences.get(tmpConcept);
                    previousAnswers=inferencesOnAnswer.get(answerNo-1);

                    if(previousAnswers==0)
                        conceptsAnswered.set(answerNo-1,conceptsAnswered.get(answerNo-1)+1);

                    tmpCnt = previousAnswers +  1;

                    if (PathMarkerPassingConfig.bAbductiveInference) {
                        if (colli.containsWhiteListLink()) {
                            tmpCnt=+1;
                            conceptsAnswered.set(answerNo-1,conceptsAnswered.get(answerNo-1)+1);
                            abductiveReasoning++;
                        }
                    }
                    inferencesOnAnswer.set(answerNo-1,tmpCnt);
                    totalInferences.set(answerNo-1,totalInferences.get(answerNo-1)+tmpCnt);
                    inferences.put(tmpConcept,inferencesOnAnswer);
                }
            }
        }

        counter = new ArrayList<>(conceptsAnswered);
        mostAnswers = Collections.max(counter);

        if (mostAnswers > 0) {
            answernumber = counter.indexOf(mostAnswers) + 1;

            if (counter.lastIndexOf(mostAnswers) != counter.indexOf(mostAnswers)) {
                //TODO: vernünftige auswertung -> chose stronger / connection
                List<Integer> scores = Arrays.asList(0,0,0,0);
                for (int i=0; i < 4 ; i++){
                    if (counter.get(i)==mostAnswers){
                        scores.set(i,totalInferences.get(i));
                    }
                }
                mostAnswers=Collections.max(scores);
                answernumber=scores.indexOf(mostAnswers)+1;
                bResultIsGuessedOfMultipleOptions = true;
            }
        }

        if (answernumber == -1) {
            bResultIsGuessed = true;
            answernumber = (int) Math.round((Math.random() * 4));
        }
        this.correctAnswerNumber = answernumber;
        return answernumber;
    }

    public int getCorrectAnswerNummer_v02() {
        int answernumber = -1;
        List<Integer> counter;

        List<Integer> conceptsAnswered = Arrays.asList(0,0,0,0);
        List<Integer> inferencesOnAnswer = Arrays.asList(0,0,0,0);
        List<Integer> totalInferences = Arrays.asList(0,0,0,0);
        int tmpCnt;
        int mostAnswers = 0;
        HashMap<Concept,List<Integer>> inferences = new HashMap<>();

        List<Concept> questionConcepts=new ArrayList<>();
        for (PathNode n: getQuestionNodes()){
            questionConcepts.add(n.getConcept());
            inferences.put(n.getConcept(),Arrays.asList(0,0,0,0));
        }

        Concept tmpConcept;
        int answerNo;
        int previousAnswers;

        for (Node node : getNodes().values()) {
            for (InferenceCollision colli : ((PathNode) node).getInferenceCollisions()) {
                if (colli.getAnswerNumber() > 0) {

                    inferenceCollisions.add(colli);
                    tmpConcept=colli.getQuestionConcept();
                    answerNo=colli.getAnswerNumber();

                    inferencesOnAnswer=inferences.get(tmpConcept);
                    previousAnswers=inferencesOnAnswer.get(answerNo-1);

                    if(previousAnswers==0)
                        conceptsAnswered.set(answerNo-1,conceptsAnswered.get(answerNo-1)+1);

                    tmpCnt = previousAnswers +  1;

                    if (PathMarkerPassingConfig.bAbductiveInference) {
                        if (colli.containsWhiteListLink()) {
                            tmpCnt=+1;
                            conceptsAnswered.set(answerNo-1,conceptsAnswered.get(answerNo-1)+1);
                            abductiveReasoning++;
                        }
                    }
                    inferencesOnAnswer.set(answerNo-1,tmpCnt);
                    totalInferences.set(answerNo-1,totalInferences.get(answerNo-1)+tmpCnt);
                    inferences.put(tmpConcept,inferencesOnAnswer);
                }
            }
        }

        counter = new ArrayList<>(totalInferences);
        mostAnswers = Collections.max(counter);

        if (mostAnswers > 0) {
            answernumber = counter.indexOf(mostAnswers) + 1;

            if (counter.lastIndexOf(mostAnswers) != counter.indexOf(mostAnswers)) {
                //TODO: vernünftige auswertung -> chose stronger / connection
                List<Integer> scores = Arrays.asList(0,0,0,0);
                for (int i=0; i < 4 ; i++){
                    if (counter.get(i)==mostAnswers){
                        scores.set(i,inferencesOnAnswer.get(i));
                    }
                }
                mostAnswers=Collections.max(scores);
                answernumber=scores.indexOf(mostAnswers)+1;
                bResultIsGuessedOfMultipleOptions = true;
            }
        }

        if (answernumber == -1) {
            bResultIsGuessed = true;
            answernumber = (int) Math.round((Math.random() * 4));
        }
        this.correctAnswerNumber = answernumber;
        return answernumber;
    }

    public int getCorrectAnswerNummer_v04() {
        int answernumber = -1;
        List<Integer> counter;

        List<Integer> conceptsAnswered = Arrays.asList(0,0,0,0);
        List<Integer> inferencesOnAnswer = Arrays.asList(0,0,0,0);
        List<Integer> totalInferences = Arrays.asList(0,0,0,0);
        int tmpCnt;
        int mostAnswers = 0;
        HashMap<Concept,List<Integer>> inferences = new HashMap<>();

        List<Concept> questionConcepts=new ArrayList<>();
        for (PathNode n: getQuestionNodes()){
            questionConcepts.add(n.getConcept());
            inferences.put(n.getConcept(),Arrays.asList(0,0,0,0));
        }

        Concept tmpConcept;
        int answerNo;
        int previousAnswers;

        for (Node node : getNodes().values()) {
            for (InferenceCollision colli : ((PathNode) node).getInferenceCollisions()) {
                if (colli.getAnswerNumber() > 0 ) {
                    if(!PathMarkerPassing.matchingWordType(colli.getQuestionConcept().getWordType())
                            || !colli.getQuestionConcept().getWordType().equals(colli.getAnswerConcept().getWordType()))
                        break;
                    inferenceCollisions.add(colli);
                    tmpConcept=colli.getQuestionConcept();
                    answerNo=colli.getAnswerNumber();

                    inferencesOnAnswer=inferences.get(tmpConcept);
                    previousAnswers=inferencesOnAnswer.get(answerNo-1);

                    if(previousAnswers==0)
                        conceptsAnswered.set(answerNo-1,conceptsAnswered.get(answerNo-1)+1);

                    tmpCnt = previousAnswers +  1;

                    if (PathMarkerPassingConfig.bAbductiveInference) {
                        if (colli.containsWhiteListLink()) {
                            tmpCnt=+1;
                            conceptsAnswered.set(answerNo-1,conceptsAnswered.get(answerNo-1)+1);
                            abductiveReasoning++;
                        }
                    }
                    inferencesOnAnswer.set(answerNo-1,tmpCnt);
                    totalInferences.set(answerNo-1,totalInferences.get(answerNo-1)+tmpCnt);
                    inferences.put(tmpConcept,inferencesOnAnswer);
                }
            }
        }

        counter = new ArrayList<>(conceptsAnswered);
        mostAnswers = Collections.max(counter);

        if (mostAnswers > 0) {
            answernumber = counter.indexOf(mostAnswers) + 1;

            if (counter.lastIndexOf(mostAnswers) != counter.indexOf(mostAnswers)) {
                //TODO: vernünftige auswertung -> chose stronger / connection
                List<Integer> scores = Arrays.asList(0,0,0,0);
                for (int i=0; i < 4 ; i++){
                    if (counter.get(i)==mostAnswers){
                        scores.set(i,totalInferences.get(i));
                    }
                }
                mostAnswers=Collections.max(scores);
                answernumber=scores.indexOf(mostAnswers)+1;
                bResultIsGuessedOfMultipleOptions = true;
            }
        }

        if (answernumber == -1) {
            bResultIsGuessed = true;
            answernumber = (int) Math.round((Math.random() * 4));
        }
        this.correctAnswerNumber = answernumber;
        return answernumber;
    }

    public int getCorrectAnswerNummer_v05() {
        int answernumber = -1;
        HashMap<Concept,List<Integer>> inferences = new HashMap<>();
        InferenceCollision bestColli=null;

        List<Concept> questionConcepts=new ArrayList<>();
        for (PathNode n: getQuestionNodes()){
            questionConcepts.add(n.getConcept());
            inferences.put(n.getConcept(),Arrays.asList(0,0,0,0));
        }

        for (Node node : getNodes().values()) {
            for (InferenceCollision colli : ((PathNode) node).getInferenceCollisions()) {
                if (colli.getAnswerNumber() > 0 ) {
                    if(!PathMarkerPassing.matchingWordType(colli.getQuestionConcept().getWordType())
                            || !colli.getQuestionConcept().getWordType().equals(colli.getAnswerConcept().getWordType()))
                        break;
                    inferenceCollisions.add(colli);
                    if(inferenceCollisions.size()==1){
                        bestColli=colli;
                    }
                    if(colli.compare(bestColli))
                        bestColli=colli;
                    abductiveReasoning++;
                }
            }
        }

        if (inferenceCollisions.size()==0) {
            bResultIsGuessed = true;
            answernumber = (int) Math.round((Math.random() * 4));
        }
        else{
            answernumber=bestColli.getAnswerNumber();
            System.out.print("best Inference: ");
            System.out.println(bestColli.toString());
        }
        this.correctAnswerNumber = answernumber;
        return answernumber;
    }

    public int getCorrectAnswerNumber_v06(){
        int answernumber = -1;
        HashMap<Concept,List<Integer>> inferences = new HashMap<>();
        ArrayList<Integer> answers = new ArrayList<>(Arrays.asList(0,0,0,0));

        List<Concept> questionConcepts=new ArrayList<>();
        for (PathNode n: getQuestionNodes()){
            questionConcepts.add(n.getConcept());
            inferences.put(n.getConcept(),Arrays.asList(0,0,0,0));
        }

        for (Node node : getNodes().values()) {
            for (InferenceCollision colli : ((PathNode) node).getInferenceCollisions()) {
                if (colli.getAnswerNumber() > 0 ) {
                    if(!PathMarkerPassing.matchingWordType(colli.getQuestionConcept().getWordType())
                            || !colli.getQuestionConcept().getWordType().equals(colli.getAnswerConcept().getWordType()))
                        break;
                    if (!colli.containsWhiteListLink())
                        break;
                    abductiveReasoning++;
                    inferenceCollisions.add(colli);
                    //choose answer that is involved in the most inferences
                    answers.set(colli.getAnswerNumber()-1,answers.get(colli.getAnswerNumber()-1)+1);
                }
            }
        }

        if (inferenceCollisions.size()==0) {
            bResultIsGuessed = true;
            answernumber = (int) Math.round((Math.random() * 4));
        }
        else if (inferenceCollisions.size()>=1){
            answernumber=answers.indexOf(Collections.max(answers)) +1 ;
        }
        this.correctAnswerNumber = answernumber;
        return answernumber;
    }

    /**
     * @return answer with highest average specifity (inferenceSpecifity/#inferences)
     */
    public int getCorrectAnswerNumber_v07(){
        int answernumber = -1;
        double tmpSpecifity;
        ArrayList<Double> answerSpecifity = new ArrayList<>(Arrays.asList(0.0,0.0,0.0,0.0));
        ArrayList<Integer> answerHits = new ArrayList<>(Arrays.asList(0,0,0,0));

        for (Node node : getNodes().values()) {
            for (InferenceCollision colli : ((PathNode) node).getInferenceCollisions()) {
                if (colli.getAnswerNumber() > 0 ) {
                    if(!PathMarkerPassing.matchingWordType(colli.getQuestionConcept().getWordType())
                            || !colli.getQuestionConcept().getWordType().equals(colli.getAnswerConcept().getWordType()))
                        break;
                    if (!colli.containsWhiteListLink())
                        break;
                    inferenceCollisions.add(colli);
                    abductiveReasoning++;
                    //choose answer that has the highest average specifity
                    answerHits.set(colli.getAnswerNumber()-1,answerHits.get(colli.getAnswerNumber()-1)+1);
                    tmpSpecifity = colli.getSpecificity();
                    tmpSpecifity += answerSpecifity.get(colli.getAnswerNumber()-1);
                    answerSpecifity.set(colli.getAnswerNumber()-1,tmpSpecifity);
                }
            }
        }

        if (inferenceCollisions.size()==0) {
            bResultIsGuessed = true;
            answernumber = (int) Math.round((Math.random() * 4));
        }
        else if (inferenceCollisions.size()>=1){
            for(int i=0;i<4;i++){
                if(answerHits.get(i)>=1)
                    answerSpecifity.set(i,answerSpecifity.get(i)/answerHits.get(i));
            }
            answernumber=answerSpecifity.indexOf(Collections.max(answerSpecifity))+1;
        }
        this.correctAnswerNumber = answernumber;
        return answernumber;
    }

    /**
     * @return answer with highest single highest specifity
     */
    public int getCorrectAnswerNumber_v07_1(){
        int answernumber = -1;
        InferenceCollision bestColli=null;

        for (Node node : getNodes().values()) {
            for (InferenceCollision colli : ((PathNode) node).getInferenceCollisions()) {
                if (colli.getAnswerNumber() > 0 ) {
                    if(!PathMarkerPassing.matchingWordType(colli.getQuestionConcept().getWordType())
                            || !colli.getQuestionConcept().getWordType().equals(colli.getAnswerConcept().getWordType()))
                        break;
                    if (!colli.containsWhiteListLink())
                        break;
                    inferenceCollisions.add(colli);
                    if(inferenceCollisions.size()==1){
                        bestColli=colli;
                    }
                    if(colli.compare_v7(bestColli))
                        bestColli=colli;
                    abductiveReasoning++;
                }
            }
        }

        if (inferenceCollisions.size()==0) {
            bResultIsGuessed = true;
            answernumber = (int) Math.round((Math.random() * 4));
        }
        else if (inferenceCollisions.size()>=1){
            answernumber=bestColli.getAnswerNumber();
        }
        this.correctAnswerNumber = answernumber;
        return answernumber;
    }

    public static boolean matchingWordType(WordType wordType) {
        return wordType.type().toString().toUpperCase().equals("NOUN") ;
    }

    public String getCorrectAnswer() {
        for (InferenceCollision colli : inferenceCollisions) {
            if (colli.getAnswerNumber() == correctAnswerNumber)
                return colli.getAnswerString();
        }
        if (bResultIsGuessed) {
            for (Node node : getNodes().values()) {
                for (Marker marker : node.getMarkers()) {
                    if (((PathMarker) marker).startsAtAnswer() && ((PathMarker) marker).getAnswerNo() == this.correctAnswerNumber)
                        return ((PathMarker) marker).origin.getLitheral();
                }
            }
        }
        return "";
    }

    public void setQuestionNodes() {
        for (Marker m : this.getOriginMarkerClasses())
            if (m instanceof PathMarker) {
                if (!((PathMarker) m).startsAtAnswer()) {
                    ((PathNode) nodes.get(((PathMarker) m).origin)).setQuestionNode(true);
                }
            }
    }

    public List<PathNode> getQuestionNodes() {
        List<PathNode> res = new ArrayList<>();
        for (Marker m : this.getOriginMarkerClasses()) {
            if (m instanceof PathMarker) {
                if (!((PathMarker) m).startsAtAnswer()) {
                    res.add(((PathNode) nodes.get(((PathMarker) m).origin)));
                }
            }
        }
        return res;
    }

    public String printInferences(){
        StringBuilder res=new StringBuilder();
        for(InferenceCollision colli :inferenceCollisions){
            res.append("inferenz: " + colli.toString() + "\n");
        }
        return res.toString();
    }
}

