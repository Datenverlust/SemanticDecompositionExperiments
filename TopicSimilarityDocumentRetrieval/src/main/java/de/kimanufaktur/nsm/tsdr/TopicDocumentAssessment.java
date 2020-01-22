/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

package de.kimanufaktur.nsm.tsdr;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TopicDocumentAssessment {
    private int topicID;
    private List<Integer> irrelevantDocs;
    private List<Integer> relevantDocs;
    private List<Integer> highlyRelevantDocs;

    public TopicDocumentAssessment(int topicID) {
        this.topicID = topicID;
        this.irrelevantDocs = new ArrayList<>();
        this.relevantDocs = new ArrayList<>();
        this.highlyRelevantDocs = new ArrayList<>();
    }

    public int getTopicID() {
        return topicID;
    }

    public List<Integer> getIrrelevantDocs() {
        return irrelevantDocs;
    }

    public List<Integer> getRelevantDocs() {
        return relevantDocs;
    }

    public List<Integer> getHighlyRelevantDocs() {
        return highlyRelevantDocs;
    }

    public void addIrrelevantDoc(int docID) {
        this.irrelevantDocs.add(docID);
    }

    public void addRelevantDoc(int docID) {
        this.relevantDocs.add(docID);
    }
    public void addHighlyRelevantDoc(int docID) {
        this.highlyRelevantDocs.add(docID);
    }

    public Set<Integer> getAllDocs() {
        Set<Integer> allDocs = new HashSet<>();
        allDocs.addAll(this.irrelevantDocs);
        allDocs.addAll(this.relevantDocs);
        allDocs.addAll(this.highlyRelevantDocs);

        return allDocs;
    }

}
