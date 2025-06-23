package com.one.netty;



import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;

import java.net.InetSocketAddress;

/**
 * @description: netty服务器端的案例
 * @author: wanjunjie
 * @date: 2024/06/05
 */
public class NettyServer {
    public static void main(String[] args) throws InterruptedException {
        // BossGroup线程池: 负责客户端的连接 处理Accept事件
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        // WorkerGroup线程池: 负责客户端的读写 处理Read/Write事件
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            // 创建服务端的启动引导类
            ServerBootstrap bootstrap = new ServerBootstrap();
            // 设置主从线程组, 采用Reactor模型
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            // 6.ChannelPipeline中可以添加多个ChannelHandler
                            ch.pipeline().addLast(new StringDecoder()); // 将ByteBuf转换为字符串
                            ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){  // 自定义handler
                                // 读事件
                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) {
                                    // 读取客户端返回的数据
                                    System.out.println("server receive:" + msg);
                                }
                            });
                        }
                    });

            // 异步绑定到服务的指定端口, sync()会阻塞直到完成, 并返回ChannelFuture
            ChannelFuture channelFuture = bootstrap.bind("localhost",10086).sync();
            System.out.println("服务器启动完成....");
            // 阻塞当前现场, 直到服务器的ServerChannel被关闭, 开始监听操作
            channelFuture.channel().closeFuture().sync();
        } finally {
            // 出现异常时, 要关闭资源
            bossGroup.shutdownGracefully().sync();
            workerGroup.shutdownGracefully().sync();
        }
    }
}
