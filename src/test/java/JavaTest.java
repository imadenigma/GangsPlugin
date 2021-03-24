import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import junit.framework.TestCase;
import me.imadenigma.gangsplugin.gangs.GangManager;
import me.imadenigma.gangsplugin.utils.Utils;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


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

    }

    public void test2() {
        Map<String,Long> map = Maps.newHashMap();
        final String imad = "qwertyuiosadjsaifh SF6Wq0frqwrwqohr 12 [8ry1289r 91rj129ibfjfvds ";
        for (int i = 0; i < 65; i++) {
            map.put(String.valueOf(imad.charAt(i)), (long) i);
        }
        //map.entrySet().stream().sorted(Map.Entry.comparingByValue()).forEach(System.out::println);
        Utils.INSTANCE.copyContent(new File("src/main/resources/","language.yml"),
                new File("src/main/resources/","mok.yml"));
    }




}
