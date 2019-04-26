
package com.ljj.io.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import com.ljj.io.IOLifecycle;

/**
 * NIO客户端
 * 
 * @author yangtao__anxpp.com
 * @version 1.0
 */
public class NIOClientHandle implements IOLifecycle, Runnable {
    /*
     * 
     */
    private String host;
    /*
     * 
     */
    private int port;
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
    private NIOReadWriteHandle rwHandle;

    /**
     * 
     * @param ip
     * @param port
     */
    public NIOClientHandle(String ip, int port) {
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
            rwHandle = new NIOReadWriteHandle();
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
                    System.out.println("连接没有完成.......");
                }
            }
            // 读消息
            if (key.isReadable()) {
                byte[] b = rwHandle.read(sc);
                if (b != null && b.length > 0) {
                    processData(b);
                } else {
                    key.cancel();
                    sc.close();
                }
            }
        }
    }

    /**
     * 
     * @param key
     * @throws IOException
     */
    private void processData(byte[] b) throws IOException {
        String str = "没有读到数据";
        if (b != null) {
            str = "计算结果：".concat(new String(b, "utf-8"));
        }
        System.out.println(str);
    }

    /**
     * 
     * @param msg
     * @throws Exception
     */
    public void sendMsg(String msg) throws Exception {
        if (isRuuning.get()) {
            socketChannel.register(selector, SelectionKey.OP_READ);
            rwHandle.write(socketChannel, msg);
        }
    }
}