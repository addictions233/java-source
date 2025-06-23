package com.one.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

/**
 * @description: Netty客户端编程
 * @author: wanjunjie
 * @date: 2024/06/05
 */
public class NettyClient {

    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup worker = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(worker)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    String message = "hello server!";
                                    ByteBuf byteBuf = ctx.alloc().buffer(4 * message.length());
                                    byteBuf.writeBytes(message.getBytes());
                                    ctx.writeAndFlush(byteBuf);
                                    System.out.println("client send:" + message);
                                }
                            });
                        }
                    });
            ChannelFuture channelFuture = bootstrap.connect("localhost",10086).sync();
            channelFuture.channel().closeFuture().sync();
        } finally {
            worker.shutdownGracefully();
        }

    }
}
