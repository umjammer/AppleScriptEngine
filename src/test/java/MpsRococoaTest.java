
import java.io.FileReader;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import vavi.util.Debug;

import static org.junit.jupiter.api.Assertions.assertTrue;


@EnabledOnOs(OS.MAC)
class MpsRococoaTest {

    @Test
    @DisplayName("the same test as the jni version")
    void test() throws Exception {
        ScriptEngineManager factory = new ScriptEngineManager();

        // Create a AppleScript engine.
        ScriptEngine engine = factory.getEngineByName("AppleScriptRococoa");
Debug.println("engine: " + engine.getClass());
        assertTrue(engine.getClass().getPackage().getName().toString().startsWith("vavix.rococoa"));

        FileReader rdr = new FileReader("src/test/resources/mps.as");
        @SuppressWarnings("unchecked")
        List<List<?>> procs = (List<List<?>>) engine.eval(rdr);
Debug.println(procs);
        assertTrue(procs.size() > 0);
        for (List<?> proc : procs) {
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
