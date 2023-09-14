package com.ayb.websocket.server.mapping;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

/**
 * 抽象业务类
 *
 * @author ayb
 * @date 2023/9/11
 */
public interface WebSocketMapping {

    /**
     * 响应消息
     *
     * @param ctx
     * @param frame
     * @return
     */
    void message(ChannelHandlerContext ctx, TextWebSocketFrame frame);

    /**
     * 二进制消息
     *
     * @param ctx
     * @param frame
     * @return
     */
    void binary(ChannelHandlerContext ctx, BinaryWebSocketFrame frame);

    /**
     * 新客户端连接
     *
     * @param ctx
     */
    void open(ChannelHandlerContext ctx);

    /**
     * 发生错误时
     *
     * @param ctx
     * @param cause
     */
    void error(ChannelHandlerContext ctx, Throwable cause);

    /**
     * 客户端下线时
     *
     * @param ctx
     */
    void close(ChannelHandlerContext ctx);

    /**
     * 空闲监听
     *
     * @param ctx
     * @param evt
     */
    void event(ChannelHandlerContext ctx, Object evt);
}
