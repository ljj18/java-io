
package com.ljj.io.nio;

import java.util.concurrent.atomic.AtomicBoolean;

public class NIOServer {

    private AtomicBoolean isRunning = new AtomicBoolean();
    
    /*
     * 
     */
    private int port;
    
    /*
     * 
     */
    private NIOServerHandle nioServerHandle;
    
    /**
     * 
     * @param port
     */
    public NIOServer(int port) {
        this.port = port;
    }
    
    public void stop() {
        if (nioServerHandle != null) {
            nioServerHandle.stop();
            nioServerHandle = null;
        }
        isRunning.set(false);
    }
    
    /**
     * 启动NIO 服务端
     * @param port
     */
    public void start() {        
        if (isRunning.compareAndSet(false, true)) {
            nioServerHandle = new NIOServerHandle(port);
            new Thread(nioServerHandle, "NIO Server").start();
        }
    }
}