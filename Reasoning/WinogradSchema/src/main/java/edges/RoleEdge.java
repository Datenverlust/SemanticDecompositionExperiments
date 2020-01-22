/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

package edges;

import de.kimanufaktur.nsm.decomposition.graph.edges.EdgeType;
import de.kimanufaktur.nsm.decomposition.graph.edges.WeightedEdge;

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
