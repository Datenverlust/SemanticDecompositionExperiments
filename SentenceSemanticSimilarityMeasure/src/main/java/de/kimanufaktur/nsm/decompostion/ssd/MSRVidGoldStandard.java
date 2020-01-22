/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

package de.kimanufaktur.nsm.decompostion.ssd;

import de.kimanufaktur.nsm.decomposition.graph.Evaluation;
import de.kimanufaktur.nsm.semanticDistanceMeasures.DataExample;
import de.kimanufaktur.nsm.semanticDistanceMeasures.SimilarityPair;
import de.kimanufaktur.nsm.semanticDistanceMeasures.data.MSRvid;
import org.apache.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;


public class MSRVidGoldStandard {
    private static final Logger logger = Logger.getLogger(MSRVidGoldStandard.class);

    /**
     * Runs the MSRvid gold standard data set from STS-12
     * and write the results to MSRvid_Result.txt and console.
     *
     * @param top The number of test data pairs which should be processed.
     */
    private void runGoldStandard(int top, String gsFile, String inputFile) {

        SentenceSemanticSimilarityMeasure sssm = new SentenceSemanticSimilarityMeasure();

        // read MSRvid data set from STS-12
        MSRvid msrvid = new MSRvid();
        DecimalFormat df = new DecimalFormat("#.##");
        List<? extends DataExample> dataSet = msrvid.readMSRvid(top, gsFile, inputFile);
        Evaluation.normalize(dataSet);
        double cumulativeResultError = 0;
        // run data set

        for (SimilarityPair pair : ((List<SimilarityPair>) dataSet)) {
            double result = sssm.compare(pair.getString1(), pair.getString2());
            if (Double.isNaN(result)) {
                result = 0.5;
            }
            pair.setResult(result);
            logger.info(pair.getString1() + ";" + pair.getString2() + ";" + df.format(pair.getTrueResult()) + ";" + df.format((pair.getResult())));
            cumulativeResultError += (Math.abs(pair.getTrueResult() - pair.getResult()));
        }

        System.out.println("SpearmanCorrelation: " + Evaluation.SpearmanCorrelation(dataSet));
        System.out.println("PearsonCorrelation: " + Evaluation.PearsonCorrelation(dataSet));
        System.out.println("Total error: " + cumulativeResultError);

        // write result to file
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
            //String rulstPath = System.getProperty("user.home").toString() + File.separator + ".Decomposition" + File.separator + "Experiments"+ File.separator + "SentenceSimilarity";
            PrintWriter writer = new PrintWriter(timeStamp + "_MSRvid_Result.csv", "UTF-8");
            writer.println("Sentence1;Sentence2;STS12Distance;OurResult");
            for (SimilarityPair pair : ((List<SimilarityPair>) dataSet)) {
                double should = pair.getTrueResult();
                double is = pair.getResult();
                String result = pair.getString1() + ";" + pair.getString2() + ";" + df.format(should) + ";" + df.format(is);
                writer.println(result);
            }
            writer.close();

        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return;
    }

}
