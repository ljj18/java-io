/**
 * 文件名称:          		AIOServerSocketChannelHandler.java
 * 版权所有@ 2019-2020 	无锡爱超信息技术有限公司，保留所有权利
 * 编译器:           		JDK1.8
 */

package com.ljj.io.client.aio;

import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * 这个处理器类，专门用来响应 ClientSocketChannel 的事件。 ClientSocketChannel只有一种事件：接受客户端的连接
 * 
 * Version 1.0.0
 * 
 * @author liangjinjing
 * 
 * Date 2019-04-28 18:01
 * 
 */
@SuppressWarnings("unused")
public class AIOClientSocketChannelHandler implements CompletionHandler<Void, Void> {

    /*
     * 
     */
    private IAIOClientHandler clientHandler;

    /**
     * 
     * @param serverSocketChannel
     */
    public AIOClientSocketChannelHandler(IAIOClientHandler clientHandler) {
        this.clientHandler = clientHandler;
    }

    /**
     * 
     *
     */
    @Override
    public void completed(Void resutl, Void attachment) {
        // TODO
    }

    @Override
    public void failed(Throwable exc, Void attachment) {
        clientHandler.failed(exc);
    }
}
