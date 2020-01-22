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
 * Created by faehndrich on 07.05.15.
 * Create a dataset from the Rubenstein1965 paper.
 * Finkelstein, L., Gabrilovich, E., Matias, Y., Rivlin, E., Solan, Z., Wolfman, G., & Ruppin, E. (2002).
 * Placing search in context: the concept revisited. ACM Trans. Inf. Syst. (), 20(1), 116–131.
 * http://doi.org/10.1145/503104.503110
 */
public class Rubenstein1965Dataset extends WordSimilarityDataSet {

    @Override
    public List<DataExample> ReadExampleDataSet() {
        String u = this.getClass().getResource("/Rubenstein1965.csv").getPath(); // TODO use this, the original resource
//        String u = this.getClass().getResource("/Rubenstein1965-subset.csv").getPath();
        //String u = System.getProperty("user.home") + File.separator + ".decomposition" + File.separator + "DataSet" + File.separator+ "Rubenstein1965.csv";
        return getDataExamples(u);
    }


    @Override
    public List<SimilarityPair> Normalize(List<SimilarityPair> list2Normlize) {
        return null;
    }


}
