package com.ayb.example.mapping;


import com.ayb.websocket.server.mapping.AbstractWebSocketMapping;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author ayb
 * @date 2023/9/14
 */
public class MyMapping extends AbstractWebSocketMapping<String, String> {

    @Override
    protected String onMessage(ChannelHandlerContext ctx, String request) {
        // 发送事件
        ctx.pipeline().fireUserEventTriggered("接收消息:" + request);

        return "Hello, WebSocket";
    }

    @Override
    protected byte[] onBinary(ChannelHandlerContext ctx, byte[] content) {
        return null;
    }

    @Override
    protected void onClose(ChannelHandlerContext ctx) {
        System.out.println("客户端" + ctx.channel().remoteAddress() + "下线");
    }

    @Override
    protected void onOpen(ChannelHandlerContext ctx) {
        System.out.println("客户端" + ctx.channel().remoteAddress() + "上线");
    }

    @Override
    protected void onError(ChannelHandlerContext ctx, Throwable cause) {
        System.out.println("出现错误,原因:" + cause.getMessage());
    }

    @Override
    protected void onEvent(ChannelHandlerContext ctx, Object evt) {
        if (evt instanceof String) {
            System.out.println("事件:" + evt);
        }
    }
}
