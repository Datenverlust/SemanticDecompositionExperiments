/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

package de.kimanufaktur.nsm.reasoning;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by klempnow on 07.12.17
 */
public class SciQChallengeQuestion implements Cloneable{
    String question;
    String distractor1;
    String distractor2;
    String distractor3;
    String correct_answer;
    String support;
    String conceptOfInterest;


    public String getConceptOfInterest() {
        return conceptOfInterest;
    }

    public void setConceptOfInterest(String conceptOfInterest) {
        this.conceptOfInterest = conceptOfInterest;
    }


    public String toString(){
        StringBuilder builder = new StringBuilder();
        builder.append("Question: " + question + "\n");
        builder.append("Answers1: " + distractor1 +"\n");
        builder.append("Answers2: " + distractor2 +"\n");
        builder.append("Answers3: " + distractor3 +"\n");
        builder.append("Context: " + conceptOfInterest + "\n");
        builder.append("correct: " + correct_answer+ "\n" );
        return builder.toString();
    }

    public String getDistractor1() {
        return distractor1;
    }

    public void setDistractor1(String distractor1) {
        this.distractor1 = distractor1;
    }

    public String getDistractor2() {
        return distractor2;
    }

    public void setDistractor2(String distractor2) {
        this.distractor2 = distractor2;
    }

    public String getDistractor3() {
        return distractor3;
    }

    public void setDistractor3(String distractor3) {
        this.distractor3 = distractor3;
    }

    public String getCorrect_answer() {
        return correct_answer;
    }

    public void setCorrect_answer(String correct_answer) {
        this.correct_answer = correct_answer;
    }

    public String getSupport() {
        return support;
    }

    public void setSupport(String support) {
        this.support = support;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Map<String, String> getAnswers() {
        HashMap<String, String> answers = new HashMap<>();
        answers.put("Answers1",distractor1);
        answers.put("Answers2",distractor2);
        answers.put("Answers3",distractor3);
        answers.put("Answers4",correct_answer);

        return answers;
    }
}
