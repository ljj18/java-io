
package com.ljj.io.server.aio;

import com.ljj.io.server.AbstractIOServerTest;
import com.ljj.io.server.IServerTest;

/**
 * 测试方法
 * 
 * @author yangtao__anxpp.com
 * @version 1.0
 */
public class AIOServerTest extends AbstractIOServerTest {

    @Override
    public void startServer() {
        server = new AIOServer(this, DEFAULT_PORT);
        server.start(); 
    }
    
    /**
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        IServerTest aioTest = new AIOServerTest();
        aioTest.start();
    }

}