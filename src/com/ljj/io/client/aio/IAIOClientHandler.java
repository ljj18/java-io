/**
 * 文件名称:          		IAIOClientHandler.java
 * 版权所有@ 2019-2020 	无锡爱超信息技术有限公司，保留所有权利
 * 编译器:           		JDK1.8
 */

package com.ljj.io.client.aio;

import java.nio.channels.AsynchronousSocketChannel;

import com.ljj.io.client.IClientContext;
import com.ljj.io.client.IOClientLifecycle;

/**
 * 
 * 
 * Version		1.0.0      
 * 
 * @author		liangjinjing
 * 
 * Date			2019-04-29 13:06
 * 
 */
public interface IAIOClientHandler extends IOClientLifecycle {

    /**
     * 获得上下文
     * @return
     */
    IClientContext getContext();
    
    /**
     * 连接、读、写失败
     * @param exc
     */
    void failed(Throwable exc);
    
    /**
     * 获得AIO socketChannel
     * @return
     */
    AsynchronousSocketChannel getSocketChannel();
}
