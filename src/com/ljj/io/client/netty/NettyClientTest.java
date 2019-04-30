/**
 * 文件名称:          		NettyClientTest.java
 * 版权所有@ 2019-2020 	无锡爱超信息技术有限公司，保留所有权利
 * 编译器:           		JDK1.8
 */

package com.ljj.io.client.netty;

import com.ljj.io.client.AbstractIOClientTest;
import com.ljj.io.client.IClientTest;

/**
 * 
 * 
 * Version 1.0.0
 * 
 * @author liangjinjing
 * 
 * Date 2019-04-30 10:21
 * 
 */
public class NettyClientTest extends AbstractIOClientTest {

    @Override
    public void startClient() {
        client = new NettyClient(this, DEFAULT_HOST, DEFAULT_PORT);
        client.start();
    }

    public static void main(String[] args) throws Exception {
        IClientTest test = new NettyClientTest();
        test.start();
    }

}
