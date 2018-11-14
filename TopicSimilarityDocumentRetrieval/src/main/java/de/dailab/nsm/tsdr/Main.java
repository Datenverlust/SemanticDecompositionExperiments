/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

package de.dailab.nsm.tsdr;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;


public class Main {


    public static void main(String[] args) throws IOException {

        // parameters
        Logger.enabled = true;
        String dataDirectory =  "/Users/Max/Documents/BA/data_workdir.nosync/qrles_nist_data/";
        String expDirectory = "/Users/Max/Documents/BA/data_workdir.nosync/exp_results/";
        String pythonScriptFile = "/Users/Max/Documents/BA/keyphrase_extraction/keyphrase_extraction.py";
        String qrelsFile = "/Users/Max/Documents/BA/data_workdir.nosync/qrels_nist.txt";

        FileHandler fh = new FileHandler(dataDirectory, expDirectory, pythonScriptFile);
        DataHandler dh = new DataHandler();
        TopicSimilarityMeasure tsm = new TopicSimilarityMeasure();

        fh.initDataHandler(dh, qrelsFile);

//        dh.selectRandExpDocIDs(1, 50, 50,50);
        dh.selectTestExpDocIDs(307, 30,30, 31);

        fh.processFiles(dh);

        runExperiment(tsm, dh);

        fh.writeExpStatistics(dh);
    }


    static void runExperiment(TopicSimilarityMeasure tsm, DataHandler dh) {
        Map<Integer,TopicDocumentAssessment> tdaMap = dh.getExpTDAMap();

        Logger.outLn("--- Experiment ---");

        for (int topicID: tdaMap.keySet()) {
            TopicDocumentAssessment currentTDA = tdaMap.get(topicID);
            int referenceDocID = currentTDA.getHighlyRelevantDocs().get(0);

            Logger.outLn("--- Result for topic ID: " + topicID);

            Collection<Integer> candidateDocIDs = currentTDA.getAllDocs();
            candidateDocIDs.remove(referenceDocID);
            for (int docID: candidateDocIDs) {
                int docRelevanceType = -1;
                if (currentTDA.getIrrelevantDocs().contains(docID)) {
                    docRelevanceType = 0;
                }
                if (currentTDA.getRelevantDocs().contains(docID)) {
                    docRelevanceType = 1;
                }
                if (currentTDA.getHighlyRelevantDocs().contains(docID)) {
                    docRelevanceType = 2;
                }

                Logger.outLn(String.format("--- DocID: %d [%d] ---", docID, docRelevanceType));

                List<Double> tsmResults = tsm.getTopicSimilarity(dh.getDocModel(referenceDocID), dh.getDocModel(docID));

                dh.collectStatistics(topicID, referenceDocID, docID, docRelevanceType, tsmResults);
            }
        }
    }

}
