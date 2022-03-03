
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * Copied from Scripting for the Java Platform
 * @author mjh
 * @see "http://java.sun.com/developer/technicalArticles/J2SE/Desktop/scripting/"
 */
@EnabledOnOs(OS.MAC)
class EnginesTest {

    private static final String script = "set cd to (random number 100000) mod 52 + 1 \r return cd";

    @Test
    void test() throws Exception {
//        System.out.println("engines loader used " + Thread.currentThread().getClass().getClassLoader());
//        ScriptEngineManager mgr = new ScriptEngineManager(Thread.currentThread().getClass().getClassLoader());

        ScriptEngineManager mgr = new ScriptEngineManager();
        // Create a JavaScript engine.
        List<ScriptEngineFactory> factories = mgr.getEngineFactories();
        for (ScriptEngineFactory factory : factories) {
            System.out.println("ScriptEngineFactory Info");
            System.out.println("\tFactory class:" + factory.getClass());
            String engName = factory.getEngineName();
            String engVersion = factory.getEngineVersion();
            String langName = factory.getLanguageName();
            String langVersion = factory.getLanguageVersion();
            System.out.printf("\tScript Engine: %s (%s)\n", engName, engVersion);
            List<String> engNames = factory.getNames();
            for (String name : engNames) {
                System.out.printf("\tEngine Alias: %s\n", name);
            }
            System.out.printf("\tLanguage: %s (%s)\n", langName, langVersion);
        }

        assertTrue(factories.stream().anyMatch(f -> f.getClass().equals(apple.applescript.AppleScriptEngineFactory.class)));

        ScriptEngine engine = mgr.getEngineByName("AppleScript");
        Long result = (Long) engine.eval(script);
        System.out.println("Pick a card script result: " + result);
        assertTrue(1 <= result && result <= 52);
    }
}
