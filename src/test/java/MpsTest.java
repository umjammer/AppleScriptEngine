
import java.io.FileReader;
import java.util.ArrayList;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import vavi.util.Debug;

import static org.junit.jupiter.api.Assertions.assertTrue;


@EnabledOnOs(OS.MAC)
class MpsTest {

    // on github: Not authorized to send Apple events to System Events.
    @Test
    @DisabledIfEnvironmentVariable(named = "GITHUB_WORKFLOW", matches = ".*")
    void test() throws Exception {
        ScriptEngineManager factory = new ScriptEngineManager();

        // Create a AppleScript engine.
        ScriptEngine engine = factory.getEngineByName("AppleScriptEngine");
Debug.println("engine: " + engine.getClass());
        assertTrue(engine.getClass().getPackage().getName().startsWith("apple"));

        FileReader rdr = new FileReader("src/test/resources/mps.as");
        @SuppressWarnings("unchecked")
        ArrayList<ArrayList<?>> procs = (ArrayList<ArrayList<?>>) engine.eval(rdr);
        assertFalse(procs.isEmpty());
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
