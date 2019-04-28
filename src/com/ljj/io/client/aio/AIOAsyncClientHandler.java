
package com.ljj.io.client.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import com.ljj.io.client.IClientContext;
import com.ljj.io.server.IOServerLifecycle;

public class AIOAsyncClientHandler implements CompletionHandler<Void, AIOAsyncClientHandler>, IOServerLifecycle, Runnable {
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
    private IClientContext context;
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

    public AIOAsyncClientHandler(IClientContext context, String host, int port) {
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
    public void completed(Void result, AIOAsyncClientHandler clientHandler) {

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
        clientChannel.write(writeBuffer, writeBuffer, new CompletionHandler<Integer, ByteBuffer>(){
            @Override
            public void completed(Integer result, ByteBuffer buffer) {
                // 完成全部数据的写入
                if (buffer.hasRemaining()) {
                    clientChannel.write(buffer, buffer, this);
                } else {
                    // 读取数据
                    ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                    clientChannel.read(readBuffer, readBuffer, new CompletionHandler<Integer, ByteBuffer>(){
                        @Override
                        public void completed(Integer result, ByteBuffer buffer) {
                            buffer.flip();
                            byte[] bytes = new byte[buffer.remaining()];
                            buffer.get(bytes);
                            // 
                            context.getAcceptHandler().onAccept(bytes);
                        }
                        @Override
                        public void failed(Throwable exc, ByteBuffer attachment) {
                            try {
                                clientChannel.close();
                                latch.countDown();
                            } catch (IOException e) {
                            }
                        }
                    });
                }
            }

            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {
                try {
                    clientChannel.close();
                    latch.countDown();
                } catch (IOException e) {
                }
            }
        });
    }

}
