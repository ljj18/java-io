
package com.ljj.io.server.bio;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import com.ljj.io.server.IServer;
import com.ljj.io.server.IServerContext;

/**
 * BIO I/O
 * 
 * @author liangjinjing
 * @version 1.0
 */
public final class BIOServer implements IServer {
    
    /*
     * 
     */
    private static ExecutorService executorService = Executors.newFixedThreadPool(60);
    /*
     * 
     */
    private int port;
    /*
     * 
     */
    private ServerSocket server;
    /*
     * 
     */
    private IServerContext content;
    /*
     * 
     */
    private AtomicBoolean isRunning = new AtomicBoolean(false);

    /**
     * 
     * @param port
     */
    public BIOServer(IServerContext context, int port) {
        this.content = context;
        this.port = port;
    }

    /**
     * 
     *
     */
    public void start() {
        if (isRunning.compareAndSet(false, true)) {
            try {
                server = new ServerSocket(port);
                Socket socket;
                while (isRunning.get()) {
                    socket = server.accept();
                    executorService.execute(new BIOServerHandler(socket, content));
                }
            } catch (Exception e) {
            }
        }
    }

    /**
     *
     */
    public void stop() {
        isRunning.compareAndSet(true, false);
        if (server != null) {
            try {
                server.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            server = null;
        }
        content = null;
    }
}