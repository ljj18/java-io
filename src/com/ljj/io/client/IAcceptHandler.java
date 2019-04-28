/**
 * 文件名称:          		ISendCompletionHandler.java
 * 版权所有@ 2019-2020 	无锡爱超信息技术有限公司，保留所有权利
 * 编译器:           		JDK1.8
 */

package com.ljj.io.client;

/**
 * 发送数据Handler
 * 
 * Version		1.0.0      
 * 
 * @author		liangjinjing
 * 
 * Date			2019-04-28 14:29
 * 
 */
public interface IAcceptHandler {
    
    /**
     * 接收的数据
     * @param t
     */
    boolean onAccept(byte[] b);
}
