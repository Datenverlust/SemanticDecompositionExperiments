/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved. Unauthorized copying of this file, via any medium is strictly prohibited Proprietary and confidential. Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 *
 */

package de.dailab.nsm.semanticDistanceMeasures.data;

import com.opencsv.CSVReader;
import de.dailab.nsm.semanticDistanceMeasures.DataExample;
import de.dailab.nsm.semanticDistanceMeasures.SimilarityPair;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by faehndrich on 31.07.15.
 * The MEN Test collection taken from {@see http://clic.cimec.unitn.it/~elia.bruni/MEN}
 * @article{Bruni2014,
    author = {Bruni, E and Tran, N K and Baroni, M},
    title = {{Multimodal Distributional Semantics.}},
    journal = {Journal of Artificial Intelligence Research},
    year = {2014}
    }
 */
public class MENDataSet implements WordSimilarityDataSet {
    @Override
    public List<DataExample> ReadExampleDataSet() {
        String u = this.getClass().getResource("/MEN/MEN.csv").getPath();
        //String u = System.getProperty("user.home") + File.separator + ".decomposition" + File.separator + "DataSet" + File.separator+ "Rubenstein1965.csv";
        List<DataExample> result = new ArrayList<>(70);
        CSVReader reader = null;
        try {
            reader = new CSVReader(new FileReader(u));
            String[] nextLine;
            while((nextLine=reader.readNext())!=null)
            {
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

    @Override
    public List<SimilarityPair> Normalize(List<SimilarityPair> list2Normlize) {
        return null;
    }
}
