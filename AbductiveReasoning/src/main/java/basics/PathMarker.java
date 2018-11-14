/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

package basics;
import de.dailab.nsm.decomposition.Concept;
import de.tuberlin.spreadalgo.Link;
import de.tuberlin.spreadalgo.Marker;
import java.util.List;

public class PathMarker implements Marker{

    Concept origin = null;
    //inferencePath contains the history, all visited nodes etc for each marker
    InferencePath inferencePath = null;
    private boolean startsAtAnswer=false;
    private double activation;
    private int answerNo=-1;
    private boolean abduktiveNode;

    public void setStartsAtAnswer(boolean startsAtAnswer){this.startsAtAnswer=startsAtAnswer;}

    public boolean startsAtAnswer(){
        return (this.startsAtAnswer && answerNo > 0 );
    }

    public Concept getOrigin() {
        return origin;
    }

    public InferencePath getInferencePath() {
        return inferencePath;
    }

    public void addLinkToHistory(Link link){inferencePath.addLinkToPath(link); }

    public double getActivation(){return this.activation;}

    public void setActivation(Double activation){this.activation=activation;}

    public PathMarker(PathMarker marker, Link nextLink){
        this.startsAtAnswer=marker.startsAtAnswer;
        this.inferencePath=new InferencePath(marker.getInferencePath());
        this.inferencePath.addLinkToPath(nextLink);
        this.activation=1;
        this.answerNo=marker.answerNo;
        this.origin=marker.origin;
        if(PathMarkerPassingConfig.bAbductiveInference)
            this.inferencePath.setAbductiveLinkNumber(nextLink,startsAtAnswer);
    }

    public PathMarker(Concept origin,Link firstLink){
        PathMarker newMarker=new PathMarker(origin);
        newMarker.inferencePath=new InferencePath(firstLink);
        if(PathMarkerPassingConfig.bAbductiveInference)
            this.inferencePath.setAbductiveLinkNumber(firstLink,startsAtAnswer);
    }

    public PathMarker(Concept origin){
        this.origin=origin;
        this.activation=1;
        this.inferencePath=new InferencePath();
    }

    public String toString(){
        StringBuilder res = new StringBuilder();
        res.append("Marker-Path starts at :"+ origin.getLitheral() + " -> " + inferencePath.printHistory());
        if (startsAtAnswer)
            res.append("    - Answer no°:" + getAnswerNo());
        return res.toString();
    }

    public int getAnswerNo() {
        return answerNo;
    }

    public void setAnswerNo(int nAnswer) {
        this.answerNo = nAnswer;
    }

    public boolean equals(PathMarker m2){
        return this.inferencePath.equals(m2.inferencePath);
    }

    public void setAbduktiveNode(boolean abductiveNode){
        this.abduktiveNode=abductiveNode;
    }

    public boolean getAbduktiveNode(){
        return abduktiveNode;
    }
}
