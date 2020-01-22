/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

package de.kimanufaktur.nsm.tsdr;

import de.kimanufaktur.nsm.decomposition.Concept;
import de.kimanufaktur.nsm.decomposition.Decomposition;
import de.kimanufaktur.nsm.decomposition.WordType;
import de.kimanufaktur.nsm.decomposition.graph.conceptCache.GraphUtil;
import org.jgrapht.Graph;

import java.util.*;

public class DocumentModel {

    private int docID;

    private String textBody;

    private String headline;

    private String onlineHeadline;

    private List<KeyphraseScorePair> keyphraseScoreList;

    private List<ConceptScorePair> topicConceptList;

    private Graph topicGraph;


    public DocumentModel (int docID) {
        this.docID = docID;
        this.keyphraseScoreList = new ArrayList<>();
        this.topicConceptList = new ArrayList<>();
        this.topicGraph = null;
    }

    public int getDocID() {
        return docID;
    }

    public String getTextBody() {
        return textBody;
    }

    public void setTextBody(String textBody) {
        this.textBody = textBody;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getOnlineHeadline() {
        return onlineHeadline;
    }

    public void setOnlineHeadline(String onlineHeadline) {
        this.onlineHeadline = onlineHeadline;
    }

    public void addKeyphraseScorePair (String keyphrase, Double score) {
        try {
            this.keyphraseScoreList.add(new KeyphraseScorePair(keyphrase, score));
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void addKeyphraseScorePair (String keyphrase, String score) {
        try {
            this.keyphraseScoreList.add(new KeyphraseScorePair(keyphrase, Double.parseDouble(score)));
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void addConceptScorePair(Concept concept, Double score) {
        try {
            this.topicConceptList.add(new ConceptScorePair(concept, score));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<KeyphraseScorePair> getKeyphraseScoreList() {
        return keyphraseScoreList;
    }

    public List<ConceptScorePair> getTopicConceptList() {
        return this.topicConceptList;
    }

    public Graph getTopicGraph() {
        return topicGraph;
    }

    public void setTopicGraph(Graph topicGraph) {
        this.topicGraph = topicGraph;
    }


    Graph buildTopicGraph(int decompositionDepth) {

        if (this.topicGraph == null) {
            Logger.outLn("Start building topic graph...");

            Graph graph = null;
            Graph g = null;

            for (KeyphraseScorePair ksp : this.getKeyphraseScoreList()) {

                for (String keyword : ksp.getKeyphrase().split(" ")) {

//                    Logger.outLn("Get graph for: " + keyword);

                    try {
                        g = GraphUtil.getGraph(keyword, WordType.UNKNOWN, decompositionDepth);

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

            }

            Logger.outLn("Finished building topic graph for docID: " + this.getDocID());
            this.topicGraph = graph;
        }

        return this.topicGraph;
    }


    List<ConceptScorePair> buildTopicConcepts() {

        if (this.topicGraph != null) {
            for (KeyphraseScorePair ksp : this.getKeyphraseScoreList()) {

                for (String keyword : ksp.getKeyphrase().split(" ")) {
                    Concept concept = GraphUtil.getConceptFromGraph(this.topicGraph, keyword);
                    addConceptScorePair(concept, ksp.getScore());

                }
            }
        }

        return this.topicConceptList;
    }


}

