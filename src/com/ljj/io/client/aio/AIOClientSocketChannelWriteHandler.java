/**
 * 文件名称:          		AIOSocketChannelReadHandle.java
 * 版权所有@ 2019-2020 	无锡爱超信息技术有限公司，保留所有权利
 * 编译器:           		JDK1.8
 */

package com.ljj.io.client.aio;

import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;

/**
 * 负责对每一个socketChannel的数据获取事件进行监听。
 * 
 * 重要的说明：一个socketchannel都会有一个独立工作的SocketChannelReadHandle对象（CompletionHandler接口的实现）， 其中又都将独享一个“文件状态标示”对象FileDescriptor、
 * 一个独立的由程序员定义的Buffer缓存（这里我们使用的是ByteBuffer）、 所以不用担心在服务器端会出现“窜对象”这种情况，因为JAVA AIO框架已经帮您组织好了。
 * 
 * 但是最重要的，用于生成channel的对象：AsynchronousChannelProvider是单例模式，无论在哪组socketchannel，
 * 对是一个对象引用（但这没关系，因为您不会直接操作这个AsynchronousChannelProvider对象）。
 * 
 * Version 1.0.0
 * 
 * @author liangjinjing
 * 
 * Date 2019-04-28 18:18
 * 
 */
public class AIOClientSocketChannelWriteHandler implements CompletionHandler<Integer, ByteBuffer> {
    /*
     * 
     */
    private IAIOClientHandler clientHandler;

    /**
     * 
     * @param socketChannel
     */
    public AIOClientSocketChannelWriteHandler(IAIOClientHandler clientHandler) {
        this.clientHandler = clientHandler;
    }

    @Override
    public void completed(Integer result, ByteBuffer byteBuffer) {
        // 完成全部数据的写入
        if (byteBuffer.hasRemaining()) {
            clientHandler.getSocketChannel().write(byteBuffer, byteBuffer, this);
        } else {
            // 读取数据
            ByteBuffer readBuffer = ByteBuffer.allocate(1024);
            clientHandler.getSocketChannel().read(readBuffer, new StringBuffer(),
                new AIOClientSocketChannelReadHandler(clientHandler, readBuffer));
        }

    }

    @Override
    public void failed(Throwable exc, ByteBuffer byteBuffer) {
        clientHandler.failed(exc);
    }
}
