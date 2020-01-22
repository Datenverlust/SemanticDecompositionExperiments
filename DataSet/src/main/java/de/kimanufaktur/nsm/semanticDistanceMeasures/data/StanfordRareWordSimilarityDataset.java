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
 * Taken from: http://stanford.edu/~lmthang/morphoNLM/
 *
 * @article{Luong2013, author = {Luong, M T and Socher, R and Manning, Christopher D},
 * title = {{Better word representations with recursive neural networks for morphology}},
 * journal = {CoNLL-2013},
 * year = {2013}
 * }
 */
public class StanfordRareWordSimilarityDataset extends WordSimilarityDataSet {
    @Override
    public List<DataExample> ReadExampleDataSet() {
        String u = this.getClass().getResource("/Stanford_Rare_Word_Similarity_Dataset/RareWordsDataSet.csv").getPath();
        //String u = System.getProperty("user.home") + File.separator + ".decomposition" + File.separator + "DataSet" + File.separator+ "Rubenstein1965.csv";
        return getDataExamples(u);
    }

    @Override
    public List<SimilarityPair> Normalize(List<SimilarityPair> list2Normlize) {
        return null;
    }
}
