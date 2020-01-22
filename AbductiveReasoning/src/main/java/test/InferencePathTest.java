/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

package test;

import de.kimanufaktur.markerpassing.Link;
import de.kimanufaktur.nsm.graph.entities.links.AntonymLink;
import de.kimanufaktur.nsm.graph.entities.links.HyponymLink;
import de.kimanufaktur.nsm.graph.entities.links.SynonymLink;
import de.kimanufaktur.nsm.reasoning.InferencePath;
import de.kimanufaktur.nsm.reasoning.MeronymLink;

import org.junit.Before;
import org.junit.Test;


import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class InferencePathTest {

    ArrayList<Link> elaborationLinks;
    InferencePath path;
    @Before
    public void setUp(){
        elaborationLinks = new ArrayList<Link>();
        path=new InferencePath();
    }

    public boolean matchesWhiteList(List<Link> linkList){
        linkList.forEach(link ->{
            path.addLinkToPath(link);
        });
        return InferencePath.matchesWhiteList(path);
    }
    @Test
    public void toCheckIfPathOfSingleLinkMatchesWhiteList(){
        elaborationLinks.add(new HyponymLink());
        assertEquals(matchesWhiteList(elaborationLinks),true);
    }

    @Test
    public void toCheckIfWhiteListPathMatchesWhiteList(){
        elaborationLinks.add(new HyponymLink());
        elaborationLinks.add(new SynonymLink());
        assertEquals(matchesWhiteList(elaborationLinks),true);
    }

    @Test
    public void checkIfPathOfSingleLinkMatchesWhiteList(){
        elaborationLinks.add(new HyponymLink());
        elaborationLinks.add(new SynonymLink());
        elaborationLinks.add(new HyponymLink());
        assertEquals(matchesWhiteList(elaborationLinks),false);
        assertEquals(path.getInferencePath().size(), 3);
    }

    @Test
    public void toTestIfInferencePathChancesWhenMultipleSameTypelinksAreTraversed(){
        elaborationLinks.add(new HyponymLink());
        elaborationLinks.add(new HyponymLink());
        elaborationLinks.add(new HyponymLink());
        elaborationLinks.add(new HyponymLink());
        elaborationLinks.add(new HyponymLink());
        elaborationLinks.add(new SynonymLink());
        assertEquals(matchesWhiteList(elaborationLinks),true);
        assertEquals(path.getInferencePath().size(), 2);
    }


    @Test
    public void toTestContainLinkTypeFunktion(){
        path.addLinkToPath(new HyponymLink());
        assertTrue(path.containsHyponymLink);
        assertFalse(path.containsAntonymLink);
        assertFalse(path.containsArbitraryRelationsLink);
        assertFalse(path.containsDefintionLink);
        assertFalse(path.containsSynonymLink);
        assertFalse(path.containsHypernymLink);
        path.addLinkToPath(new AntonymLink());
        assertTrue(path.containsAntonymLink);
    }
    @Test
    public void toTestMeronymLink(){
        path.addLinkToPath(new MeronymLink());
        assertTrue(path.containsMeronymLink);
        assertTrue(InferencePath.matchesWhiteList(path));
    }

}
