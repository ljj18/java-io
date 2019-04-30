
package com.ljj.io.server.netty;

import com.ljj.io.server.AbstractIOServerTest;
import com.ljj.io.server.IServerTest;

/**
 * 测试类
 * 
 * @author liangjinjing
 * @version 1.0
 */
public class NettyServerTest extends AbstractIOServerTest {
    
    
    @Override
    public void startServer() {
        server = new NettyServer(this, DEFAULT_PORT);
        server.start(); 
    }
    
    /**
     * 测试主方法
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        IServerTest nioTest = new NettyServerTest();
        nioTest.start();
    }
}