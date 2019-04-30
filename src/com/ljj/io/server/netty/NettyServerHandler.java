
package com.ljj.io.server.netty;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.atomic.AtomicBoolean;

import com.ljj.io.server.IOServerLifecycle;
import com.ljj.io.server.IServerContext;

import io.netty.bootstrap.ServerBootstrap;
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
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Handles a server-side channel.
 */
public class NettyServerHandler extends ChannelInboundHandlerAdapter implements IOServerLifecycle, Runnable {

    /*
     * 
     */
    private int port;
    /*
     * 
     */
    private IServerContext context;
    /*
     * 
     */
    private AtomicBoolean isRunning = new AtomicBoolean(false);

    /**
     * 
     * @param context
     */
    public NettyServerHandler(IServerContext context, int port) {
        this.context = context;
        this.port = port;
    }

    /**
     * 收到客户端消息
     * 
     * @throws UnsupportedEncodingException
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws UnsupportedEncodingException {
        ByteBuf in = (ByteBuf)msg;
        byte[] req = new byte[in.readableBytes()];
        in.readBytes(req);
        String calrResult = context.getAcceptHandler().onAccept(req);
        ctx.write(Unpooled.copiedBuffer(calrResult.getBytes()));
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    /**
     * 异常处理
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void start() {
        if (isRunning.compareAndSet(false, true)) {
            EventLoopGroup bossGroup = new NioEventLoopGroup();
            EventLoopGroup workerGroup = new NioEventLoopGroup();
            try {
                ServerBootstrap b = new ServerBootstrap();
                NettyServerHandler serverHandler = this;
                b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).option(ChannelOption.SO_BACKLOG, 1024)
                    .childOption(ChannelOption.SO_KEEPALIVE, true).childHandler(new ChannelInitializer<SocketChannel>(){
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(serverHandler);
                        }
                    });

                ChannelFuture f = b.bind(port).sync();
                f.channel().closeFuture().sync();
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
            finally {
                workerGroup.shutdownGracefully();
                bossGroup.shutdownGracefully();
            }
        }
    }

    @Override
    public void stop() {
    }

    @Override
    public void run() {
        start();
    }
}