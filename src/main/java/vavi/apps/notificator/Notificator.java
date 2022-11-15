/*
 * Copyright (c) 2022 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.apps.notificator;


import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import vavi.util.Debug;


/**
 * Notificator.
 * <p>
 * usage
 * <pre>
 *  $ mvn package
 *  $ open target/Notificator/Notificator.app --args こんにちは
 * </pre>
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2022-07-25 nsano initial version <br>
 */
public class Notificator {

    /**
     * Check jdk version for each environment!
     * <pre>
     *         | app click on finder | run stub on commandline | open command
     * --------+---------------------+-------------------------+--------------
     * rococoa |        OK           |         crash           |     OK
     * jni     |        OK           |         crash           |     OK
     *
     * </pre>
     * @param args 0: message, 1: title, 2: sub title, 3: sound
     */
    public static void main(String[] args) throws Exception {
        String engineName = "AppleScriptRococoa";
//        String engineName = "AppleScriptEngine";
        String message = args.length > 0 ? args[0] : "Test Message";
        String title = args.length > 1 ? args[1] : "Message from Notificator";
        String subTitle = args.length > 2 ? args[2] : "Powered by " + engineName;
        String sound = args.length > 3 ? args[3] : "Frog";
        ScriptEngineManager factory = new ScriptEngineManager();
        ScriptEngine engine = factory.getEngineByName(engineName);

        String script = String.format(
                "display notification \"%s\" with title \"%s\" subtitle \"%s\" sound name \"%s\"",
                 message, title, subTitle, sound);
        Object r = engine.eval(script);
Debug.println(script);
    }
}
