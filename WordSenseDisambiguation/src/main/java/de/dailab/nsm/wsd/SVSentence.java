/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

package de.dailab.nsm.wsd;

import java.util.ArrayList;

/**
 * Created by root on 28.12.15.
 */
public class SVSentence {

    public String getCoveredText() {
        return coveredText;
    }

    public void setCoveredText(String coveredText) {
        this.coveredText = coveredText;
    }

    public ArrayList<SVWord> getWords() {
        return words;
    }

    public void setWords(ArrayList<SVWord> words) {
        this.words = words;
    }

    public void addWord(SVWord word) {
        this.words.add(word);
    }

    public void removeWord(SVWord word) {
        this.words.remove(word);
    }


    public SVSentence() {
        this.coveredText = "";
        this.words = new ArrayList<>();
    }

    public SVSentence(String coveredText) {
        this.coveredText = coveredText;
        this.words = new ArrayList<>();
    }

    private String coveredText;
    private ArrayList<SVWord> words;
}
