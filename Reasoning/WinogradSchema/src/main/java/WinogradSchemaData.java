/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

import de.dailab.nsm.semanticDistanceMeasures.DataExample;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Hannes on 28.03.2017.
 */
public class WinogradSchemaData extends DataExample implements Cloneable {
    public String text;
    private List<String> answers;
    public String pron;
    public String quote;
    public String correctAnswer;
    public Map<String, Double> answerMap;
    public double result;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<String> getAnswers() {
        return answers;
    }

    public void setAnswers(List<String> answers) {
        this.answers = answers;
    }

    public String getPron() {
        return pron;
    }

    public void setPron(String pron) {
        this.pron = pron;
    }

    public String getQuote() {
        return quote;
    }

    public void setQuote(String quote) {
        this.quote = quote;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public Map<String, Double> getAnswerMap(){
        return answerMap;
    }

    public void setAnswerMap(Map<String, Double> answerMap){
        this.answerMap = answerMap;
    }

    public double getResult() {
        return result;
    }

    public void setResult(double result) {
        this.result = result;
    }

    public WinogradSchemaData(String text, List<String> answers, String pron, String quote, String correctAnswer) {
        this.text = text;
        this.answers = answers;
        this.pron = pron;
        this.quote = quote;
        this.correctAnswer = correctAnswer;
        this.answerMap = new HashMap<>();
    }



@Override
public int hashCode() {
    int hash = this.text.hashCode() + this.correctAnswer.hashCode();
    return hash;
}

    @Override
    public boolean equals(Object obj) {
        if(this == null || obj == null){
            return false;
        }
        return this.hashCode() == obj.hashCode();
    }



}
