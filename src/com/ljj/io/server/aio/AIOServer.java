
package com.ljj.io.server.aio;

import java.util.concurrent.atomic.AtomicBoolean;

import com.ljj.io.server.IServer;
import com.ljj.io.server.IServerContext;

/**
 * AIO服务端
 * 
 * @author yangtao__anxpp.com
 * @version 1.0
 */
public class AIOServer implements IServer {

    /*
     * 
     */
    private int port;

    /*
     * 
     */
    private AtomicBoolean isRunning = new AtomicBoolean(false);
    /*
     * 
     */
    private AIOAsyncServerHandler serverHandle;
    /*
     * 
     */
    private IServerContext context;

    /**
     * 
     * @param context
     * @param port
     */
    public AIOServer(IServerContext context, int port) {
        this.context = context;
        this.port = port;
    }

    @Override
    public void start() {
        if (isRunning.compareAndSet(false, true)) {
            serverHandle = new AIOAsyncServerHandler(context, port);
            new Thread(serverHandle, "AIO Server-1").start();
        }
    }

    @Override
    public void stop() {
        if (isRunning.compareAndSet(true, false)) {
            if (serverHandle != null) {
                serverHandle.stop();
            }
        }
    }
}