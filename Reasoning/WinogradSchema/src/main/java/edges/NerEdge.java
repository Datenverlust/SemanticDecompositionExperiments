/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

package edges;

import de.dailab.nsm.decomposition.graph.edges.EdgeType;
import de.dailab.nsm.decomposition.graph.edges.WeightedEdge;

/**
 * Created by Hannes on 30.03.2017.
 */
public class NerEdge extends WeightedEdge {
    private String relationName;

    public NerEdge(){
        this.setEdgeType(EdgeType.Unknown);
    }

    public void setRelationName(String relationName) {
        this.relationName = relationName;
    }

    public String getRelationName() {
        return relationName;
    }
} 