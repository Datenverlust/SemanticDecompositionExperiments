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
 * Taken from: http://www.cs.technion.ac.il/~gabr/resources/data/wordsim353/
 *
 * @article{Finkelstein2002, author = {Finkelstein, Lev and Gabrilovich, Evgeniy and Matias, Yossi and Rivlin, Ehud and Solan, Zach and Wolfman, Gadi and Ruppin, Eytan},
 * title = {{Placing search in context: the concept revisited.}},
 * journal = {ACM Trans. Inf. Syst. ()},
 * year = {2002}
 * }
 */
public class WordSim353DataSet extends WordSimilarityDataSet {
    @Override
    public List<DataExample> ReadExampleDataSet() {
        String u = this.getClass().getResource("/wordsim353/WordSim353.csv").getPath();
        //String u = System.getProperty("user.home") + File.separator + ".decomposition" + File.separator + "DataSet" + File.separator+ "Rubenstein1965.csv";
        return super.getDataExamples(u);
    }

    @Override
    public List<SimilarityPair> Normalize(List<SimilarityPair> list2Normlize) {
        return null;
    }
}
