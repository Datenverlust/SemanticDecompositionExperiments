/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

package de.kimanufaktur.nsm.semanticDistanceMeasures.test;

import de.kimanufaktur.nsm.decomposition.Concept;
import de.kimanufaktur.nsm.decomposition.Decomposition;
import de.kimanufaktur.nsm.decomposition.WordType;
import de.kimanufaktur.nsm.semanticDistanceMeasures.BiDirectionalOneStepAlgorithm;
import org.junit.Test;

/**
 * Created by faehndrich on 03.09.15.
 */
public class BDOSTests {

    public static void main(String args[]) {
        System.out.println("welcome to BiDirectionalOneStepAlgorithm Tests");
        BDOSTests test = new BDOSTests();

        test.bdosTest();

    }
    @Test
    public void bdosTest() {

        Concept tree = Decomposition.createConcept("tree", WordType.NN);
        Concept branch = Decomposition.createConcept("branch", WordType.NN);
        Concept acacia = Decomposition.createConcept("acacia", WordType.NN);
        Concept bison = Decomposition.createConcept("bison", WordType.NN);
        Concept infinity = Decomposition.createConcept("infinity", WordType.NN);
        Concept organism = Decomposition.createConcept("organism", WordType.NN);
        Concept duramen = Decomposition.createConcept("duramen", WordType.NN);
        Concept entity = Decomposition.createConcept("entity", WordType.NN);
        Concept fibula = Decomposition.createConcept("fibula", WordType.NN);
        Concept gum_arabic = Decomposition.createConcept("gum arabic", WordType.NN);
        Concept edible_nut = Decomposition.createConcept("edible nut", WordType.NN);
        Concept gum = Decomposition.createConcept("gum", WordType.NN);
        Concept lucky = Decomposition.createConcept("lucky", WordType.JJ);
        Concept happy = Decomposition.createConcept("happy", WordType.JJ);
        Concept sad = Decomposition.createConcept("sad", WordType.JJ);
        Concept sing = Decomposition.createConcept("sing", WordType.VB);
        Concept dance = Decomposition.createConcept("dance", WordType.VB);

        //Create BiDirectionalOneStepAlgorithm
        BiDirectionalOneStepAlgorithm bdos = new BiDirectionalOneStepAlgorithm();
        bdos.init();
        Concept c1 = tree;
        Concept c2 = bison;
        double result = bdos.compareConcepts(c1, c2);
        System.out.println("the result of " + c1.getLitheral() +" and "+c2.getLitheral() +" is " + result);
        assert (result != 0);


    }
}
