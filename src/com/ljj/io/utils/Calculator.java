
package com.ljj.io.utils;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public enum Calculator {
    Instance;
    private final static ScriptEngine jse = new ScriptEngineManager().getEngineByName("JavaScript");
    public Object cal(String expression) {
        try {
            return jse.eval(expression);
        } catch (ScriptException e) {
            return "表达式错误！！！！";
        }
    }
}