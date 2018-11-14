/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

package basics;

import de.dailab.nsm.decomposition.graph.entities.links.WeightedLink;
import de.dailab.nsm.decomposition.graph.spreadingActivation.MarkerPassing.MarkerPassingConfig;

public class MeronymLink extends WeightedLink {

    @Override
    public double getWeight() {
        return 1.0; //TODO extend to allow configuration by markerpassingconfig-file
    }

}
