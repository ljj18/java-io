
package com.ljj.io.aio.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import com.ljj.io.IContext;
import com.ljj.io.IOLifecycle;

public class AIOAsyncServerHandler
    implements CompletionHandler<AsynchronousSocketChannel, AIOAsyncServerHandler>, IOLifecycle, Runnable {
    /*
     * 
     */
    private CountDownLatch latch;
    /*
     * 
     */
    private AsynchronousServerSocketChannel channel;

    /*
     * 
     */
    private AtomicBoolean isRunning = new AtomicBoolean(false);
    /*
     * 
     */
    private IContext context;

    public AIOAsyncServerHandler(IContext context, int port) {
        this.context = context;
        try {
            // 创建服务端通道
            channel = AsynchronousServerSocketChannel.open();
            // 绑定端口
            channel.bind(new InetSocketAddress(port));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        start();
    }

    @Override
    public void start() {
        
        if (isRunning.compareAndSet(false, true)) {
            /*
             * CountDownLatch初始化,它的作用：在完成一组正在执行的操作之前，允许当前的现场一直阻塞 此处，
             * 让现场在此阻塞，防止服务端执行完成后退出,也可以使用while(true)+sleep
             */
            latch = new CountDownLatch(1);
            // 用于接收客户端的连接
            channel.accept(this, this);
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void stop() {
        context = null;
        if (isRunning.compareAndSet(true, false)) {
            if (channel != null) {
                try {
                    channel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * 
     *
     */
    @Override
    public void completed(AsynchronousSocketChannel socketChanner, AIOAsyncServerHandler serverHandler) {
        channel.accept(serverHandler, this);
        // 创建新的Buffer
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        // 异步读 第三个参数为接收消息回调的业务Handler
        socketChanner.read(buffer, buffer, new AIOReadHandler(context, socketChanner));

    }

    @Override
    public void failed(Throwable exc, AIOAsyncServerHandler serverHandler) {
        exc.printStackTrace();
        latch.countDown();
    }
}