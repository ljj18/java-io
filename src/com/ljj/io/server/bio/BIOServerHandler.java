
package com.ljj.io.server.bio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import com.ljj.io.server.IServerContext;

/**
 *
 * @author liangjinjing
 * 
 */
public class BIOServerHandler implements Runnable {
    
    /*
     * 
     */
    private Socket socket;
    /*
     * 
     */
    private IServerContext context;

    /**
     * 
     * @param socket
     */
    public BIOServerHandler(Socket socket, IServerContext context) {
        this.socket = socket;
        this.context = context;
    }

    @Override
    public void run() {
        BufferedReader in = null;
        PrintWriter out = null;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            String msg;
            while (true) {
                if ((msg = in.readLine()) == null) {
                    break;
                }
                out.println(context.getAcceptHandler().onAccept(msg.getBytes("utf-8")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
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
}