
package com.ljj.io.client.nio;

import com.ljj.io.client.AbstractIOClientTest;
import com.ljj.io.client.IClientTest;

/**
 * 测试方法
 * 
 * @author liangjinjing
 * @version 1.0
 */
public class NIOClientTest extends AbstractIOClientTest {

    /**
     * 
     */
    public void startClient() {
        client = new NIOClient(this, DEFAULT_HOST, DEFAULT_PORT);
        client.start();
    }

    /**
     * 
     * @param args
     * @throws InterruptedException
     */
    public static void main(String[] args) {
        IClientTest test = new NIOClientTest();
        test.start();
    }
}