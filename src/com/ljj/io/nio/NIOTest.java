
package com.ljj.io.nio;

import com.ljj.io.AbstractIOTest;
import com.ljj.io.utils.Const;

/**
 * 测试类
 * 
 * @author liangjinjing
 * @version 1.0
 */
public class NIOTest extends AbstractIOTest {
    
    @Override
    public void startServer() {
        server = new NIOServer(this, Const.DEFAULT_PORT);
        server.start(); 
    }

    @Override
    public void startClient() {
        client = new NIOClient(this, Const.DEFAULT_HOST, Const.DEFAULT_PORT);
        client.start();
    }
    
    /**
     * 测试主方法
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        NIOTest nioTest = new NIOTest();
        nioTest.start();
    }
}