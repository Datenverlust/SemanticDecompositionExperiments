/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

import java.util.Map;

/**
 * Created by faehndrich on 30.09.16.
 */
public class WinogradSchemaQuestion implements Cloneable{
    String sentence;
    String conceptOfInterest;
    Map<String, Boolean> answers;

    public String getSentence() {
        return sentence;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }

    public String getConceptOfInterest() {
        return conceptOfInterest;
    }

    public void setConceptOfInterest(String conceptOfInterest) {
        this.conceptOfInterest = conceptOfInterest;
    }

    public Map<String, Boolean> getAnswers() {
        return answers;
    }

    public void setAnswers(Map<String, Boolean> answers) {
        this.answers = answers;
    }

    public WinogradSchemaQuestion(String sentence, String conceptOfInterest, Map<String, Boolean> answers){
        this.sentence = sentence;
        this.conceptOfInterest = conceptOfInterest;
        this.answers = answers;
    }
}
