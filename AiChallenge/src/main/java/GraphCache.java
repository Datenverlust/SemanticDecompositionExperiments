/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

import de.dailab.nsm.decomposition.Concept;
import de.tuberlin.spreadalgo.Marker;
import org.jgrapht.Graph;

import java.util.List;
import java.util.Map;

public class GraphCache {
    Question question;
    Graph decGraph ;
    Graph syntaxGraph;
    Graph nerGraph;
    Graph roleGraph;
    Map<String, String> nerMap;
    List<Map<Concept, List<? extends Marker>>> startActivation;

    public GraphCache(Question question,Graph decGraph, Graph syntaxGraph, Graph nerGraph, Graph roleGraph,Map<String, String> nerMapList,List<Map<Concept, List<? extends Marker>>> startActivation){
        this.question = question;
        this.decGraph = decGraph;
        this.syntaxGraph = syntaxGraph;
        this.nerGraph = nerGraph;
        this.roleGraph = roleGraph;
        this.nerMap = nerMap;
        this.startActivation=startActivation;

    }

}
