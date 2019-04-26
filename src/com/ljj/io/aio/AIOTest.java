
package com.ljj.io.aio;

import com.ljj.io.AbstractIOTest;
import com.ljj.io.aio.client.AIOClient;
import com.ljj.io.aio.server.AIOServer;
import com.ljj.io.utils.Const;

/**
 * 测试方法
 * 
 * @author yangtao__anxpp.com
 * @version 1.0
 */
public class AIOTest extends AbstractIOTest {
    

    @Override
    public void startServer() {
        server = new AIOServer(this, Const.DEFAULT_PORT);
        server.start();   
    }

    @Override
    public void startClient() {
        client = new AIOClient(this, Const.DEFAULT_HOST, Const.DEFAULT_PORT);
        client.start();
    }
    
    public static void main(String[] args) throws Exception {
        AIOTest aioTest = new AIOTest();
        aioTest.start();
    }

}