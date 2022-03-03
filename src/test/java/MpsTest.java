
import java.io.FileReader;
import java.util.ArrayList;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import static org.junit.jupiter.api.Assertions.assertTrue;


@EnabledOnOs(OS.MAC)
class MpsTest {

    @Test
    void test() throws Exception {
        ScriptEngineManager factory = new ScriptEngineManager();
        // Create a AppleScript engine.
        ScriptEngine engine = factory.getEngineByName("AppleScript");
        FileReader rdr = new FileReader("src/test/resources/mps.as");
        @SuppressWarnings("unchecked")
        ArrayList<ArrayList<?>> procs = (ArrayList<ArrayList<?>>) engine.eval(rdr);
        assertTrue(procs.size() > 0);
        for (ArrayList<?> proc : procs) {
            String pid = proc.get(0).toString();
            String name = proc.get(1).toString();
            String path = proc.get(2).toString();
            String creator = proc.get(3).toString();
            String bundleid = proc.get(4).toString();
            if (name.length() > 10)
                System.out.println(pid + "\t" + name + "\t" + bundleid + "\t" + creator + "\t" + path);
            else
                System.out.println(pid + "\t" + name + "\t\t" + bundleid + "\t" + creator + "\t" + path);
        }
    }
}