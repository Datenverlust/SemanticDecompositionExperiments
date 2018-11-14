package edges;

import de.dailab.nsm.decomposition.graph.edges.EdgeType;
import de.dailab.nsm.decomposition.graph.edges.WeightedEdge;

/**
 * Created by Hannes on 30.03.2017.
 */
public class RoleEdge extends WeightedEdge {
    private String roleType;

    public RoleEdge(){
        this.setEdgeType(EdgeType.Unknown);
    }

    public void setRoleType(String relationName) {
        this.roleType = relationName;
    }

    public String getRoleType() {
        return roleType;
    }
}
