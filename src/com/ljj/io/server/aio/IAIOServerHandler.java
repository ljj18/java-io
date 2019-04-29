/**
 * 文件名称:          		IAIOServerHandler.java
 * 版权所有@ 2019-2020 	无锡爱超信息技术有限公司，保留所有权利
 * 编译器:           		JDK1.8
 */

package com.ljj.io.server.aio;

import java.nio.channels.AsynchronousServerSocketChannel;

import com.ljj.io.server.IOServerLifecycle;
import com.ljj.io.server.IServerContext;

/**
 * TODO: 文件注释
 * 
 * Version 1.0.0
 * 
 * @author liangjinjing
 * 
 * Date 2019-04-29 12:36
 * 
 */
public interface IAIOServerHandler extends IOServerLifecycle {

    /**
     * 
     * @return
     */
    IServerContext getContext();
    
    /**
     * 连接、读、写失败
     * @param exc
     */
    void failed(Throwable exc);
    
    /**
     * 获得AIO socketChannel
     * @return
     */
    AsynchronousServerSocketChannel getServerSocketChannel();
}
