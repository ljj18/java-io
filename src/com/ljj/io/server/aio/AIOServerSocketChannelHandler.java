/**
 * 文件名称:          		AIOServerSocketChannelHandler.java
 * 版权所有@ 2019-2020 	无锡爱超信息技术有限公司，保留所有权利
 * 编译器:           		JDK1.8
 */

package com.ljj.io.server.aio;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * 这个处理器类，专门用来响应 ServerSocketChannel 的事件。
 * ServerSocketChannel只有一种事件：接受客户端的连接
 * 
 * Version		1.0.0      
 * 
 * @author		liangjinjing
 * 
 * Date			2019-04-28 18:01
 * 
 */
public class AIOServerSocketChannelHandler implements CompletionHandler<AsynchronousSocketChannel, Void>{
    
    /*
     * 
     */
    private IAIOServerHandler serverHandler;
    
    /**
     * 
     * @param serverSocketChannel
     */
    public AIOServerSocketChannelHandler(IAIOServerHandler serverHandler) {
       this.serverHandler = serverHandler;
    }
    
    @Override
    public void completed(AsynchronousSocketChannel socketChannel, Void attachment) {
        // 每次都要重新注册监听（一次注册，一次响应），但是由于“文件状态标示符”是独享的，所以不需要担心有“漏掉的”事件
        serverHandler.getServerSocketChannel().accept(attachment, this);
        // 创建新的Buffer
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        /*
         * 为这个新的socketChannel注册“read”事件，以便操作系统在收到数据并准备好后，主动通知应用程序
         * 在这里，由于我们要将这个客户端多次传输的数据累加起来一起处理，所以我们将一个StringBuffer对象作为一个“附件”依附在这个channel上
         */
        socketChannel.read(buffer, new StringBuffer(), new AIOServerSocketChannelReadHandler(socketChannel, serverHandler, buffer));
    }

    @Override
    public void failed(Throwable exc, Void attachment) {
        serverHandler.failed(exc);
    }

}
