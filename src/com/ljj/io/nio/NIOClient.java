
package com.ljj.io.nio;

import java.util.concurrent.atomic.AtomicBoolean;

import com.ljj.io.IOLifecycle;

public class NIOClient implements IOLifecycle {

    /*
     * 
     */
    private NIOClientHandle clientHandle;

    /*
     * 
     */
    private AtomicBoolean isRunning = new AtomicBoolean(false);

    /*
     * 
     */
    private String host;
    /*
     * 
     */
    private int port;

    /**
     * 
     * @param host
     * @param port
     */
    public NIOClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * 
     * @param ip
     * @param port
     */
    public void start() {
        if (isRunning.compareAndSet(false, true)) {
            clientHandle = new NIOClientHandle(host, port);
            new Thread(clientHandle, "nio Client").start();
        }
    }
    
    public void stop() {
        if (isRunning.compareAndSet(true, false)) {
            if (clientHandle != null) {
                clientHandle.stop();
                clientHandle = null;
            }
        }
    }

    /**
     * 
     * @param msg
     * @return
     * @throws Exception
     */
    public boolean sendMsg(String msg) throws Exception {
        if (msg.equals("q")) {
            return false;
        }
        if (isRunning.get()) {
            clientHandle.sendMsg(msg);
            return true;
        }
        return false;
    }
}