/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

package de.kimanufaktur.nsm.wsd;

import de.kimanufaktur.nsm.decomposition.Concept;
import de.kimanufaktur.nsm.decomposition.Definition;
import de.kimanufaktur.nsm.decomposition.graph.spreadingActivation.MarkerPassing.TypedMarkerPassing;
import de.kimanufaktur.nsm.graph.spreadingActivation.MarkerPassing.TypedMarkerPassingConfig;
import org.jgrapht.Graph;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by faehndrich on 22.06.16.
 */


public class SAMP {

    private TypedMarkerPassingConfig config;
    private Graph sentenceGraph;
    private HashSet<Concept> initialConcepts;

    public SAMP(TypedMarkerPassingConfig config, Graph sentenceGraph, HashSet<Concept> initialConcepts) {
        this.config = config;
        this.sentenceGraph = sentenceGraph;
        this.initialConcepts = initialConcepts;
    }

    public HashMap<Concept, Definition> execute() {
        HashMap<Concept, Definition> r;
        TypedMarkerPassing markerPassing = new TypedMarkerPassing(sentenceGraph, config);
        HashMap<Concept, Double> initial = new HashMap<>();
        for (Concept c : initialConcepts) {
            initial.put(c, config.initialMarkerAmount);
        }
        markerPassing.setInitialConcepts(initial);
        markerPassing.execute();
        r = markerPassing.getDefinitionOfInitialConcepts();
        return r;
    }
}