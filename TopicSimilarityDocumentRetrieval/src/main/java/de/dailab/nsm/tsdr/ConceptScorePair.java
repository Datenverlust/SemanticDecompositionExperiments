/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

package de.dailab.nsm.tsdr;

import de.dailab.nsm.decomposition.Concept;

class ConceptScorePair {

    private Concept concept;
    private double score;

    ConceptScorePair(Concept concept, Double score) {
        this.concept = concept;
        this.score = score;
    }

    public Concept getConcept() {
        return concept;
    }

    public double getScore() {
        return score;
    }
}
