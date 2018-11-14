/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

package basics;
import de.dailab.nsm.decomposition.graph.spreadingActivation.MarkerPassing.MarkerPassingConfig;

public class PathMarkerPassingConfig extends MarkerPassingConfig {

    static private int DecompositionDepth=2;
    static private int nAbductionValue=10;
    static private int nMaxAmountOfQuestions=250;
    static public boolean bAbductiveInference=false;
    static int terminationPulsCount = 150;
    static private double dHypernym=1.0D;
    static private double dSynonym=1.0D;
    static private double dMeronym=1.0D;
    static private double dDefintion=1.0D;
    static private double dHyponym=1.0D;
    static private double dArbitraryRelation=1.0D;

    public static int getDecompositionDepth() {
        return DecompositionDepth;
    }

    public static void setDecompositionDepth(int decompositionDepth) {
        DecompositionDepth = decompositionDepth;
    }

    public static int getAbductionValue() {
        return nAbductionValue;
    }

    public static void setAbductionValue(int nAbductionValue) {
        PathMarkerPassingConfig.nAbductionValue = nAbductionValue;
    }

    public static int getMaxAmountOfQuestions() {
        return nMaxAmountOfQuestions;
    }

    public static void setMaxAmountOfQuestions(int nMaxAmountOfQuestions) {
        PathMarkerPassingConfig.nMaxAmountOfQuestions = nMaxAmountOfQuestions;
    }

    public static double getHypernym() {
        return dHypernym;
    }

    public static void setHypernym(double dHypernym) {
        PathMarkerPassingConfig.dHypernym = dHypernym;
    }

    public static double getSynonym() {
        return dSynonym;
    }

    public static void setSynonym(double dSynonym) {
        PathMarkerPassingConfig.dSynonym = dSynonym;
    }

    public static double getMeronym() {
        return dMeronym;
    }

    public static void setMeronym(double dMeronym) {
        PathMarkerPassingConfig.dMeronym = dMeronym;
    }

    public static double getDefintion() {
        return dDefintion;
    }

    public static void setDefintion(double dDefintion) {
        PathMarkerPassingConfig.dDefintion = dDefintion;
    }

    public static double getHyponym() {
        return dHyponym;
    }

    public static void setHyponym(double dHyponym) {
        PathMarkerPassingConfig.dHyponym = dHyponym;
    }

    public static double getArbitraryRelation() {
        return dArbitraryRelation;
    }

    public static void setArbitraryRelation(double dArbitraryRelation) {
        PathMarkerPassingConfig.dArbitraryRelation = dArbitraryRelation;
    }

    public static int getTerminationPulsCount() {
        return terminationPulsCount;
    }

    public static void setTerminationPulsCount(int terminationPulsCount) {
        PathMarkerPassingConfig.terminationPulsCount = terminationPulsCount;
    }


}
