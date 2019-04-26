
package com.ljj.io.aio.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import com.ljj.io.IContext;
import com.ljj.io.IOLifecycle;

public class AIOAsyncClientHandler implements CompletionHandler<Void, AIOAsyncClientHandler>, IOLifecycle, Runnable {
    /*
     * 
     */
    private int port;
    /*
     * 
     */
    private String host;
    /*
     * 
     */
    private IContext context;
    /*
     * 
     */
    private AsynchronousSocketChannel clientChannel;
    /*
     * 
     */
    private CountDownLatch latch;
    /*
     * 
     */
    private AtomicBoolean isRunning = new AtomicBoolean(false);

    public AIOAsyncClientHandler(IContext context, String host, int port) {
        this.context = context;
        this.host = host;
        this.port = port;
        try {
            /**
             * 创建异步的客户端通道
             */
            clientChannel = AsynchronousSocketChannel.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start() {
        if (isRunning.compareAndSet(false, true)) {
            // 创建CountDownLatch等待
            latch = new CountDownLatch(1);
            // 发起异步连接操作，回调参数就是这个类本身，如果连接成功会回调completed方法
            clientChannel.connect(new InetSocketAddress(host, port), this, this);
            try {
                latch.await();
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
    }

    @Override
    public void stop() {
        // TODO Auto-generated method stub
        if (isRunning.compareAndSet(true, false)) {
            if (clientChannel != null) {
                try {
                    clientChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void run() {
        start();
    }

    
    @Override
    public void completed(Void result, AIOAsyncClientHandler attachment) {
        
    }

    @Override
    public void failed(Throwable exc, AIOAsyncClientHandler attachment) {
        exc.printStackTrace();
        try {
            clientChannel.close();
            latch.countDown();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 
     * @param msg
     */
    public void sendMsg(String msg) {
        byte[] req = msg.getBytes();
        ByteBuffer writeBuffer = ByteBuffer.allocate(req.length);
        writeBuffer.put(req);
        writeBuffer.flip();
        // 异步写
        clientChannel.write(writeBuffer, writeBuffer, new WriteHandler(context, clientChannel, latch));
    }

}
