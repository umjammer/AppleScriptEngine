/*
 * Copyright (c) 2022 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.junit.jupiter.api.Test;

import vavi.util.Debug;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test1.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2022/03/03 umjammer initial version <br>
 */
class Test1 {

    /**
     * TODO notification needs to make it application
     * @see vavi.apps.notificator.Notificator
     */
    @Test
    void test1() throws Exception {
        ScriptEngineManager factory = new ScriptEngineManager();
        ScriptEngine engine = factory.getEngineByName("AppleScriptRococoa");
Debug.println("engine: " + engine.getClass());
        assertTrue(engine.getClass().getPackage().getName().toString().startsWith("vavix.rococoa"));

        String script =
                "display notification \"All graphics have been converted.\" " +
                "with title \"My Graphic Processing Script\" " +
                "subtitle \"Processing is complete.\" " +
                "sound name \"Frog\"";
        Object r = engine.eval(script);
Debug.println(r);
    }
}

/* */
