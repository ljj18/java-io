
package com.ljj.io.client.bio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

import com.ljj.io.client.IClient;
import com.ljj.io.client.IClientContext;

/**
 * 阻塞式I/O创建的客户端
 * 
 * @author liangjinjing
 * @version 1.0
 */
public class BIOClient implements IClient {

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
    private IClientContext context;
    /*
     * 
     */
    private Socket socket;

    /*
     * 
     */
    private AtomicBoolean isRunning = new AtomicBoolean(false);

    /**
     * 
     * @param host
     * @param port
     */
    public BIOClient(IClientContext context, String host, int port) {
        this.context = context;
        this.host = host;
        this.port = port;
    }

    @Override
    public void start() {
        if (isRunning.compareAndSet(false, true)) {
            try {
                socket = new Socket(host, port);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 
     * @param msg
     */
    /*public void sendMsg(String msg) {
        Socket socket = null;
        BufferedReader in = null;
        PrintWriter out = null;
        try {
            socket = new Socket(host, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            out.println(msg);
            context.getAcceptHandler().onAccept(in.readLine().getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            // 一下必要的清理工作
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                in = null;
            }
            if (out != null) {
                out.close();
                out = null;
            }
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                socket = null;
            }
        }
    }*/
    
    /**
     * 
     *
     */
    public void sendMsg(String msg) {
        BufferedReader in = null;
        PrintWriter out = null;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            out.println(msg);
            String result = in.readLine();
            context.getAcceptHandler().onAccept(result == null ? null : in.readLine().getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            // 必要的清理工作
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                in = null;
            }
            if (out != null) {
                out.close();
                out = null;
            }
        }
    }
    

    @Override
    public void stop() {
        if (isRunning.compareAndSet(true, false)) {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                socket = null;
            }
        }
    }
}