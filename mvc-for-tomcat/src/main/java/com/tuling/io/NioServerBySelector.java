package com.tuling.io;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Fox
 * IO多路复用模型
 *
 */
public class NioServerBySelector {

    public static void main(String[] args) {

        try {
            //创建服务，绑定端口8080
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(8080));

            // channel 非阻塞
            serverSocketChannel.configureBlocking(false);

            System.out.println("NioServer2 启动....");

            // 获取Selector   多路复用选择器
            Selector selector = Selector.open();
            //绑定selector,注册事件
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            while (true){
                // 返回就绪的channel
                //selector.select();// 阻塞，直到至少有一个channel就绪

                if(selector.select(3000)==0)
                    continue;

                System.out.println("返回就绪的channel");

                // 拿到就绪的通道集合  accept  read write
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();

                while (iterator.hasNext()){
                    SelectionKey selectionKey = iterator.next();
                    //删除
                    iterator.remove();
                    // 业务逻辑
                    handler(selectionKey);
                }
            }
        }catch (Exception e){

        }
    }

    private static void handler(SelectionKey selectionKey) throws IOException {
        // accept  read  write
        //连接事件
        if(selectionKey.isAcceptable()){
            System.out.println("有新的客户端建立连接");

            //serverSocketChannel.accept();
            // 非阻塞
            SocketChannel socketChannel =
                    ((ServerSocketChannel) selectionKey.channel()).accept();

            socketChannel.configureBlocking(false);
            // 注册事件
            socketChannel.register(selectionKey.selector(),SelectionKey.OP_READ);

        }else if(selectionKey.isReadable()){
            SocketChannel socketChannel = (SocketChannel) selectionKey.channel();

            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);//缓存区

            int data = socketChannel.read(byteBuffer);
            if(data>0){
                byteBuffer.flip();
                System.out.println("读取客户端发送的数据："+new String(byteBuffer.array(),0,data));
                byteBuffer.clear();
            }


        }



    }
}
