package links;

import de.dailab.nsm.decomposition.graph.entities.links.WeightedLink;

/**
 * Created by Hannes on 30.03.2017.
 */
public class RoleLink extends WeightedLink {
    @Override
    public double getWeight() {
        if(this.getType()=="vnRole") return markerPassingConfig.getVnRoleLinkWeight();
        else return markerPassingConfig.getRoleLinkWeight();
    }
    private String type;
    public String getType(){
        return this.type;
    }
    public void setType(String type){
        this.type=type;
    }

    public RoleLink(String type){
        this.type=type;
    }
}