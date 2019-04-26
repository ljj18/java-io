
package com.ljj.io.nio;

import java.util.Scanner;

import com.ljj.io.utils.Const;

/**
 * 测试类
 * 
 * @author liangjinjing
 * @version 1.0
 */
public class NIOTest {

    /**
     * 测试主方法
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        // 运行服务器
        NIOServer nioServer = new NIOServer(Const.DEFAULT_PORT);
        nioServer.start();
        // 避免客户端先于服务器启动前执行代码
        Thread.sleep(100);
        // 运行客户端
        NIOClient nioClient = new NIOClient(Const.DEFAULT_HOST, Const.DEFAULT_PORT);
        nioClient.start();
        //
        Scanner scaner = new Scanner(System.in);
        System.out.print("请输入表达式：");
        boolean loop = true;
        while (loop) {
            String msg = scaner.nextLine();
            System.out.println();
            loop = nioClient.sendMsg(msg);
        }
        //
        scaner.close();
        // 停客户端 
        nioClient.stop();
        // 停服务端
        nioServer.stop();
        
        System.out.println("退出成功!!!");
    }
}