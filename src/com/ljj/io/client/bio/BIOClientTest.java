
package com.ljj.io.client.bio;

import com.ljj.io.client.AbstractIOClientTest;
import com.ljj.io.client.IClientTest;

/**
 * 测试方法
 * 
 * @author liangjinjing
 * @version 1.0
 */
public class BIOClientTest extends AbstractIOClientTest {

    /**
     * 
     */
    public void startClient() {
        client = new BIOClient(this, DEFAULT_HOST, DEFAULT_PORT);
        client.start();
    }

    /**
     * 
     * @param args
     * @throws InterruptedException
     */
    public static void main(String[] args) {
        IClientTest test = new BIOClientTest();
        test.start();
    }
}