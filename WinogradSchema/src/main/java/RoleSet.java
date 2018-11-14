import java.util.List;
import java.util.Map;

/**
 * Created by Hannes on 02.04.2017.
 */
public interface RoleSet {
    Map<String, List<String>> readRoleSet(String verb);
}
