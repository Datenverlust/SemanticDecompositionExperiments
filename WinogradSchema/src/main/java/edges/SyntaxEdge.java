package edges;

import de.dailab.nsm.decomposition.graph.edges.EdgeType;
import de.dailab.nsm.decomposition.graph.edges.WeightedEdge;

/**
 * Created by Hannes on 30.03.2017.
 */
public class SyntaxEdge extends WeightedEdge {
    private String relationName;
    private String specific;

    public SyntaxEdge(){
        this.setEdgeType(EdgeType.Unknown);
    }

    public void setRelationName(String relationName) {
        this.relationName = relationName;
    }

    public String getRelationName() {
        return relationName;
    }

    public String getSpecific() {
        return specific;
    }

    public void setSpecific(String specific) {
        this.specific = specific;
    }
}
