/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

package test;

import basics.*;
import de.dailab.nsm.decomposition.Concept;
import de.dailab.nsm.decomposition.graph.entities.links.*;
import de.tuberlin.spreadalgo.*;
import org.junit.*;
import java.util.*;
import static org.junit.Assert.assertEquals;

public class PathMarkerTest {

    private PathMarker marker1;
    private PathMarker marker2;
    private Concept concept1;
    private Concept concept2;
    private Concept concept3;
    private Link link1;
    private Link link2;
    private PathNode node1;
    private PathNode node2;
    private PathNode node3;
    List<Marker> markerList;

    @Before
    public void setUp(){
        concept1=new Concept();
        concept1.setLemma("concept1");
        concept2=new Concept();
        concept2.setLemma("concept2");
        concept3=new Concept();
        concept3.setLemma("concept3");
        node1 = new PathNode(concept1);
        node2 = new PathNode(concept2);
        node3 = new PathNode(concept3);
        link1=new HyponymLink();
        link1.setSource(node1);
        link1.setTarget(node2);
        link2=new SynonymLink();
        link2.setSource(node2);
        link2.setTarget(node3);
        markerList=new ArrayList<>();
        marker1=createMarker(concept1);
    }

    public PathMarker createMarker(Concept concept){
        PathMarker marker = new PathMarker(concept);
        markerList.add(marker);
        return marker;
    }
    public PathMarker createMarker(PathMarker marker, Link link){
        PathMarker newMarker = new PathMarker(marker,link);
        markerList.add(newMarker);
        return newMarker;
    }
    @Test
    public void toTestCorrectMarkerProduktion(){
        assertEquals(markerList.size(),1);
        assertEquals(marker1.getOrigin().getLemma().toString(),"concept1");
    }

    @Test
    public void toTestMarkerHistoryGrowsAfterPassing(){
        marker2=createMarker(marker1,link1);
        assertEquals(markerList.size(),2);
        assertEquals(marker2.getOrigin().getLemma().toString(),marker1.getOrigin().getLemma().toString());
    }
}
