/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

package de.kimanufaktur.nsm.semanticDistanceMeasures.data;

import de.kimanufaktur.nsm.semanticDistanceMeasures.DataExample;
import de.kimanufaktur.nsm.semanticDistanceMeasures.SimilarityPair;

import java.util.Collection;
import java.util.List;

/**
 * Created by Johannes Fähndrich on 10.06.18 as part of his dissertation.
 */
public interface DataSet {
    List<DataExample> ReadExampleDataSet();

    Collection<SimilarityPair> Normalize(List<SimilarityPair> list2Normlize);
}
