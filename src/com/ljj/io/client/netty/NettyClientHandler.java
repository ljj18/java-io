
package com.ljj.io.client.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.atomic.AtomicBoolean;

import com.ljj.io.client.IClientContext;
import com.ljj.io.client.IOClientLifecycle;

public class NettyClientHandler extends ChannelInboundHandlerAdapter implements IOClientLifecycle, Runnable {
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
    private AtomicBoolean isRunning = new AtomicBoolean(false);

    /*
     * 
     */
    private ChannelHandlerContext ctx;

    /**
     * 
     * @param context
     * @param host
     * @param port
     */
    public NettyClientHandler(IClientContext context, String host, int port) {
        this.context = context;
        this.host = host;
        this.port = port;
    }

    /**
     * tcp链路简历成功后调用
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
    }

    /**
     * 
     * @param msg
     * @return
     */
    public void sendMsg(String msg) {
        if (isRunning.get()) {
            byte[] req = msg.getBytes();
            ByteBuf m = Unpooled.buffer(req.length);
            m.writeBytes(req);
            ctx.writeAndFlush(m);
        }
    }

    /**
     * 收到服务器消息后调用
     * 
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws UnsupportedEncodingException {
        ByteBuf buf = (ByteBuf)msg;
        byte[] req = new byte[buf.readableBytes()];
        buf.readBytes(req);
        context.getAcceptHandler().onAccept(req);
    }

    /**
     * 发生异常时调用
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void run() {
        start();
    }

    @Override
    public void start() {
        if (isRunning.compareAndSet(false, true)) {
            EventLoopGroup workerGroup = new NioEventLoopGroup();
            try {
                Bootstrap b = new Bootstrap();
                b.group(workerGroup);
                b.channel(NioSocketChannel.class);
                b.option(ChannelOption.SO_KEEPALIVE, true);
                NettyClientHandler clientHandler = this;
                b.handler(new ChannelInitializer<SocketChannel>(){
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(clientHandler);
                    }
                });
                ChannelFuture f = b.connect(host, port).sync();
                f.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            finally {
                workerGroup.shutdownGracefully();
            }
        }

    }

    @Override
    public void stop() {
        if(isRunning.compareAndSet(true, false)) {
            ctx = null;
        }

    }
}