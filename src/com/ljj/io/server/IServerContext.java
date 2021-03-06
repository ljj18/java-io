/**
 * 文件名称:          		IContext.java
 * 版权所有@ 2019-2020 	无锡爱超信息技术有限公司，保留所有权利
 * 编译器:           		JDK1.8
 */

package com.ljj.io.server;

/**
 * 服务端上下文相关
 * 
 * Version		1.0.0      
 * 
 * @author		liangjinjing
 * 
 * Date			2019-04-26 16:28
 * 
 */
public interface IServerContext {
    
    /**
     * 
     * @return
     */
    IServerAcceptHandler getAcceptHandler();

}
