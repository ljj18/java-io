
package com.ljj.io.server.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import com.ljj.io.server.IServerContext;

public class AIOServerHandler implements IAIOServerHandler, Runnable {
    /*
     * 
     */
    private CountDownLatch latch;
    /*
     * 
     */
    private AsynchronousServerSocketChannel serverSocketchannel;

    /*
     * 
     */
    private AtomicBoolean isRunning = new AtomicBoolean(false);
    /*
     * 
     */
    private IServerContext context;

    /*
     * 对于使用的线程池技术
     * 1、Executors是线程池生成工具，通过这个工具我们可以很轻松的生成“固定大小的线程池”、“调度池”、“可伸缩线程数量的池”。具体请看API Doc
     * 2、当然您也可以通过ThreadPoolExecutor直接生成池。 
     * 3、这个线程池是用来得到操作系统的“IO事件通知”的，不是用来进行“得到IO数据后的业务处理的”。要进行后者的操作，您可以再使用一个池（最好不要混用）
     * 4、您也可以不使用线程池（不推荐），如果决定不使用线程池，直接AsynchronousServerSocketChannel.open()就行了。
     * 
     */
    private ExecutorService threadPool = Executors.newFixedThreadPool(20);

    public AIOServerHandler(IServerContext context, int port) {
        this.context = context;
        try {
            // 创建服务端通道
            serverSocketchannel = AsynchronousServerSocketChannel.open(AsynchronousChannelGroup.withThreadPool(threadPool));
            // 绑定端口
            serverSocketchannel.bind(new InetSocketAddress(port));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        start();
    }

    @Override
    public void start() {

        if (isRunning.compareAndSet(false, true)) {
            /*
             * CountDownLatch初始化,它的作用：在完成一组正在执行的操作之前，允许当前的现场一直阻塞 此处， 让现场在此阻塞，防止服务端执行完成后退出,也可以使用while(true)+sleep
             */
            latch = new CountDownLatch(1);
            /*
             * 为AsynchronousServerSocketChannel注册监听，注意只是为AsynchronousServerSocketChannel通道注册监听
             * 并不包括为 随后客户端和服务器 socketchannel通道注册的监听
             */
            serverSocketchannel.accept(null, new AIOServerSocketChannelHandler(this));
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void stop() {
        this.context = null;
        if (isRunning.compareAndSet(true, false)) {
            close();
        }
    }
    
    /**
     * 
     * @return
     */
    public IServerContext getContext() {
        return this.context;
    }


    @Override
    public void failed(Throwable exc) {
        if (isRunning.get()) {
            close();    
        }
    }
    @Override
    public AsynchronousServerSocketChannel getServerSocketChannel() {
        assert this.serverSocketchannel != null;
        return this.serverSocketchannel;
    }
    
    /**
     * 关闭通道
     */
    private void close() {
        latch.countDown();
        if (serverSocketchannel != null) {
            try {
                serverSocketchannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}