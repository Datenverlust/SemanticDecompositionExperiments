/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

package de.kimanufaktur.nsm.tsdr;

import de.kimanufaktur.nsm.decomposition.graph.spreadingActivation.MarkerPassing.DoubleMarkerPassing;

import java.text.SimpleDateFormat;
import java.util.*;

public class DataHandler {

    private Map<Integer, DocumentModel> documentMap;
    private Map<Integer, TopicDocumentAssessment> fullTDAMap;
    private Map<Integer,TopicDocumentAssessment> expTDAMap;
    private List<List<String>> expStatistics;
    private String expName;

    DataHandler() {
        this.documentMap = new HashMap<>();
        this.fullTDAMap = new HashMap<>();
        this.expTDAMap = null;
        this.expStatistics = new ArrayList<>();
        this.expName = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
    }


    void init(Map<Integer, TopicDocumentAssessment> tdaMap) {
        this.fullTDAMap = tdaMap;
    }

    void selectRandExpDocIDs(int numTopics, int numIrrel, int numRel, int numHiRel) {

        this.expTDAMap = new HashMap<>();
        Random random = new Random();

        int topicCount = 0;
        int topicBound = fullTDAMap.keySet().size();
        while (topicCount < numTopics && topicCount < topicBound) {

            int currentTopicID = -1;
            while (currentTopicID < 0 || expTDAMap.containsKey(currentTopicID)) {
                currentTopicID = (Integer) fullTDAMap.keySet().toArray()[random.nextInt(topicBound)];
            }
            TopicDocumentAssessment currentTDA = fullTDAMap.get(currentTopicID);
            TopicDocumentAssessment newTDA = new TopicDocumentAssessment(currentTopicID);
            expTDAMap.put(currentTopicID, newTDA);

            newTDA.getIrrelevantDocs().addAll(getRandomDocIDSublist(currentTDA.getIrrelevantDocs(), numIrrel, random));
            newTDA.getRelevantDocs().addAll(getRandomDocIDSublist(currentTDA.getRelevantDocs(), numRel, random));
            newTDA.getHighlyRelevantDocs().addAll(getRandomDocIDSublist(currentTDA.getHighlyRelevantDocs(), numHiRel, random));

            topicCount++;
        }
    }

    void selectTestExpDocIDs(int topicID, int numIrrel, int numRel, int numHiRel) {
        this.expTDAMap = new HashMap<>();

        TopicDocumentAssessment expTDA = new TopicDocumentAssessment(topicID);
        TopicDocumentAssessment topicTDA = this.fullTDAMap.get(topicID);

        if (topicTDA != null) {
            Logger.outLn("--- topic ID: " + topicID);

            expTDA.getIrrelevantDocs().addAll(getFirstDocIDSublist(topicTDA.getIrrelevantDocs(), numIrrel));
            expTDA.getRelevantDocs().addAll(getFirstDocIDSublist(topicTDA.getRelevantDocs(), numRel));
            expTDA.getHighlyRelevantDocs().addAll(getFirstDocIDSublist(topicTDA.getHighlyRelevantDocs(), numHiRel));


        }

        this.expTDAMap.put(topicID, expTDA);
    }


    private List<Integer> getFirstDocIDSublist (List<Integer> docIDs, int numDocs) {
        List<Integer> docIDSublist = new ArrayList<>();

        int bound = docIDs.size();

        Logger.outLn("numDocs: " + numDocs + " / bound: " + bound);

        if (numDocs <= bound) {
            docIDSublist.addAll(docIDs.subList(0, numDocs));
        }
        else {
            docIDSublist.addAll(docIDs.subList(0, bound));
        }

        return docIDSublist;
    }


    private List<Integer> getRandomDocIDSublist (List<Integer> docIDs, int numDocs, Random random) {
        List<Integer> docIDSublist = new ArrayList<>();

        int count = 0;
        int bound = docIDs.size();
        while (count < numDocs && count < bound) {

            int currentDocID = -1;
            while (currentDocID < 0 || docIDSublist.contains(currentDocID)) {
                currentDocID = docIDs.get(random.nextInt(bound));
            }
            docIDSublist.add(currentDocID);

            count++;
        }

        return docIDSublist;
    }

    Collection<Integer> getAllExpDocIDs() {
        Set<Integer> allDocIDs = new HashSet<>();
        for (TopicDocumentAssessment tda: expTDAMap.values()) {
            allDocIDs.addAll(tda.getAllDocs());
        }
        return allDocIDs;
    }

    Map<Integer, DocumentModel> getDocumentMap() {
        return documentMap;
    }

    DocumentModel getDocModel (int docID) {
        return this.documentMap.get(docID);
    }

    Map<Integer, TopicDocumentAssessment> getExpTDAMap() {
        return this.expTDAMap;
    }

    void collectStatistics (int topicID, int referenceID, int candidateID, int docRelevance, List<Double> tsmResults) {
        List<String> newEntry = new ArrayList<>();

        DocumentModel referenceDocModel = this.documentMap.get(referenceID);
        DocumentModel candidateDocModel = this.documentMap.get(candidateID);

        int referenceTextLength = referenceDocModel.getTextBody().length();
        int candidateTextLength = candidateDocModel.getTextBody().length();

        double referenceAvgKPEScore = -1;
        for (KeyphraseScorePair ksp: referenceDocModel.getKeyphraseScoreList()) {
            referenceAvgKPEScore += ksp.getScore();
        }
        referenceAvgKPEScore = referenceAvgKPEScore / (float) referenceDocModel.getKeyphraseScoreList().size();

        double candidateAvgKPEScore = -1;
        for (KeyphraseScorePair ksp: candidateDocModel.getKeyphraseScoreList()) {
            candidateAvgKPEScore += ksp.getScore();
        }
        candidateAvgKPEScore = referenceAvgKPEScore / (float) candidateDocModel.getKeyphraseScoreList().size();

        newEntry.add(Integer.toString(topicID));
        newEntry.add(Integer.toString(referenceID));
        newEntry.add(Integer.toString(candidateID));
        newEntry.add(Integer.toString(referenceTextLength));
        newEntry.add(Integer.toString(candidateTextLength));
        newEntry.add(Double.toString(referenceAvgKPEScore));
        newEntry.add(Double.toString(candidateAvgKPEScore));
        newEntry.add(Integer.toString(docRelevance));

        for (Double result: tsmResults) {
            newEntry.add(Double.toString(result));
        }

        this.expStatistics.add(newEntry);
    }

    public List<List<String>> getExpStatistics() {
        return expStatistics;
    }

    public String getExpName() {
        return expName;
    }
}
