/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.remoting.api;

import org.apache.dubbo.common.URL;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.ssl.SslContext;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.List;

public class PortUnificationServerHandler extends ByteToMessageDecoder {

    private final SslContext sslCtx;
    private final URL url;
    private final List<WireProtocol> protocols;
    private final DefaultChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    public PortUnificationServerHandler(URL url, List<WireProtocol> protocols) {
        this(url, null,protocols);
    }

    public PortUnificationServerHandler(URL url, SslContext sslCtx, List<WireProtocol> protocols) {
        this.url = url;
        this.sslCtx = sslCtx;
        this.protocols = protocols;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }

    public DefaultChannelGroup getChannels() {
        return channels;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        channels.add(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        channels.remove(ctx.channel());
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
       // ctx.channel() 拿到的是NioSocketChannel 接收到数据ByteBuf后，会先解码，然后再触发Handler的channelRead

        // 根据前5个字节确定对应的协议
        // Will use the first five bytes to detect a protocol.
        if (in.readableBytes() < 5) {
            return;
        }

        // 看是不是HTTP2.0
        for (final WireProtocol protocol : protocols) {
            in.markReaderIndex();
            // 判断通信的协议
            final ProtocolDetector.Result result = protocol.detector().detect(ctx, in);
            in.resetReaderIndex();
            switch (result) {
                case UNRECOGNIZED:
                    continue;
                case RECOGNIZED:
                    // 符合个某个协议后，再給Socket连接对应的pipeline绑定Handler
                    protocol.configServerPipeline(url, ctx.pipeline(), sslCtx);
                    // 移除自身这个Handler
                    ctx.pipeline().remove(this);
                case NEED_MORE_DATA:
                    return;
                default:
                    return;
            }
        }
        // Unknown protocol; discard everything and close the connection.
        in.clear();
        ctx.close();
    }

}
