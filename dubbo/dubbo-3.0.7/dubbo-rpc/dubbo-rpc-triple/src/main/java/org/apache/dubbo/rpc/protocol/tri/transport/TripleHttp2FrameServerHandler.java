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

package org.apache.dubbo.rpc.protocol.tri.transport;

import org.apache.dubbo.common.logger.Logger;
import org.apache.dubbo.common.logger.LoggerFactory;
import org.apache.dubbo.rpc.HeaderFilter;
import org.apache.dubbo.rpc.PathResolver;
import org.apache.dubbo.rpc.TriRpcStatus;
import org.apache.dubbo.rpc.model.FrameworkModel;
import org.apache.dubbo.rpc.protocol.tri.compressor.DeCompressor;
import org.apache.dubbo.rpc.protocol.tri.stream.ServerStream;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http2.Http2DataFrame;
import io.netty.handler.codec.http2.Http2HeadersFrame;
import io.netty.handler.codec.http2.Http2ResetFrame;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.ReferenceCounted;

import java.util.List;
import java.util.concurrent.Executor;

/**
 * k1 服务端处理http 2的请求
 */
public class TripleHttp2FrameServerHandler extends ChannelDuplexHandler {

    private static final AttributeKey<ServerStream> SERVER_STREAM_KEY = AttributeKey.valueOf(
        "tri_server_stream");


    private static final Logger LOGGER = LoggerFactory.getLogger(
        TripleHttp2FrameServerHandler.class);
    private final PathResolver pathResolver;
    private final FrameworkModel frameworkModel;
    private final Executor executor;
    private final List<HeaderFilter> filters;
    private final String acceptEncoding;

    public TripleHttp2FrameServerHandler(
        FrameworkModel frameworkModel,
        Executor executor,
        List<HeaderFilter> filters) {
        this.frameworkModel = frameworkModel;
        this.executor = executor;
        this.filters = filters;
        this.acceptEncoding = String.join(",",
            frameworkModel.getExtensionLoader(DeCompressor.class).getSupportedExtensions());
        this.pathResolver = frameworkModel.getExtensionLoader(PathResolver.class)
            .getDefaultExtension();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof Http2HeadersFrame) {
            // 处理请求头
            onHeadersRead(ctx, (Http2HeadersFrame) msg);
        } else if (msg instanceof Http2DataFrame) {
            // 处理请求体
            onDataRead(ctx, (Http2DataFrame) msg);
        } else if (msg instanceof ReferenceCounted) {
            // ignored
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof Http2ResetFrame) {
            onResetRead(ctx, (Http2ResetFrame) evt);
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    public void onResetRead(ChannelHandlerContext ctx, Http2ResetFrame frame) {
        final ServerStream serverStream = ctx.channel().attr(SERVER_STREAM_KEY)
            .get();
        LOGGER.warn("Triple Server received remote reset errorCode=" + frame.errorCode());
        if (serverStream != null) {
            serverStream.transportObserver.cancelByRemote(TriRpcStatus.CANCELLED
                .withDescription("Cancel by remote peer, err_code=" + frame.errorCode()));
        }
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (LOGGER.isWarnEnabled()) {
            LOGGER.warn("Exception in processing triple message", cause);
        }
        TriRpcStatus status = TriRpcStatus.getStatus(cause,
            "Provider's error:\n" + cause.getMessage());
        final ServerStream serverStream = ctx.channel().attr(SERVER_STREAM_KEY)
            .get();
        if (serverStream != null) {
            serverStream.close(status, null);
        }
    }

    public void onDataRead(ChannelHandlerContext ctx, Http2DataFrame msg) throws Exception {
        final ServerStream serverStream = ctx.channel().attr(SERVER_STREAM_KEY)
            .get();
        serverStream.transportObserver.onData(msg.content(), msg.isEndStream());
    }

    public void onHeadersRead(ChannelHandlerContext ctx, Http2HeadersFrame msg) throws Exception {
        // 收到一个Http2HeadersFrame时，生成一个ServerStream，和ClientStream对应
        // 此处ctx.channel()拿到的是子Channel, 对应的是Http2StreamChannel，表示流
        ServerStream serverStream = new ServerStream(ctx.channel(), frameworkModel, executor,
            pathResolver, acceptEncoding, filters);
        ctx.channel().attr(SERVER_STREAM_KEY).set(serverStream);

        // 调用
        serverStream.transportObserver.onHeader(msg.headers(), msg.isEndStream());
    }

}
