/**
 * 文件名称:          		AIOSocketChannelReadHandle.java
 * 版权所有@ 2019-2020 	无锡爱超信息技术有限公司，保留所有权利
 * 编译器:           		JDK1.8
 */

package com.ljj.io.server.aio;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
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
public class AIOServerSocketChannelReadHandler implements CompletionHandler<Integer, StringBuffer> {

    /*
     * 
     */
    private AsynchronousSocketChannel socketChannel;
    /*
     * 
     */
    private IAIOServerHandler serverHandler;
    /*
     * 
     */
    private ByteBuffer readBuffer;

    /**
     * 
     * @param socketChannel
     */
    public AIOServerSocketChannelReadHandler(AsynchronousSocketChannel socketChannel, IAIOServerHandler serverHandler, ByteBuffer readBuffer) {
        this.socketChannel = socketChannel;
        this.readBuffer = readBuffer;
        this.serverHandler = serverHandler;
    }

    @Override
    public void completed(Integer result, StringBuffer stringBuffer) {
        // result = -1，说明客户端主动终止了TCP套接字，这时服务端终止就可以了
        if (result == -1) {
            try {
                socketChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        /*
         * 实际上，由于我们从Integer result知道了本次channel从操作系统获取数据总长度 所以实际上，我们不需要切换成“读模式”的，但是为了保证编码的规范性，还是建议进行切换。
         * 
         * 另外，无论是JAVA AIO框架还是JAVA NIO框架，都会出现“buffer的总容量”小于“当前从操作系统获取到的总数据量”， 但区别是，JAVA AIO框架中，我们不需要专门考虑处理这样的情况，因为JAVA
         * AIO框架已经帮我们做了处理（做成了多次通知）
         */
        readBuffer.flip();
        byte[] contexts = new byte[1024];
        readBuffer.get(contexts, 0, result);
        readBuffer.clear();
        try {
            String nowContent = new String(contexts, 0, result, "UTF-8");
            stringBuffer.append(nowContent);
        } catch (UnsupportedEncodingException e) {
        }

        // 如果条件成立，说明还没有接收到“结束标记”
        if (stringBuffer.indexOf("over") == -1) {
            return;
        } else {
            // 清空已经读取的缓存，并从新切换为写状态(这里要注意clear()和capacity()两个方法的区别)
            readBuffer.clear();
            // ======================================================
            // 当然接受完成后，可以在这里正式处理业务了
            // ======================================================
            String str = serverHandler.getContext().getAcceptHandler().onAcceptByString(stringBuffer.toString());
            // 回发数据，并关闭channel
            ByteBuffer sendBuffer = null;
            try {
                sendBuffer = ByteBuffer.wrap(URLEncoder.encode(str, "UTF-8").getBytes());
                socketChannel.write(sendBuffer);
                socketChannel.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // 
        stringBuffer = new StringBuffer();
        // 还要继续监听（一次监听一次通知）
        socketChannel.read(readBuffer, stringBuffer, this);

    }

    @Override
    public void failed(Throwable exc, StringBuffer stringBuffer) {
        try {
            this.socketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
