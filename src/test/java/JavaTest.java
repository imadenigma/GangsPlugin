import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import junit.framework.TestCase;

import java.util.HashSet;
import java.util.Set;


public class JavaTest extends TestCase {

    public void test1() {
        Set<String> virtualNames = Sets.newHashSet();
        virtualNames.add("imad");
        virtualNames.add("im");
        virtualNames.add("ima");
        virtualNames.add("imd");
        Gson gson = new Gson();
        String waw = gson.toJson(virtualNames,new TypeToken<HashSet<String>>(){}.getType());
        System.out.println(waw);

        Set<String > ok = gson.fromJson(waw,new TypeToken<Set<String>>(){}.getType());


    }

}
