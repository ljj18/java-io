
package com.ljj.io.client.netty;

import java.util.concurrent.atomic.AtomicBoolean;

import com.ljj.io.client.IClient;
import com.ljj.io.client.IClientContext;

public class NettyClient implements IClient {

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
    private IClientContext contet;
    /*
     * 
     */
    private NettyClientHandler clientHandler;
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
    public NettyClient(IClientContext context, String host, int port) {
        this.contet = context;
        this.host = host;
        this.port = port;
    }

    @Override
    public void start() {
        if (isRunning.compareAndSet(false, true)) {
            clientHandler = new NettyClientHandler(contet, host, port);
            new Thread(clientHandler, "Netty Client-1").start();
        }
    }

    @Override
    public void stop() {
        if (isRunning.compareAndSet(true, false)) {
            clientHandler.stop();
        }

    }

    @Override
    public void sendMsg(String msg) {
        clientHandler.sendMsg(msg);
    }
}