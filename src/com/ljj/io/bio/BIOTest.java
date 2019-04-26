
package com.ljj.io.bio;

import com.ljj.io.AbstractIOTest;
import com.ljj.io.utils.Const;

/**
 * 测试方法
 * 
 * @author liangjinjing
 * @version 1.0
 */
public class BIOTest extends AbstractIOTest {

    /**
     * 启动服务端
     */
    public void startServer() {
        server = new BIOServer(this, Const.DEFAULT_PORT);
        new Thread(() -> server.start(), "BIO Server-1").start();
    }

    /**
     * 
     */
    public void startClient() {
        client = new BIOClient(this, Const.DEFAULT_HOST, Const.DEFAULT_PORT);
    }

    /**
     * 
     * @param args
     * @throws InterruptedException
     */
    public static void main(String[] args) {
        BIOTest bioTest = new BIOTest();
        bioTest.start();
    }
}