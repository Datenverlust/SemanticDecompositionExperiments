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