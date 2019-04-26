
package com.ljj.io.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import com.ljj.io.IContext;
import com.ljj.io.IOLifecycle;
import com.ljj.io.utils.Calculator;

/**
 * NIO
 * 
 * @author liangjinjing
 * @version 1.0
 */
public class NIOServerHandle implements IOLifecycle, Runnable {
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
    private NIOReadWriteHandle rwHandle;
    
    /*
     * 
     */
    private IContext context;

    /**
     * 
     * @param port
     */
    public NIOServerHandle(IContext context, int port) {
        try {
            this.context = context;
            selector = Selector.open();
            serverChannel = ServerSocketChannel.open();
            serverChannel.configureBlocking(false);
            serverChannel.socket().bind(new InetSocketAddress(port), 1024);
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
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
                byte[] b = rwHandle.read(sc);
                if (b != null && b.length > 0) {
                    String result = processData(b);
                    rwHandle.write(sc, result);
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
    private String processData(byte[] b) throws IOException {
        String msg = new String(b, "utf-8");
        context.getPrint().onPrintServer(msg);
        return Calculator.Instance.cal(msg).toString();
    }
}
