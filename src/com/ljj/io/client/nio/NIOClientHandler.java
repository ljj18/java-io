
package com.ljj.io.client.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import com.ljj.io.client.IClientContext;
import com.ljj.io.server.IOServerLifecycle;

/**
 * NIO客户端
 * 
 * @author liangjinjing
 * @version 1.0
 */
public class NIOClientHandler implements IOServerLifecycle, Runnable {
    
    
    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;
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
    private Selector selector;
    /*
     * 
     */
    private SocketChannel socketChannel;
    /*
     * 
     */
    private AtomicBoolean isRuuning = new AtomicBoolean(false);
    /*
     * 
     */
    private AtomicBoolean isStopFinish = new AtomicBoolean(false);

    /*
     * 
     */
    private IClientContext context;
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
     * @param ip
     * @param port
     */
    public NIOClientHandler(IClientContext context, String ip, int port) {
        
        this.context = context;
        this.host = ip;
        this.port = port;
        try {
            // 创建选择器
            selector = Selector.open();
            // 打开监听通道
            socketChannel = SocketChannel.open();
            // 如果为 true，则此通道将被置于阻塞模式；如果为 false，则此通道将被置于非阻塞模式
            socketChannel.configureBlocking(false);// 开启非阻塞模式
            // 注册链接Key
            socketChannel.register(selector, SelectionKey.OP_CONNECT);
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
        try {
            if (isRuuning.compareAndSet(false, true)) {
                socketChannel.connect(new InetSocketAddress(host, port));
            }
        } catch (IOException io) {
            io.printStackTrace();
            System.exit(1);
        }
        // 循环遍历selector
        while (isRuuning.get()) {
            try {
                // 无论是否有读写事件发生，selector每隔1s被唤醒一次
                selector.select(1000);
                // 获取操作Key
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> it = keys.iterator();
                SelectionKey key = null;
                while (it.hasNext()) {
                    key = it.next();
                    it.remove();
                    // 读数据
                    try {
                        handleInput(key);
                    } catch (IOException io) {
                        key.cancel();
                        if (key.channel() != null) {
                            key.channel().close();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        isStopFinish.set(true);

    }

    /**
     * 
     */
    public void stop() {
        if (isRuuning.compareAndSet(true, false)) {
            if (isStopFinish.compareAndSet(true, true)) {
                try {
                    if (selector != null) {
                        selector.close();
                    }
                    if (socketChannel != null) {
                        socketChannel.close();
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
            SocketChannel sc = (SocketChannel)key.channel();
            if (key.isConnectable()) {
                if (!sc.finishConnect()) {
                    System.out.println("连接不成功.......");
                }
            }
            // 读消息
            if (key.isReadable()) {
                byte[] b = read(sc);
                if (b != null && b.length > 0) {
                    context.getAcceptHandler().onAccept(b);
                } else {
                    key.cancel();
                    sc.close();
                }
            }
        }
    }

    /**
     * 
     * @param msg
     * @throws Exception
     */
    public void sendMsg(String msg) {
        if (isRuuning.get()) {
            try {
                socketChannel.register(selector, SelectionKey.OP_READ);
                write(socketChannel, msg);
            } catch (IOException ioe) {
                ioe.printStackTrace();
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