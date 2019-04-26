
package com.ljj.io.aio.server;

import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

import com.ljj.io.IContext;

/**
 * 作为handler接收客户端连接
 * 
 * @author liangjinjing
 *
 */
public class AcceptHandler implements CompletionHandler<AsynchronousSocketChannel, AIOAsyncServerHandler> {
    
    /*
     * 
     */
    private IContext context;
    
    public AcceptHandler(IContext context) {
        this.context = context;
    }
    
    @Override
    public void completed(AsynchronousSocketChannel channel, AIOAsyncServerHandler serverHandler) {
        /*serverHandler.channel.accept(serverHandler, this);
        // 创建新的Buffer
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        // 异步读 第三个参数为接收消息回调的业务Handler
        channel.read(buffer, buffer, new AIOReadHandler(channel));*/
    }

    @Override
    public void failed(Throwable exc, AIOAsyncServerHandler serverHandler) {
        /*exc.printStackTrace();
        serverHandler.latch.countDown();*/
    }
}