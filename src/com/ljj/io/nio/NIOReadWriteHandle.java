/**
 * 文件名称:          		ReadWriteHandle.java
 * 版权所有@ 2019-2020 	无锡爱超信息技术有限公司，保留所有权利
 * 编译器:           		JDK1.8
 */

package com.ljj.io.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import com.ljj.io.utils.Const;

/**
 * 
 * 
 * Version 1.0.0
 * 
 * @author liangjinjing
 * 
 * Date 2019-04-26 09:56
 * 
 */
public class NIOReadWriteHandle {

    /*
     * 读Buffer
     */
    private ByteBuffer readBuffer;
    /*
     * 写缓冲
     */
    private ByteBuffer writeBuffer;

    /**
     * 
     */
    public NIOReadWriteHandle() {
        readBuffer = ByteBuffer.allocate(Const.DEFAULT_BUFFER_SIZE);
        writeBuffer = ByteBuffer.allocate(Const.DEFAULT_BUFFER_SIZE);
    }

    /**
     * 异步读数据
     * 
     * @param key
     * @return
     */
    public byte[] read(SocketChannel socketChannel) throws IOException {
        // 清除缓冲区
        readBuffer.clear();
        // 读取请求码流，返回读取到的字节数
        int readBytes = socketChannel.read(readBuffer);
        // 读取到字节，对字节进行编解码
        if (readBytes > 0) {
            // 将缓冲区当前的limit设置为position=0，用于后续对缓冲区的读取操作
            readBuffer.flip();
            // 根据缓冲区可读字节数创建字节数组
            byte[] bytes = new byte[readBuffer.remaining()];
            // 将缓冲区可读字节数组复制到新建的数组中
            readBuffer.get(bytes);
            return bytes;
        }
        return null;
    }

    /**
     * 异步写数据
     * 
     * @param socketChannel
     * @param request
     * @throws IOException
     */
    public void write(SocketChannel socketChannel, String msg) throws IOException {
        // 清除缓冲区
        writeBuffer.clear();
        // 将字节数组复制到缓冲区
        writeBuffer.put(msg.getBytes());
        // flip操作
        writeBuffer.flip();
        // 发送缓冲区的字节数组
        socketChannel.write(writeBuffer);
    }

}
