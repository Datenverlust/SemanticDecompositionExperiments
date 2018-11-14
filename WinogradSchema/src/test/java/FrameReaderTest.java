import java.util.List;
import java.util.Map;
import java.util.SortedMap;

/**
 * Created by Hannes on 02.04.2017.
 */
public class FrameReaderTest{
    public static void main(String[] args){
        RoleSet roleSet=new FrameReader();
        Map<String, List<String>> newRoleSet= roleSet.readRoleSet("siot.09");

        if(newRoleSet==null || newRoleSet.size()==0) System.out.println("Ups, da hat etwas nicht geklappt.");
        else {
            for (String key : newRoleSet.keySet()) {
                System.out.println(key);
                for (String element : newRoleSet.get(key)) {
                    System.out.println("   " + element);
                }
                System.out.println();
            }
        }
    }
}
