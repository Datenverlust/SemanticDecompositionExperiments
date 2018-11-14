
/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

/**
 * Created by linus on 31.03.18.
 */
public class Question {
    String questionContent;
    String anwserA;
    String anwserB;
    String anwserC;
    String anwserD;
    String rightAwnser;

    public QuestionClass getType() {
        return type;
    }

    public void setType(QuestionClass type) {
        this.type = type;
    }

    QuestionClass type;

    public Question(String questionContent, String anwserA, String anwserB, String anwserC, String anwserD, String rightAwnser){
        this.questionContent = questionContent;
        this.anwserA = anwserA;
        this.anwserB = anwserB;
        this.anwserC = anwserC;
        this.anwserD = anwserD;
        this.rightAwnser = rightAwnser;


    }

    @Override
    public String toString() {
        return "Question{" +
                "questionContent='" + questionContent + '\'' +
                ", anwserA='" + anwserA + '\'' +
                ", anwserB='" + anwserB + '\'' +
                ", anwserC='" + anwserC + '\'' +
                ", anwserD='" + anwserD + '\'' +
                ", rightAwnser='" + rightAwnser + '\'' +
                '}';
    }

    public String getQuestionContent() {
        return questionContent;
    }

    public void setQuestionContent(String questionContent) {
        this.questionContent = questionContent;
    }

    public String getAnwserA() {
        return anwserA;
    }

    public void setAnwserA(String anwserA) {
        this.anwserA = anwserA;
    }

    public String getAnwserB() {
        return anwserB;
    }

    public void setAnwserB(String anwserB) {
        this.anwserB = anwserB;
    }

    public String getAnwserC() {
        return anwserC;
    }

    public void setAnwserC(String anwserC) {
        this.anwserC = anwserC;
    }

    public String getAnwserD() {
        return anwserD;
    }

    public void setAnwserD(String anwserD) {
        this.anwserD = anwserD;
    }

    public String getRightAwnser() {
        return rightAwnser;
    }

    public void setRightAwnser(String rightAwnser) {
        this.rightAwnser = rightAwnser;
    }

}

