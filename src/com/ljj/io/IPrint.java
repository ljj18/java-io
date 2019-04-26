/**
 * 文件名称:          		IPrint.java
 * 版权所有@ 2019-2020 	无锡爱超信息技术有限公司，保留所有权利
 * 编译器:           		JDK1.8
 */

package com.ljj.io;

/**
 * 输出字符
 * 
 * Version		1.0.0      
 * 
 * @author		liangjinjing
 * 
 * Date			2019-04-26 16:18
 * 
 */
public interface IPrint {

    /**
     * 输出服务接收到的字符
     * @param msg
     */
    void onPrintServer(String msg);
   
    /**
     * 输出客户端接收到字符
     * @param msg
     */
    void onPrintClient(String msg);
}
