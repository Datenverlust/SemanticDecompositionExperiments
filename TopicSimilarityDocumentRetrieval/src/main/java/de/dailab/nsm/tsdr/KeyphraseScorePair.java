/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

package de.dailab.nsm.tsdr;

class KeyphraseScorePair {

    private String keyphrase;
    private double score;

    KeyphraseScorePair(String keyphrase, Double score) {
        this.keyphrase = keyphrase;
        this.score = score;
    }

    public String getKeyphrase() {
        return keyphrase;
    }

    public double getScore() {
        return score;
    }
}
