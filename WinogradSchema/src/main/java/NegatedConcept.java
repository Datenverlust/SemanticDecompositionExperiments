import de.dailab.nsm.decomposition.Concept;

/**
 * Created by Hannes on 05.04.2017.
 */
public class NegatedConcept extends Concept {
    String originalName=null;

    public NegatedConcept(String litheral) {
        super(litheral+"(NEG)");
        setOriginalName(litheral);
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }
}
