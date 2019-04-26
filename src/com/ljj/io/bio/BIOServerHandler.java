
package com.ljj.io.bio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import com.ljj.io.IContext;
import com.ljj.io.utils.Calculator;

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
    private IContext context;

    /**
     * 
     * @param socket
     */
    public BIOServerHandler(Socket socket, IContext context) {
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
                context.getPrint().onPrintServer(msg);
                out.println(Calculator.Instance.cal(msg).toString());
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