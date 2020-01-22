/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved. Unauthorized copying of this file, via any medium is strictly prohibited Proprietary and confidential. Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 *
 */

package de.kimanufaktur.nsm.semanticDistanceMeasures.data;


import de.kimanufaktur.nsm.semanticDistanceMeasures.DataExample;
import de.kimanufaktur.nsm.semanticDistanceMeasures.SimilarityPair;

import java.util.List;

/**
 * Created by faehndrich on 31.07.15.
 * taken from: http://www.technion.ac.il/~kirar/Datasets.html
 *
 * @book{Radinsky2011, author = {Radinsky, Kira and Agichtein, Eugene and Gabrilovich, Evgeniy and Markovitch, Shaul},
 * title = {{A word at a time: computing word relatedness using temporal semantic analysis}},
 * publisher = {ACM},
 * year = {2011}
 * }
 */
public class MtrukDataSet extends WordSimilarityDataSet {
    @Override
    public List<DataExample> ReadExampleDataSet() {
        String u = this.getClass().getResource("/Mtruk.csv").getPath();
        //String u = System.getProperty("user.home") + File.separator + ".decomposition" + File.separator + "DataSet" + File.separator+ "Rubenstein1965.csv";
        return getDataExamples(u);
    }

    @Override
    public List<SimilarityPair> Normalize(List<SimilarityPair> list2Normlize) {
        return null;
    }
}
