/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved. Unauthorized copying of this file, via any medium is strictly prohibited Proprietary and confidential. Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 *
 */

package de.kimanufaktur.nsm.semanticDistanceMeasures.data;

import de.kimanufaktur.nsm.semanticDistanceMeasures.DataExample;
import de.kimanufaktur.nsm.semanticDistanceMeasures.SimilarityPair;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;


public class MSRvid extends WordSimilarityDataSet {

    private static final Logger logger = Logger.getLogger(MSRvid.class);
    List<DataExample> msrvid = new ArrayList<>();

    public static void main(String[] args) {

        MSRvid test = new MSRvid();
        test.readMSRvid(750, "STS.gs.MSRvid.txt", "STS.input.MSRvid.txt");

    }

    /**
     * read the MSRvid gold standard data set from STS-12 folder into a list of type SentencePair
     */
    public List<DataExample> readMSRvid(int top, String gsFile, String inputFile) {
        String pathDistance = this.getClass().getResource("/STS-12/" + gsFile).getPath();
        String pathSentence = this.getClass().getResource("/STS-12/" + inputFile).getPath();

        try {
            BufferedReader distanceReader = new BufferedReader(new FileReader(pathDistance));
            BufferedReader sentenceReader= new BufferedReader(new FileReader(pathSentence));

            List<Double> distanceSet = new ArrayList<Double>();
            String line;
            while ((line = distanceReader.readLine()) != null) {
                distanceSet.add(Double.parseDouble(line) / 5);
            }
            distanceReader.close();

            int i = 0;
            while ((line = sentenceReader.readLine()) != null) {
                if (i == top)
                    break;
                String[] sentencePair = line.split("\t", 2);
                double distance = distanceSet.get(i);
                logger.info("load sentence: " + sentencePair[0] + " " + sentencePair[1] + " " + distance);
                msrvid.add(new SimilarityPair(sentencePair[0], sentencePair[1], distance));
                i++;
            }
            sentenceReader.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        msrvid.sort(new Comparator<DataExample>() {
            @Override
            public int compare(DataExample o1, DataExample o2) {
                if (o1.getResult() < o2.getResult()) {
                    return 1;
                } else if (o1.getResult() > o2.getResult()) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
        return msrvid;

    }

    @Override
    public List<DataExample> ReadExampleDataSet() {
        return readMSRvid(459,"STS.gs.SMTeuroparl.txt", "STS.input.SMTeuroparl.txt");
    }

    @Override
    public Collection<SimilarityPair> Normalize(List<SimilarityPair> list2Normlize) {
        return null;
    }
}
