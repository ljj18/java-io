
package com.ljj.io.server.nio;

import com.ljj.io.server.AbstractIOServerTest;
import com.ljj.io.server.IServerTest;

/**
 * 测试类
 * 
 * @author liangjinjing
 * @version 1.0
 */
public class NIOTest extends AbstractIOServerTest {
    
    
    @Override
    public void startServer() {
        server = new NIOServer(this, DEFAULT_PORT);
        server.start(); 
    }
    
    /**
     * 测试主方法
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        IServerTest nioTest = new NIOTest();
        nioTest.start();
    }
}