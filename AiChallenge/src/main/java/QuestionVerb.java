/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

import de.kimanufaktur.nsm.decomposition.Concept;

public class QuestionVerb extends Concept {

    int WortNr=-1;
    int SatzNr=-1;
    String originalName=null;

    public QuestionVerb(String litheral, int WortNr, int SatzNr){
        super(litheral+"(NNS)");
        setOriginalName(litheral);
        setWortNr(WortNr);
        setSatzNr(SatzNr);
    }

    public int getWortNr() {
        return WortNr;
    }

    public void setWortNr(int wortNr) {
        WortNr = wortNr;
    }

    public int getSatzNr() {
        return SatzNr;
    }

    public void setSatzNr(int satzNr) {
        SatzNr = satzNr;
    }

    public Boolean isPron(int satzNr, int wortNr){
        if(satzNr==this.SatzNr && wortNr==this.WortNr){
            return true;
        } else {
            return false;
        }
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }
}
