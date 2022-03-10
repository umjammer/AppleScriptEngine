/*
 * Copyright (c) 2022 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.nio.file.Files;
import java.nio.file.Paths;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.rococoa.Foundation;
import org.rococoa.ObjCObjectByReference;
import org.rococoa.cocoa.foundation.NSDictionary;

import vavi.util.Debug;

import vavix.rococoa.foundation.NSAppleEventDescriptor;
import vavix.rococoa.foundation.NSAppleScript;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * Test1.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2022/03/02 umjammer initial version <br>
 */
@EnabledOnOs(OS.MAC)
public class TestRococa {

    @Test
    @DisplayName("return takes long time. why??? -> run on main thread")
    void test1() throws Exception {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) securityManager.checkExec("/usr/bin/osascript");

        ObjCObjectByReference outError = new ObjCObjectByReference();
        NSAppleEventDescriptor desc = Foundation.callOnMainThread(() -> {
            String command = "beep";
            NSAppleScript as = NSAppleScript.createWithSource(command);
Debug.println("here1");
            return as.executeAndReturnError(outError);
        });
        if (desc == null) {
            NSDictionary error = outError.getValueAs(NSDictionary.class);
Debug.println("error: " + error);
        } else {
Debug.println(desc);
        }
Debug.println("Done");
    }

    @Test
    @DisplayName("return value")
    void test2() throws Exception {
        ScriptEngineManager sem = new ScriptEngineManager();
        ScriptEngine engine = sem.getEngineByName("AppleScriptRococoa");
Debug.println("engine: " + engine.getClass());
        assertTrue(engine.getClass().getPackage().getName().toString().startsWith("vavix.rococoa"));

        String statement = "1 + 2";
        Object r = engine.eval(statement);
        assertEquals(3, r);
Debug.println("Done: " + r);
    }

    @Test
    @DisplayName("null return value")
    void test3() throws Exception {
        ScriptEngineManager sem = new ScriptEngineManager();
        ScriptEngine engine = sem.getEngineByName("AppleScriptRococoa");
Debug.println("engine: " + engine.getClass());
        assertTrue(engine.getClass().getPackage().getName().toString().startsWith("vavix.rococoa"));

        String statement = "beep";
        Object r = engine.eval(statement);
        assertNull(r);
Debug.println("Done: " + r);
    }

    @Test
    void test4() throws Exception {
        ScriptEngineManager sem = new ScriptEngineManager();
        ScriptEngine engine = sem.getEngineByName("AppleScriptRococoa");
Debug.println("engine: " + engine.getClass());
        assertTrue(engine.getClass().getPackage().getName().toString().startsWith("vavix.rococoa"));

        String statement =
                "display notification \"All graphics have been converted.\" " +
                "with title \"My Graphic Processing Script\" " +
                "subtitle \"Processing is complete.\" " +
                "sound name \"Frog\"";
        Object r = engine.eval(statement);
        Thread.sleep(5000);
Debug.println("Done: " + r);
    }

    @Test
    @Disabled("takes 5 min")
    void test5() throws Exception {
        String script = new String(Files.readAllBytes(Paths.get(TestRococa.class.getResource("/test1.as").toURI())));
        ScriptEngineManager sem = new ScriptEngineManager();
        ScriptEngine engine = sem.getEngineByName("AppleScriptRococoa");
Debug.println("engine: " + engine.getClass());
        assertTrue(engine.getClass().getPackage().getName().toString().startsWith("vavix.rococoa"));

        Object r = engine.eval(script);
Debug.println("Done: " + r);
    }

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
//        String command = "log \"hello world\n\"";
//        String command = "display dialog \"hello world\"";
//        String command = "tell applicatin Chrome.app\r" +
//                          "beep\r" +
//                          "end tell\n";
        String command = "beep";
//        final String command = "tell application \"System Preferences\"\n" +
//                "activate\n" +
//                "reveal anchor \"Proxies\" of pane \"com.apple.preference.network\"\n" +
//                "end tell";
//        Path p = Paths.get(Main1.class.getResource("/mps.as").toURI());
//        String command = String.join("\n", Files.readAllLines(p).toArray(new String[0]));
Debug.println("\n" + command);

        NSAppleScript as = NSAppleScript.createWithSource(command);
Debug.println("here1");
        ObjCObjectByReference outError = new ObjCObjectByReference();
Debug.println("here2");
        NSAppleEventDescriptor desc = as.executeAndReturnError(outError);
Debug.println("here3");
        if (desc == null) {
            NSDictionary error = outError.getValueAs(NSDictionary.class);
Debug.println("error: " + error);
        } else {
Debug.println(desc);
        }
Debug.println("Done");
    }
}

/* */
