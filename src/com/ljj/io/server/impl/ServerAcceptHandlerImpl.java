/**
 * 文件名称:          		AcceptHandlerImpl.java
 * 版权所有@ 2019-2020 	无锡爱超信息技术有限公司，保留所有权利
 * 编译器:           		JDK1.8
 */

package com.ljj.io.server.impl;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import com.ljj.io.server.IServerAcceptHandler;

/**
 * TODO: 文件注释
 * 
 * Version 1.0.0
 * 
 * @author liangjinjing
 * 
 * Date 2019-04-28 15:54
 * 
 */
public class ServerAcceptHandlerImpl implements IServerAcceptHandler {

    private final static ScriptEngine jse = new ScriptEngineManager().getEngineByName("JavaScript");
    //
    private final static String EXPRESSION_ERROR = "表达式不合法!!!";

    @Override
    public String onAccept(byte[] b) {
        if (b == null || b.length == 0) {
            System.out.println("接收的数据：空串");
            return EXPRESSION_ERROR;
        }
        try {
            return onAcceptByString(new String(b, "utf-8"));
        } catch (Exception e) {
            return EXPRESSION_ERROR;
        }
    }

    
    /**
     * 
     * @param str
     * @return
     */
    public String onAcceptByString(String str) {
        if (str == null || str.length() == 0) {
            System.out.println("接收的数据：空串");
            return EXPRESSION_ERROR;
        }
        try {
            System.out.println("接收的数据：" + str);
            return String.valueOf(jse.eval(str));
        } catch (Exception e) {
            return EXPRESSION_ERROR;
        }
    }
}
