/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

package de.kimanufaktur.nsm.semanticDistanceMeasures.data;

import com.opencsv.CSVReader;
import de.kimanufaktur.nsm.semanticDistanceMeasures.DataExample;
import de.kimanufaktur.nsm.semanticDistanceMeasures.SimilarityPair;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by faehndrich on 31.07.15.
 */
public abstract class WordSimilarityDataSet implements DataSet {

    public List<DataExample> getDataExamples(String u) {
        List<DataExample> result = new ArrayList<>(70);
        CSVReader reader = null;
        try {
            reader = new CSVReader(new FileReader(u));
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                SimilarityPair pair = new SimilarityPair(nextLine[0], nextLine[1], Double.valueOf(nextLine[2]));
                result.add(pair);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
