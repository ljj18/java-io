/**
 * 文件名称:          		AbstractTest.java
 * 版权所有@ 2019-2020 	无锡爱超信息技术有限公司，保留所有权利
 * 编译器:           		JDK1.8
 */

package com.ljj.io;

import com.ljj.io.utils.IOScanner;

/**
 * TODO: 文件注释
 * 
 * Version 1.0.0
 * 
 * @author liangjinjing
 * 
 * Date 2019-04-26 15:42
 * 
 */
public abstract class AbstractIOTest implements IOLifecycle, IContext {

    /*
     * 
     */
    protected IClient client;
    /*
     * 
     */
    protected IServer server;
    /*
     * 
     */
    protected IOScanner ioScanner;
    
    /*
     * 
     */
    protected IPrint print;

    /*
     * 
     */
    public abstract void startServer();

    /*
     * 
     */
    public abstract void startClient();

    public AbstractIOTest() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> stop()));
        
        print = new IPrint(){
            @Override
            public void onPrintServer(String msg) {
                System.out.println("服务端收到：" + msg);
            }
            
            @Override
            public void onPrintClient(String msg) {
                System.out.println("计算结果：" + msg);
                System.out.println();
                System.out.print("请输入表达式：");
            }
        };
    }

    /**
     * 
     */
    public void start() {
        // 启动服务端
        startServer();
        // 避免客户端先于服务器启动前执行代码
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 启动客户端
        startClient();
        //
        startScanner();
    }
    
    /**
     * 
     */
    public void stop() {
        if (server != null) {
            server.stop();
        }
        if (ioScanner != null) {
            ioScanner.stop();
        }
        server = null;
        client = null;
        ioScanner = null;
    }
    
    /**
     * 
     */
    public IPrint getPrint() {
       return print; 
    }

    /**
     * 
     */
    private void startScanner() {
        ioScanner = new IOScanner((String msg) -> {
            if (client != null) {
                client.sendMsg(msg);
            }
        });
        ioScanner.start();
    }


}
