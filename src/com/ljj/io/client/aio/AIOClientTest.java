
package com.ljj.io.client.aio;

import com.ljj.io.client.AbstractIOClientTest;
import com.ljj.io.client.IClientTest;

/**
 * 测试方法
 * 
 * @author yangtao__anxpp.com
 * @version 1.0
 */
public class AIOClientTest extends AbstractIOClientTest {
    

    @Override
    public void startClient() {
        client = new AIOClient(this, DEFAULT_HOST, DEFAULT_PORT);
        client.start();
    }
    
    public static void main(String[] args) throws Exception {
        IClientTest test = new AIOClientTest();
        test.start();
    }

}