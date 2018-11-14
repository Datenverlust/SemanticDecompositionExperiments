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
public class NerLink extends WeightedLink {

    @Override
    public double getWeight() {
        return markerPassingConfig.getNerLinkWeight();
    }


}