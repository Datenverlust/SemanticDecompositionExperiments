/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved. Unauthorized copying of this file, via any medium is strictly prohibited Proprietary and confidential. Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 *
 */

package de.dailab.nsm.semanticDistanceMeasures.data;


import com.opencsv.CSVReader;
import de.dailab.nsm.semanticDistanceMeasures.DataExample;
import de.dailab.nsm.semanticDistanceMeasures.SynonymPair;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by faehndrich on 31.07.15.
 * taken from: http://www.technion.ac.il/~kirar/Datasets.html
 * @book{Radinsky2011,
    author = {Radinsky, Kira and Agichtein, Eugene and Gabrilovich, Evgeniy and Markovitch, Shaul},
    title = {{A word at a time: computing word relatedness using temporal semantic analysis}},
    publisher = {ACM},
    year = {2011}
    }
 */
public class MtrukDataSet implements WordSimilarityDataSet {
    @Override
    public List<DataExample> ReadExampleDataSet() {
        String u = this.getClass().getResource("/Mtruk.csv").getPath();
        //String u = System.getProperty("user.home") + File.separator + ".decomposition" + File.separator + "DataSet" + File.separator+ "Rubenstein1965.csv";
        List<DataExample> result = new ArrayList<>(70);
        CSVReader reader = null;
        try {
            reader = new CSVReader(new FileReader(u));
            String[] nextLine;
            while((nextLine=reader.readNext())!=null)
            {
                SynonymPair pair = new SynonymPair(nextLine[0], nextLine[1], Double.valueOf(nextLine[2]));
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
    public List<SynonymPair> Normalize(List<SynonymPair> list2Normlize) {
        return null;
    }
}
