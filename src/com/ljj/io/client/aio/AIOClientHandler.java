
package com.ljj.io.client.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import com.ljj.io.client.IClientContext;

public class AIOClientHandler implements IAIOClientHandler, Runnable {
    /*
     * 
     */
    private int port;
    /*
     * 
     */
    private String host;
    /*
     * 
     */
    private IClientContext context;
    /*
     * 
     */
    private AsynchronousSocketChannel clientSocketChannel;
    /*
     * 
     */
    private AtomicBoolean isRunning = new AtomicBoolean(false);

    /*
     * 对于使用的线程池技术 1、Executors是线程池生成工具，通过这个工具我们可以很轻松的生成“固定大小的线程池”、“调度池”、“可伸缩线程数量的池”。具体请看API Doc
     * 2、当然您也可以通过ThreadPoolExecutor直接生成池。 3、这个线程池是用来得到操作系统的“IO事件通知”的，不是用来进行“得到IO数据后的业务处理的”。要进行后者的操作，您可以再使用一个池（最好不要混用）
     * 4、您也可以不使用线程池（不推荐），如果决定不使用线程池，直接AsynchronousServerSocketChannel.open()就行了。
     * 
     */
    private ExecutorService threadPool = Executors.newFixedThreadPool(20);

    /**
     * 
     * @param context
     * @param host
     * @param port
     */
    public AIOClientHandler(IClientContext context, String host, int port) {
        this.context = context;
        this.host = host;
        this.port = port;
    }

    @Override
    public void run() {
        start();
    }

    @Override
    public void start() {
        if (isRunning.compareAndSet(false, true)) {
            try {
                /**
                 * 创建异步的客户端通道
                 */
                clientSocketChannel = AsynchronousSocketChannel
                    .open(AsynchronousChannelGroup.withThreadPool(threadPool));
                // 发起异步连接操作，回调参数就是这个类本身，如果连接成功会回调completed方法
                clientSocketChannel.connect(new InetSocketAddress(host, port), null,
                    new AIOClientSocketChannelHandler(this));
            } catch (IOException e) {
                isRunning.set(false);
                e.printStackTrace();
            }
        }
    }

    @Override
    public void stop() {
        //
        if (isRunning.compareAndSet(true, false)) {
            close();
        }
    }

    @Override
    public IClientContext getContext() {
        return context;
    }

    @Override
    public void failed(Throwable exc) {
        exc.printStackTrace();
        close();
    }

    /**
     * 获得AIO socketChannel
     * 
     * @return
     */
    @Override
    public AsynchronousSocketChannel getSocketChannel() {
        assert clientSocketChannel != null;
        return clientSocketChannel;
    }
    
    /**
     * 
     * @param msg
     */
    public void sendMsg(String msg) {
        byte[] req = msg.getBytes();
        ByteBuffer writeBuffer = ByteBuffer.allocate(req.length);
        writeBuffer.put(req);
        writeBuffer.flip();
        // 异步写
        clientSocketChannel.write(writeBuffer, writeBuffer, new AIOClientSocketChannelWriteHandler(this));
    }
    

    private void close() {
        if (clientSocketChannel != null) {
            try {
                clientSocketChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
