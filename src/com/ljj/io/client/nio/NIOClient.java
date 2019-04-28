
package com.ljj.io.client.nio;

import java.util.concurrent.atomic.AtomicBoolean;

import com.ljj.io.client.IClient;
import com.ljj.io.client.IClientContext;

public class NIOClient implements IClient {

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
    private NIOClientHandle clientHandle;

    /*
     * 
     */
    private AtomicBoolean isRunning = new AtomicBoolean(false);

    /*
     * 
     */
    private IClientContext context;

    /**
     * 
     * @param host
     * @param port
     */
    public NIOClient(IClientContext context, String host, int port) {
        this.context = context;
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
            clientHandle = new NIOClientHandle(context, host, port);
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
    public void sendMsg(String msg) {
        clientHandle.sendMsg(msg);
    }
}