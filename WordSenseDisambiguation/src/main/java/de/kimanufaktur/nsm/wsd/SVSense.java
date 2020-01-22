/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

package de.kimanufaktur.nsm.wsd;

/**
 * Created by root on 28.12.15.
 */
public class SVSense {

    public String getID() {
        return id;
    }

    public void setID(String id) {
        this.id = id;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public SVSense() {
        this.confidence = 0.d;
        this.id = "";
    }

    public SVSense(String coveredText, String id, double confidence) {
        this.id = id;
        this.confidence = confidence;
    }

    private String id;
    private double confidence;
}
