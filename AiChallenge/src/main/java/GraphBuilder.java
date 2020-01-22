/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

import de.kimanufaktur.nsm.decomposition.Concept;
import de.kimanufaktur.nsm.decomposition.Decomposition;
import de.kimanufaktur.nsm.decomposition.Definition;
import de.kimanufaktur.nsm.decomposition.graph.conceptCache.GraphUtil;
import de.kimanufaktur.nsm.decomposition.graph.edges.WeightedEdge;
import de.kimanufaktur.nsm.decomposition.graph.spreadingActivation.MarkerPassing.MarkerPassingConfig;
import edges.NerEdge;
import edges.RoleEdge;
import edges.SyntaxEdge;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.util.CoreMap;
import org.jgrapht.Graph;
import org.jgrapht.graph.ListenableDirectedGraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GraphBuilder {



    public static List<List<SemanticGraphEdge>> getSyntactics(Annotation document) {
        List<List<SemanticGraphEdge>> edgeList = new ArrayList<>();
        List<SemanticGraph> synGraphs = new ArrayList<>();

        //Erstellt für jeden Satz aus Schema einen semantischen Graphen
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
        for (CoreMap sentence : sentences) {
            synGraphs.add(sentence.get(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class));
        }

        //Sammelt die Edges eines jeden Graphen in einer Liste von Listen
        for (SemanticGraph dependencies : synGraphs) {
            List<SemanticGraphEdge> edges = dependencies.edgeListSorted();
            edgeList.add(edges);
        }

        return edgeList;
    }

    public static List<String> excludedAttribute(List<List<SemanticGraphEdge>> edgeList, List<String> answerList, QuestionConcept pron) {
        List<SemanticGraphEdge> edges = edgeList.get(pron.getSatzNr());
        List<String> toExclude = new ArrayList<>();
        for (SemanticGraphEdge edge : edges) {
            String[] name = edge.getRelation().getShortName().split(("[\\p{Punct}]+"));
            String relationName = name[0];
            if (relationName.equals("prep")) {
                IndexedWord source = edge.getSource();
                IndexedWord target = edge.getTarget();
                if (source.index() - 1 == pron.getWortNr() || target.index() - 1 == pron.getWortNr()) {
                    for (String answer : answerList) {
                        Definition ansDef = new Definition(answer);
                        for (Concept ansCon : ansDef.getDefinition()) {
                            if (target.lemma().equals(ansCon.getLemma()) || source.lemma().equals(ansCon.getLemma())) {
                                toExclude.add(answer);
                                System.out.println("EXCLUDED ANSWER: " + answer);
                            }
                        }
                    }
                }
            }
        }

        return toExclude;
    }

    public static Graph getSyntaxGraph(List<List<SemanticGraphEdge>> edgeList, QuestionConcept concept) {

        List<Graph> graphs = synEdgesToGraphs(edgeList,concept);

        Graph mergedGraph = WinogradGraphUtil.mergeAllSynGraphs(graphs);

        return mergedGraph;
    }

    public static List<Graph> synEdgesToGraphs(List<List<SemanticGraphEdge>> edgeList, QuestionConcept pron){
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

    public static Map<String, String> getNerMap(Annotation document) {
        Map<String, String> nerMap = new HashMap<>();
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
        for (CoreMap sentence : sentences) {
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                String ne = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
                if (!ne.equals("O")) {
                    ne = ne.toLowerCase();
                    nerMap.put(token.lemma(), ne);
                }
            }
        }
        return nerMap;
    }

    public static Graph getNerGraph(Map<String, String> nerMap) {
        List<Graph> graphs = new ArrayList<>();
        for (String word : nerMap.keySet()) {
            Concept source = new Concept(word);
            String lemma = nerMap.get(word);

            Definition targetdef = new Definition(lemma);
            for (Concept target : targetdef.getDefinition()) {
                //graphs.add(GraphUtil.getGraph(targetcon.getLitheral(), targetcon.getWordType(), MarkerPassingConfig.getDecompositionDepth()));
                ListenableDirectedGraph graph = new ListenableDirectedGraph(WeightedEdge.class);
                graph.addVertex(source);
                graph.addVertex(target);
                NerEdge nerEdge = new NerEdge();
                nerEdge.setSource(source);
                nerEdge.setTarget(target);

                graph.addEdge(source, target, nerEdge);
                graphs.add(graph);
            }
        }
        Graph mergedGraph = WinogradGraphUtil.mergeAllNerGraphs(graphs);
        return mergedGraph;
    }

    public static Graph getRoleGraph(Map<List<Concept>, List<String>> roles) {
        List<Graph> graphList = new ArrayList<>();

        //Für jedes Argument von Prädikat
        for (List<Concept> phrase : roles.keySet()) {
            //Für jedes Wort in Argumentphrase
            for (Concept word : phrase) {
                if ((!Decomposition.getConcepts2Ignore().contains(word))) {
                    //Für jede für Argument vorgesehene Rolle
                    for (String role : roles.get(phrase)) {
                        Definition roleDef = new Definition(role);
                        //Für jedes Concept in Rollenbeschreibung
                        for (Concept roleCon : roleDef.getDefinition()) {
                            if ((!Decomposition.getConcepts2Ignore().contains(roleCon))) {
                                ListenableDirectedGraph graph = new ListenableDirectedGraph(RoleEdge.class);
                                graph.addVertex(word);
                                graph.addVertex(roleCon);
                                RoleEdge roleEdge = new RoleEdge();
                                if (roles.get(phrase).indexOf(role) == 0) {
                                    roleEdge.setRoleType("role");
                                } else {
                                    roleEdge.setRoleType("vnrole");
                                }
                                roleEdge.setSource(word);
                                roleEdge.setTarget(roleCon);
                                graph.addEdge(word, roleCon, roleEdge);
                                graphList.add(graph);
                            }
                        }
                    }

                }
            }
        }


        Graph mergedGraph = WinogradGraphUtil.mergeAllRoleGraphs(graphList);
        return mergedGraph;
    }

    public static Graph getDecGraph(List<List<String>> schema, Map<List<Concept>, List<String>> roleMap, Map<String, String> nerMap) {

        List<Graph> graphs = new ArrayList<>();

        for (List<String> sentence : schema) {
            for (String word : sentence) {
                Definition wordDef = new Definition(word);
                for (Concept wordCon : wordDef.getDefinition()) {
                    if (!Decomposition.getConcepts2Ignore().contains(wordCon) && !nerMap.containsKey(wordCon.getLitheral()) ) {
                        graphs.add(GraphUtil.getGraph(wordCon.getLitheral(), wordCon.getWordType(), MarkerPassingConfig.getDecompositionDepth()));
                    }
                }
            }
        }

        for (List<Concept> phrase : roleMap.keySet()) {
            List<String> roles = roleMap.get(phrase);
            for (String role : roles) {
                Definition roleDef = new Definition(role);
                for (Concept roleCon : roleDef.getDefinition()) {
                    if (!Decomposition.getConcepts2Ignore().contains(roleCon)) {
                        graphs.add(GraphUtil.getGraph(roleCon.getLitheral(), roleCon.getWordType(), MarkerPassingConfig.getDecompositionDepth()));
                    }
                }
            }
        }

        for (String name : nerMap.keySet()) {
            String entity = nerMap.get(name);
            Definition entDef = new Definition(entity);
            for (Concept entCon : entDef.getDefinition()) {
                graphs.add(GraphUtil.getGraph(entCon.getLitheral(), entCon.getWordType(), MarkerPassingConfig.getDecompositionDepth()));
            }
        }

        Graph mergedGraph = WinogradGraphUtil.mergeDecGraphs(graphs);
        return mergedGraph;
    }
}
