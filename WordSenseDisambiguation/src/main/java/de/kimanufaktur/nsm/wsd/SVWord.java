/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

package de.kimanufaktur.nsm.wsd;

import de.kimanufaktur.nsm.decomposition.WordType;

import java.util.ArrayList;

/**
 * Created by root on 28.12.15.
 */
public class SVWord {

    public SVWord() {
        this.coveredText = "";
        this.DisambiguationSubject = "";
        this.senses = new ArrayList<>();
        this.POS = "";
    }


    public String getCoveredText() {
        return coveredText;
    }

    public String getDisambiguationSubject() {
        return DisambiguationSubject;
    }

    public void setSenses(ArrayList<SVSense> senses) {
        this.senses = senses;
    }

    public ArrayList<SVSense> getSenses() {
        return this.senses;
    }

    public void addSense(SVSense sense) {
        this.senses.add(sense);
    }

    public void removeSense(SVSense sense) {
        this.senses.remove(sense);
    }

    public void setCoveredText(String coveredText) {
        this.coveredText = coveredText;
    }

    public void setDisambiguationSubject(String disambiguationSubject) {
        DisambiguationSubject = disambiguationSubject;
    }

    public void setPOS(String pos) {
        this.POS = pos;
    }

    public String getPOS() {
        return this.POS;
    }

    public WordType getWordType() {
        switch(this.POS) {
            case "ADV":
                return WordType.RB;
            case "VERB":
                return WordType.VB;
            case "ADJ":
                return WordType.JJ;
            case "NOUN":
                return WordType.NN;
            default:
                return WordType.UNKNOWN;
        }
    }


    private String POS;
    private WordType wordType;
    private ArrayList<SVSense> senses;
    private String coveredText;
    private String DisambiguationSubject;
}
