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
 * The MEN Test collection taken from {@see http://clic.cimec.unitn.it/~elia.bruni/MEN}
 *
 * @article{Bruni2014, author = {Bruni, E and Tran, N K and Baroni, M},
 * title = {{Multimodal Distributional Semantics.}},
 * journal = {Journal of Artificial Intelligence Research},
 * year = {2014}
 * }
 */
public class MENDataSet extends WordSimilarityDataSet {
    @Override
    public List<DataExample> ReadExampleDataSet() {
        String u = this.getClass().getResource("/MEN/MEN.csv").getPath();
        //String u = System.getProperty("user.home") + File.separator + ".decomposition" + File.separator + "DataSet" + File.separator+ "Rubenstein1965.csv";
        return getDataExamples(u);
    }

    @Override
    public List<SimilarityPair> Normalize(List<SimilarityPair> list2Normlize) {
        return null;
    }
}
