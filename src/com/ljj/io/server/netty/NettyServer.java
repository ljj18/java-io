package com.ljj.io.server.netty;
import java.util.concurrent.atomic.AtomicBoolean;

import com.ljj.io.server.IServer;
import com.ljj.io.server.IServerContext;

public class NettyServer implements IServer {

    /*
     * 
     */
    private int port;
    /*
     * 
     */
    private IServerContext context;
    /*
     * 
     */
    private AtomicBoolean isRunning = new AtomicBoolean(false);
    /*
     * 
     */
    private NettyServerHandler serverHandler;
    
    
    /**
     * 
     * @param context
     * @param port
     */
    public NettyServer(IServerContext context, int port) {
        this.context = context;
        this.port = port;
    }


    @Override
    public void start() {
        if (isRunning.compareAndSet(false, true)) {
            serverHandler = new NettyServerHandler(context, port);
            new Thread(serverHandler, "Netty Server-1").start();
        }
    }

    @Override
    public void stop() {
        if (isRunning.compareAndSet(true, false)) {
            serverHandler.stop();
            serverHandler = null;
        }
        
    }
}