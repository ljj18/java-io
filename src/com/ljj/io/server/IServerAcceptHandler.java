/**
 * 文件名称:          		IAcceptHandler.java
 * 版权所有@ 2019-2020 	无锡爱超信息技术有限公司，保留所有权利
 * 编译器:           		JDK1.8
 */

package com.ljj.io.server;

/**
 * 
 * 
 * Version		1.0.0      
 * 
 * @author		liangjinjing
 * 
 * Date			2019-04-28 15:06
 * 
 */
public interface IServerAcceptHandler {

    /**
     * 接收到的字符
     * @param b
     */
    String onAccept(byte[] b);
}
