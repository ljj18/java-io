
package com.ljj.io.bio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import com.ljj.io.IClient;
import com.ljj.io.IContext;

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
    private IContext context;

    /**
     * 
     * @param host
     * @param port
     */
    public BIOClient(IContext context, String host, int port) {
        this.context = context;
        this.host = host;
        this.port = port;
    }

    /**
     * 
     * @param msg
     */
    public void sendMsg(String msg) {
        Socket socket = null;
        BufferedReader in = null;
        PrintWriter out = null;
        try {
            socket = new Socket(host, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            out.println(msg);
            context.getPrint().onPrintClient(in.readLine());
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
    }

    @Override
    public void start() {
        
    }

    @Override
    public void stop() {
        
    }
}