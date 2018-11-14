/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

package links;

import de.dailab.nsm.decomposition.graph.entities.links.WeightedLink;

/**
 * Created by Hannes on 30.03.2017.
 */
public class SyntaxLink extends WeightedLink {

    @Override
    public double getWeight() {
        if(this.getSpecific()=="but" || this.getSpecific()=="although") return markerPassingConfig.getContrastLinkWeight();
        else return markerPassingConfig.getSyntaxLinkWeight();
    }
    private String name;
    private String specific;

    public String getSpecific() {
        return specific;
    }
    public void setSpecific(String specific) {
        this.specific = specific;
    }

    public String getName(){
        return this.name;
    }
    public void setName(String name){
        this.name=name;
    }

    public SyntaxLink(String name){
        this.name=name;
    }
}