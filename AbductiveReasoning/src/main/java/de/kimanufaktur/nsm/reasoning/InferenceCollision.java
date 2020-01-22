/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

package de.kimanufaktur.nsm.reasoning;
import de.kimanufaktur.markerpassing.Node;
import de.kimanufaktur.nsm.decomposition.Concept;


public class InferenceCollision {
    private PathMarker m1;
    private PathMarker m2;
    private int answerNo=-1;
    private boolean containsWhiteListLink;

    public InferenceCollision(PathMarker mark1, PathMarker mark2, boolean whiteList){
        m1=mark1;
        m2=mark2;
        if (m1.startsAtAnswer())
            answerNo=m1.getAnswerNo();
        else
            answerNo=m2.getAnswerNo();
        containsWhiteListLink=whiteList;
    }

    public int getAnswerNumber(){
        return answerNo;
    }

    public Concept getQuestionConcept(){
        if(m1.startsAtAnswer())
            return m2.getOrigin();
        else
            return m1.getOrigin();
    }

    public Concept getAnswerConcept(){
        if(m1.startsAtAnswer())
            return m1.getOrigin();
        else
            return m2.getOrigin();
    }

    public boolean containsWhiteListLink(){
        return containsWhiteListLink;
    }

    public String getAnswerString(){
        if (m1.startsAtAnswer())
            return m1.origin.getLitheral();
        else
            return m2.origin.getLitheral();
    }

    public double getAbductiveValue(){
        return 0.5 *( m1.getInferencePath().getAbductiveValue())
                + 0.5 *( m2.getInferencePath().getAbductiveValue());
    }

    public int getAbductions(){
        return m1.inferencePath.getAbductiveLinkSize()+m2.getInferencePath().getAbductiveLinkSize();
    }

    public int getVisitedNodes(){
        return m1.inferencePath.getPathSize()+m2.inferencePath.getPathSize()-1;
    }

    public double getSpecificity(){
        return (0.5 * (m1.inferencePath.getPathSpecifity()+m2.inferencePath.getPathSpecifity()));
    }

    /**
     * a value less to 1 indicates less generalisation per step and therefore a closer relation between two concepts.
     * @param ic2
     * @return
     */
    public boolean compare(InferenceCollision ic2){
        if(this.getAbductions()< ic2.getAbductions())
            return true;
        else if (this.getAbductions()==ic2.getAbductions()){
            if (this.getVisitedNodes()>ic2.getVisitedNodes()) {
                return true;
            }else {
                return false;
            }
        }
        else
            return false;
    }

    public boolean compare_v7(InferenceCollision ic2){
        if(this.getSpecificity()>= ic2.getSpecificity())
            return true;
        else
            return false;
    }


    @Override
    public String toString(){
        StringBuilder res = new StringBuilder();
        if(m1.startsAtAnswer()){
            res.append(m1.origin.getLitheral()+" ->");
            for (Node node: m1.getInferencePath().visitedNodes){
                res.append(((PathNode)node).getConcept().getLitheral()+" -> ");
            }
            res.append(" Kollision " + "\n");
            res.append(m2.origin.getLitheral()+" ->");
            for (Node node: m2.getInferencePath().visitedNodes){
                res.append(((PathNode)node).getConcept().getLitheral()+" -> ");
            }
            res.append("Kollision");

        }else{
            res.append(m2.origin.getLitheral()+" ->");
            for (Node node: m2.getInferencePath().visitedNodes){
                res.append(((PathNode)node).getConcept().getLitheral()+" -> ");
            }
            res.append(" Kollision " + "\n");
            res.append(m1.origin.getLitheral()+"->");
            for (Node node: m1.getInferencePath().visitedNodes){
                res.append(((PathNode)node).getConcept().getLitheral()+" -> ");
            }
            res.append("Kollision");

        }
        return res.toString();
    }

    public boolean equals(InferenceCollision ic2){
        return m1.equals(ic2.m1) && m2.equals(ic2.m2) && answerNo==ic2.answerNo && containsWhiteListLink==ic2.containsWhiteListLink;
    }
}
