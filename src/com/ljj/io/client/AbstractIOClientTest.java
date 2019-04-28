/**
 * 文件名称:          		AbstractTest.java
 * 版权所有@ 2019-2020 	无锡爱超信息技术有限公司，保留所有权利
 * 编译器:           		JDK1.8
 */

package com.ljj.io.client;

import java.util.concurrent.atomic.AtomicBoolean;

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
public abstract class AbstractIOClientTest implements IClientTest {

    /*
     * 
     */
    public static final int DEFAULT_PORT = 12345;
    /*
     * 
     */
    public static final String DEFAULT_HOST = "127.0.0.1";
    
    
    /*
     * 
     */
    protected IClient client;
    /*
     * 
     */
    private ClientScanner ioScanner;
    
    /**
     * 
     */
    private AtomicBoolean isRunning = new AtomicBoolean(false);

    /**
     * 启动客户端
     */
    public abstract void startClient();

    /**
     * 
     */
    public AbstractIOClientTest() {
        // 添加程序退出钩子
        Runtime.getRuntime().addShutdownHook(new Thread(() -> stop()));
    }

    /**
     * 
     */
    public void start() {
        if (isRunning.compareAndSet(false, true)) {
            // 启动客户端
            startClient();
            //
            startScanner();
        }
    }
    
    /**
     * 
     */
    public void stop() {
        if (isRunning.compareAndSet(true, false)) {
            if (ioScanner != null) {
                ioScanner.stop();
            }
            client = null;
            ioScanner = null;
        }
    }
    
    /**
     * 
     */
    public IAcceptHandler getAcceptHandler() {
       return ioScanner;
    }

    /**
     * 
     */
    private void startScanner() {
        ioScanner = new ClientScanner((String msg) -> {
            if (client != null) {
                client.sendMsg(msg);
            }
        });
        ioScanner.start();
    }


}
