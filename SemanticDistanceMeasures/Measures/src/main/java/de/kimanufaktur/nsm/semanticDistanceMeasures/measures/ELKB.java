/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

package de.kimanufaktur.nsm.semanticDistanceMeasures.measures;

import de.kimanufaktur.nsm.decomposition.Concept;
import de.kimanufaktur.nsm.decomposition.IConcept;
import de.kimanufaktur.nsm.semanticDistanceMeasures.SemanticDistanceMeasureInterface;
import semDist.SemDist;


/**
 * Created by Sabine on 09.07.2015.
 */
public class ELKB implements SemanticDistanceMeasureInterface {

    SemDist semDist = null;

    public ELKB(){
        this.init();
    }

    public void init(){
        semDist = new SemDist("1911X5",true);
    }

    @Override
    public double compareConcepts(IConcept c1, IConcept c2) {
        double result = 0;
        if(semDist == null){
            init();
        }
        result = semDist.getSimilarity( ((Concept)c1).getLitheral(),((Concept)c2).getLitheral());
        //semDist.wordPairs(c1.getLitheral(), c2.getLitheral());
        return result/16;
    }
}
