package com.tuling.io;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Fox
 * proactor模型
 */
public class AioServer {

    public AsynchronousServerSocketChannel serverSocketChannel;

    public static void main(String[] args) throws Exception {
        new AioServer().listen();
        Thread.sleep(Integer.MAX_VALUE);
    }

    private void listen() throws IOException {

        //1. 创建一个线程池
        ExecutorService es = Executors.newCachedThreadPool();
        //2. 创建异步通道群组
        AsynchronousChannelGroup acg = AsynchronousChannelGroup.withCachedThreadPool(es, 1);

        //3. 创建服务端异步通道
        serverSocketChannel = AsynchronousServerSocketChannel.open(acg);
        //4. 绑定监听端口
        serverSocketChannel.bind(new InetSocketAddress(8080));
        System.out.println("AioServer 启动....");

        //5. 监听连接，传入回调类处理连接请求
        serverSocketChannel.accept(this, new CompletionHandler<AsynchronousSocketChannel, AioServer>() {
            //
            //            //具体处理连接请求的就是completed方法，它有两个参数：第一个是异步通道，第二个就是上面传入的AioServer对象
            @Override
            public void completed(AsynchronousSocketChannel socketChannel, AioServer attachment) {
                try {
                    if (socketChannel.isOpen()) {
                        System.out.println("接收到新的客户端的连接，地址："
                                + socketChannel.getRemoteAddress());
                        final ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                        //调用 read 函数读取客户端发送的数据
                        socketChannel.read(byteBuffer, socketChannel,
                                        new CompletionHandler<Integer, AsynchronousSocketChannel>() {
                                            @Override
                            public void completed(Integer result, AsynchronousSocketChannel attachment) {

                                try {
                                    //读取请求，处理客户端发送的数据
                                    byteBuffer.flip();
                                    String content = Charset.defaultCharset()
                                            .newDecoder().decode(byteBuffer).toString();
                                    System.out.println("服务端接受到客户端发来的数据：" + content);

                                } catch (CharacterCodingException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void failed(Throwable exc, AsynchronousSocketChannel attachment) {
                                exc.printStackTrace();
                                try {
                                    attachment.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                        });

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    //当有新的客户端接入的时候，直接调用accept的方法
                    attachment.serverSocketChannel.accept(attachment, this);
                }

            }

            @Override
            public void failed(Throwable exc, AioServer attachment) {
                exc.printStackTrace();
            }
        });
    }



}