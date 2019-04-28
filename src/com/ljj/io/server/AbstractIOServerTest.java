/**
 * 文件名称:          		AbstractTest.java
 * 版权所有@ 2019-2020 	无锡爱超信息技术有限公司，保留所有权利
 * 编译器:           		JDK1.8
 */

package com.ljj.io.server;

import java.util.concurrent.atomic.AtomicBoolean;

import com.ljj.io.server.impl.ServerAcceptHandlerImpl;

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
public abstract class AbstractIOServerTest implements IServerTest, IServerContext {

    /**
     * 
     */
    public final static int DEFAULT_PORT = 12345;
    
    /*
     * 
     */
    protected IServer server;
    
    /*
     * 
     */
    private IServerAcceptHandler acceptHandler;

    /*
     * 
     */
    private AtomicBoolean isRunning = new AtomicBoolean(false);

    /**
     * 启动服务端
     */
    public abstract void startServer();

    /**
     * 
     */
    public AbstractIOServerTest() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> stop()));
        acceptHandler = new ServerAcceptHandlerImpl();
    }

    /**
     * 
     */
    public void start() {
        if (isRunning.compareAndSet(false, true)) {
            // 启动服务端
            startServer();
            
            //
            System.out.println("启动成功......");
        }
    }

    /**
     * 
     */
    public void stop() {
        if (isRunning.compareAndSet(true, false)) {
            if (server != null) {
                server.stop();
            }
            server = null;
        }
    }
    
    /**
     * 
     */
    public IServerAcceptHandler getAcceptHandler() {
        return this.acceptHandler;
    }

}
