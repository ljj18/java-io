
package com.ljj.io.server.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import com.ljj.io.server.IOServerLifecycle;
import com.ljj.io.server.IServerContext;

/**
 * NIO
 * 
 * @author liangjinjing
 * @version 1.0
 */
public class NIOServerHandler implements IOServerLifecycle, Runnable {

    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;
    /*
     * 选择器
     */
    private Selector selector;
    /*
     * 通道
     */
    private ServerSocketChannel serverChannel;
    /*
     * 
     */
    private AtomicBoolean isRunning = new AtomicBoolean(false);
    /*
     * 
     */
    private AtomicBoolean isStopFinish= new AtomicBoolean(false);
    
    /*
     * 
     */
    private IServerContext context;
    
    /*
     * 读Buffer
     */
    private ByteBuffer readBuffer;
    /*
     * 写缓冲
     */
    private ByteBuffer writeBuffer;

    /**
     * 
     * @param port
     */
    public NIOServerHandler(IServerContext context, int port) {
        try {
            this.context = context;
            selector = Selector.open();
            serverChannel = ServerSocketChannel.open();
            serverChannel.configureBlocking(false);
            serverChannel.socket().bind(new InetSocketAddress(port), 1024);
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
            //
            readBuffer = ByteBuffer.allocate(DEFAULT_BUFFER_SIZE);
            writeBuffer = ByteBuffer.allocate(DEFAULT_BUFFER_SIZE);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public void run() {
        start();
    }

    /**
     * 
     */
    public void start() {
        if (isRunning.compareAndSet(false, true)) {
            while (isRunning.get()) {
                try {
                    // 无论是否有读写事件发生，selector每隔1s被唤醒一次
                    selector.select(1000);
                    Set<SelectionKey> keys = selector.selectedKeys();
                    Iterator<SelectionKey> it = keys.iterator();
                    SelectionKey key = null;
                    while (it.hasNext()) {
                        key = it.next();
                        it.remove();
                        try {
                            handleInput(key);
                        } catch (IOException io) {
                            key.cancel();
                            if (key.channel() != null) {
                                key.channel().close();
                            }
                        }
                    }
                } catch (IOException io) {
                    io.printStackTrace();
                }
            }
            isStopFinish.set(true);
        }

    }

    /**
     *
     */
    public void stop() {
        if (isRunning.compareAndSet(true, false)) {
            if (isStopFinish.compareAndSet(true, false)) {
                try {
                    if (selector != null) {
                        selector.close();
                    }
                    if (serverChannel != null) {
                        serverChannel.close();
                    }
                } catch (IOException io) {
                    io.printStackTrace();
                }
            }
        }
    }

    /**
     * 
     * @param key
     * @throws IOException
     */
    private void handleInput(SelectionKey key) throws IOException {
        if (key.isValid()) {
            if (key.isAcceptable()) {
                ServerSocketChannel ssc = (ServerSocketChannel)key.channel();
                SocketChannel sc = ssc.accept();
                sc.configureBlocking(false);
                sc.register(selector, SelectionKey.OP_READ);
            }
            if (key.isReadable()) {
                SocketChannel sc = (SocketChannel)key.channel();
                byte[] b = read(sc);
                if (b != null && b.length > 0) {
                    String result = context.getAcceptHandler().onAccept(b);
                    write(sc, result);
                } else {
                    key.cancel();
                    sc.close();
                }
            }
        }
    }
    

    /**
     * 异步读数据
     * 
     * @param key
     * @return
     */
    private byte[] read(SocketChannel socketChannel) throws IOException {
        // 清除缓冲区
        readBuffer.clear();
        // 读取请求码流，返回读取到的字节数
        int readBytes = socketChannel.read(readBuffer);
        // 读取到字节，对字节进行编解码
        if (readBytes > 0) {
            // 将缓冲区当前的limit设置为position=0，用于后续对缓冲区的读取操作
            readBuffer.flip();
            // 根据缓冲区可读字节数创建字节数组
            byte[] bytes = new byte[readBuffer.remaining()];
            // 将缓冲区可读字节数组复制到新建的数组中
            readBuffer.get(bytes);
            return bytes;
        }
        return null;
    }

    /**
     * 异步写数据
     * 
     * @param socketChannel
     * @param request
     * @throws IOException
     */
    private void write(SocketChannel socketChannel, String msg) throws IOException {
        // 清除缓冲区
        writeBuffer.clear();
        // 将字节数组复制到缓冲区
        writeBuffer.put(msg.getBytes());
        // flip操作
        writeBuffer.flip();
        // 发送缓冲区的字节数组
        socketChannel.write(writeBuffer);
    }
}
