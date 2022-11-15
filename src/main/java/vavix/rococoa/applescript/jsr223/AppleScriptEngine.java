/*
 * Copyright (c) 2022 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavix.rococoa.applescript.jsr223;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.logging.Level;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptException;
import javax.script.SimpleBindings;
import javax.script.SimpleScriptContext;

import org.rococoa.Foundation;
import org.rococoa.ObjCObjectByReference;
import org.rococoa.cocoa.foundation.NSDictionary;
import org.rococoa.cocoa.foundation.NSProcessInfo;

import vavi.util.ByteUtil;
import vavi.util.Debug;

import vavix.rococoa.foundation.AEConverter;
import vavix.rococoa.foundation.NSAppleEventDescriptor;
import vavix.rococoa.foundation.NSAppleScript;


/**
 * AppleScriptEngine.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 220302 nsano make the initial version <br>
 */
public class AppleScriptEngine implements ScriptEngine {

    /** */
    private static final String __ENGINE_VERSION__ = "0.0 release v1";
    /** */
    private static final String MY_NAME = "AppleScriptRococoa";
    /** */
    private static final String MY_SHORT_NAME = "apple";
    /** */
    private static final String STR_THISLANGUAGE = "AppleScript";

    /** */
    private ScriptEngineFactory factory;

    /** */
    private ScriptContext defaultContext;

    private static AEConverter converter = new AEConverter();

    static void checkSecurity() {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) securityManager.checkExec("/usr/bin/osascript");
    }

    /** */
    public AppleScriptEngine(AppleScriptEngineFactory factory) {
        this.factory = factory;
        defaultContext = new SimpleScriptContext();
        // set special values
        put(LANGUAGE_VERSION, "2.7");
        put(LANGUAGE, STR_THISLANGUAGE);
        put(ENGINE, MY_NAME);
        put(ENGINE_VERSION, __ENGINE_VERSION__);
        put(ARGV, ""); // TO DO: set correct value
        put(FILENAME, ""); // TO DO: set correct value
        put(NAME, MY_SHORT_NAME);
        /*
         * I am not sure if this is correct; we need to check if
         * the name really is THREADING. I have no idea why there is
         * no constant as for the other keys
         */
        put("THREADING", null);
    }

    @Override
    public Object eval(String script) throws ScriptException {
        return eval(script, getContext());
    }

    @Override
    public Object eval(String script, ScriptContext context) throws ScriptException {
        checkSecurity();

        ObjCObjectByReference error = new ObjCObjectByReference();

        try {
            NSAppleEventDescriptor desc = Foundation.callOnMainThread(() -> {
                NSAppleScript as = NSAppleScript.createWithSource(script);

                NSAppleEventDescriptor event = toNSAppleEventDescriptor(context);
                NSAppleEventDescriptor r = as.executeAppleEvent_error(event, error);
Debug.println(Level.FINE, "event: " + r + "\n" + (r == null ? error.getValueAs(NSDictionary.class).toString() : ""));

                return as.executeAndReturnError(error);
            });
            if (desc != null) {
Debug.printf(Level.FINE, "%08x, %s, %s", desc.descriptorType(), Arrays.toString(ByteUtil.getBeBytes(desc.descriptorType())), new String(ByteUtil.getBeBytes(desc.descriptorType())));
                return converter.toJava(desc);
            } else {
                NSDictionary d = error.getValueAs(NSDictionary.class);
                throw new ScriptException(d.toString());
            }
        } catch (Exception e) {
            throw new ScriptException(e);
        }
    }

    @Override
    public Object eval(String script, Bindings bindings) throws ScriptException {
        Bindings current = getContext().getBindings(ScriptContext.ENGINE_SCOPE);
        getContext().setBindings(bindings, ScriptContext.ENGINE_SCOPE);
        Object result = eval(script);
        getContext().setBindings(current, ScriptContext.ENGINE_SCOPE);
        return result;
    }

    @Override
    public Object eval(Reader reader) throws ScriptException {
        return eval(getScriptFromReader(reader));
    }

    @Override
    public Object eval(Reader reader, ScriptContext scriptContext) throws ScriptException {
        return eval(getScriptFromReader(reader), scriptContext);
    }

    @Override
    public Object eval(Reader reader, Bindings bindings) throws ScriptException {
        return eval(getScriptFromReader(reader), bindings);
    }

    @Override
    public void put(String key, Object value) {
        getBindings(ScriptContext.ENGINE_SCOPE).put(key, value);
    }

    @Override
    public Object get(String key) {
        return getBindings(ScriptContext.ENGINE_SCOPE).get(key);
    }

    @Override
    public Bindings getBindings(int scope) {
        return getContext().getBindings(scope);
    }

    @Override
    public void setBindings(Bindings bindings, int scope) {
        getContext().setBindings(bindings, scope);
    }

    @Override
    public Bindings createBindings() {
        return new SimpleBindings();
    }

    @Override
    public ScriptContext getContext() {
        return defaultContext;
    }

    @Override
    public void setContext(ScriptContext context) {
        defaultContext = context;
    }

    @Override
    public ScriptEngineFactory getFactory() {
        return factory;
    }

    /** */
    private static String getScriptFromReader(Reader reader) {
        try {
            StringWriter script = new StringWriter();
            int data;
            while ((data = reader.read()) != -1) {
                script.write(data);
            }
            script.flush();
            return script.toString();
        } catch (IOException e) {
e.printStackTrace(System.err);
            return null;
        }
    }

    /** java -> ae */
    private NSAppleEventDescriptor toNSAppleEventDescriptor(ScriptContext context) {

        Object value = context.getAttribute("javax.script.function", ScriptContext.ENGINE_SCOPE);
        String function = value != null ? (String) value : null;

        value = context.getAttribute("javax.script.argv", ScriptContext.ENGINE_SCOPE);
        Object args = value;

        int pid = NSProcessInfo.processInfo().processIdentifier();
Debug.println(Level.FINE, "pid: " + pid);

        NSAppleEventDescriptor targetAddress = NSAppleEventDescriptor.descriptorWithDescriptorType_bytes_length(
            NSAppleEventDescriptor.typeKernelProcessID,
            ByteUtil.getBeBytes(pid),
            Integer.BYTES);

        // create the event to call a subroutine in the script
        NSAppleEventDescriptor event = NSAppleEventDescriptor.alloc().initWithEventClass_eventID_targetDescriptor_returnID_transactionID(
            NSAppleEventDescriptor.kASAppleScriptSuite,
            NSAppleEventDescriptor.kASSubroutineEvent,
            targetAddress,
            NSAppleEventDescriptor.kAutoGenerateReturnID,
            NSAppleEventDescriptor.kAnyTransactionID);

        // set up the handler
        if (function != null) {
            NSAppleEventDescriptor subroutineDescriptor = NSAppleEventDescriptor.descriptorWithString(
                function.toLowerCase());
            event.setParamDescriptor_forKeyword(subroutineDescriptor, NSAppleEventDescriptor.keyASSubroutineName);
        }

        // set up the arguments
        if (args != null) {
            event.setParamDescriptor_forKeyword(converter.toAe(args), NSAppleEventDescriptor.keyDirectObject);
        }

        return event;
    }
}

/* */
