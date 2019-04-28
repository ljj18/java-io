
package com.ljj.io.server.bio;

import com.ljj.io.server.AbstractIOServerTest;
import com.ljj.io.server.IServerTest;

/**
 * 测试方法
 * 
 * @author liangjinjing
 * @version 1.0
 */
public class BIOServerTest extends AbstractIOServerTest {

    /**
     * 启动服务端
     */
    public void startServer() {
        server = new BIOServer(this, DEFAULT_PORT);
        new Thread(() -> server.start(), "BIO Server-1").start();
    }

    /**
     * 
     * @param args
     * @throws InterruptedException
     */
    public static void main(String[] args) {
        IServerTest bioTest = new BIOServerTest();
        bioTest.start();
    }
}