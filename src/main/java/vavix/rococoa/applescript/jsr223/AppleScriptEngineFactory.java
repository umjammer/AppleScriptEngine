/*
 * Copyright (c) 2008 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavix.rococoa.applescript.jsr223;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.script.ScriptEngineFactory;


/**
 * ScriptEngineFactory.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 080602 nsano make the initial version <br>
 */
public class AppleScriptEngineFactory implements ScriptEngineFactory {

    /** */
    private static final String FILEEXT = ".scpt";

    /** */
    private static final String [] MIMETYPES = {
        "text/plain",
        "text/application/x-applescript",
        "application/application/x-applescript"
    };

    /** */
    private static final String [] NAMES = {
        "AppleScriptRococoa",
        "AppleScript",
        "applescriptrococoa",
        "applescript"
    };

    /** */
    private AppleScriptEngine scriptEngine;
    /** */
    private List<String> extensions;
    /** */
    private List<String> mimeTypes;
    /** */
    private List<String> names;

    static {
        java.awt.Toolkit.getDefaultToolkit();
    }

    /** */
    public AppleScriptEngineFactory() {
        scriptEngine = new AppleScriptEngine(this);
        extensions = Collections.nCopies(1, FILEEXT);
        mimeTypes = Arrays.asList(MIMETYPES);
        names = Arrays.asList(NAMES);
    }

    @Override
    public String getEngineName() {
        return getScriptEngine().get(AppleScriptEngine.ENGINE).toString();
    }

    @Override
    public String getEngineVersion() {
        return getScriptEngine().get(AppleScriptEngine.ENGINE_VERSION).toString();
    }

    @Override
    public List<String> getExtensions() {
        return extensions;
    }

    @Override
    public List<String> getMimeTypes() {
        return mimeTypes;
    }

    @Override
    public List<String> getNames() {
        return names;
    }

    @Override
    public String getLanguageName() {
        return getScriptEngine().get(AppleScriptEngine.LANGUAGE).toString();
    }

    @Override
    public String getLanguageVersion() {
        return getScriptEngine().get(AppleScriptEngine.LANGUAGE_VERSION).toString();
    }

    @Override
    public Object getParameter(String key) {
        return getScriptEngine().get(key).toString();
    }

    @Override
    public String getMethodCallSyntax(String obj, String m, String... args)  {
        StringBuilder sb = new StringBuilder();
        sb.append(obj);
        sb.append(" ");
        int len = args.length;
        for (int i = 0; i < len; i++) {
            if (i > 0) {
                sb.append(' ');
            }
            sb.append(args[i]);
        }
        return sb.toString();
    }

    @Override
    public String getOutputStatement(String toDisplay) {
        return "log \"" + toDisplay + "\"";
    }

    @Override
    public String getProgram(String ... statements) {
        StringBuilder sb = new StringBuilder();
        int len = statements.length;
        for (int i = 0; i < len; i++) {
            if (i > 0) {
                sb.append('\n');
            }
            sb.append(statements[i]);
        }
        return sb.toString();
    }

    @Override
    public AppleScriptEngine getScriptEngine() {
        return scriptEngine;
    }
}

/* */
