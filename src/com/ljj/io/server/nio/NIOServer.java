
package com.ljj.io.server.nio;

import java.util.concurrent.atomic.AtomicBoolean;

import com.ljj.io.server.IServer;
import com.ljj.io.server.IServerContext;

public class NIOServer implements IServer {

    private AtomicBoolean isRunning = new AtomicBoolean();
    
    /*
     * 
     */
    private int port;
    
    /*
     * 
     */
    private NIOServerHandler nioServerHandle;
    /*
     * 
     */
    private IServerContext context;
    
    /**
     * 
     * @param port
     */
    public NIOServer(IServerContext context, int port) {
        this.context = context;
        this.port = port;
    }

    /**
     * 启动NIO 服务端
     * @param port
     */
    public void start() {        
        if (isRunning.compareAndSet(false, true)) {
            nioServerHandle = new NIOServerHandler(context, port);
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