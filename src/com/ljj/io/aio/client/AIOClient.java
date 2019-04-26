
package com.ljj.io.aio.client;

import java.util.concurrent.atomic.AtomicBoolean;

import com.ljj.io.IClient;
import com.ljj.io.IContext;

public class AIOClient implements IClient {

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
    private IContext context;
    /*
     * 
     */
    private AIOAsyncClientHandler clientHandle;
    /*
     * 
     */
    private AtomicBoolean isRunning = new AtomicBoolean(false);

    /**
     * 
     * @param context
     * @param host
     * @param port
     */
    public AIOClient(IContext context, String host, int port) {
        this.context = context;
        this.host = host;
        this.port = port;
    }

    @Override
    public void start() {
        if (isRunning.compareAndSet(false, true)) {
            clientHandle = new AIOAsyncClientHandler(context, host, port);
            new Thread(clientHandle, "AIO Client-1").start();
        }
    }

    @Override
    public void stop() {
        if (isRunning.compareAndSet(true, false)) {
            if (clientHandle != null) {
                clientHandle.stop();
            }
        }
    }

    @Override
    public void sendMsg(String msg) {
        clientHandle.sendMsg(msg);
    }

}