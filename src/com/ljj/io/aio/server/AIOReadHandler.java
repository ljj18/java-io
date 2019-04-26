
package com.ljj.io.aio.server;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

import com.ljj.io.IContext;
import com.ljj.io.utils.Calculator;

public class AIOReadHandler implements CompletionHandler<Integer, ByteBuffer> {
    /*
     * 
     */
    private AsynchronousSocketChannel channel;
    /*
     * 
     */
    private IContext context;

    /**
     * 
     * @param context
     * @param channel
     */
    public AIOReadHandler(IContext context, AsynchronousSocketChannel channel) {
        this.channel = channel;
        this.context = context;
    }

    @Override
    public void completed(Integer result, ByteBuffer attachment) {
        attachment.flip();
        byte[] message = new byte[attachment.remaining()];
        attachment.get(message);
        try {
            String msg = new String(message, "UTF-8");
            context.getPrint().onPrintServer(msg);
            //
            doWrite(Calculator.Instance.cal(msg).toString());

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void doWrite(String result) {
        byte[] bytes = result.getBytes();
        ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
        writeBuffer.put(bytes);
        writeBuffer.flip();
        channel.write(writeBuffer, writeBuffer, new CompletionHandler<Integer, ByteBuffer>(){
            @Override
            public void completed(Integer result, ByteBuffer buffer) {
            }

            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {

            }
        });
    }

    @Override
    public void failed(Throwable exc, ByteBuffer attachment) {
        try {
            this.channel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}