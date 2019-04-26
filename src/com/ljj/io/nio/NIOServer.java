
package com.ljj.io.nio;

import java.util.concurrent.atomic.AtomicBoolean;

import com.ljj.io.IContext;
import com.ljj.io.IServer;

public class NIOServer implements IServer {

    private AtomicBoolean isRunning = new AtomicBoolean();
    
    /*
     * 
     */
    private int port;
    
    /*
     * 
     */
    private NIOServerHandle nioServerHandle;
    /*
     * 
     */
    private IContext context;
    
    /**
     * 
     * @param port
     */
    public NIOServer(IContext context, int port) {
        this.context = context;
        this.port = port;
    }

    /**
     * 启动NIO 服务端
     * @param port
     */
    public void start() {        
        if (isRunning.compareAndSet(false, true)) {
            nioServerHandle = new NIOServerHandle(context, port);
            new Thread(nioServerHandle, "NIO Server").start();
        }
    }
    
    /**
     *
     */
    public void stop() {
        if (nioServerHandle != null) {
            nioServerHandle.stop();
            nioServerHandle = null;
        }
        context = null;
        isRunning.set(false);
    }
    
}