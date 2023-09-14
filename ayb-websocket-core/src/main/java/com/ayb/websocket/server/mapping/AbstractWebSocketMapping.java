package com.ayb.websocket.server.mapping;

import com.ayb.websocket.utils.JsonUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.lang.reflect.ParameterizedType;

/**
 * WebSocket抽象类业务逻辑类
 *
 * @author ayb
 * @date 2023/9/11
 */
public abstract class AbstractWebSocketMapping<Q, R> implements WebSocketMapping {

    private final Class<Q> reqClass;

    public AbstractWebSocketMapping() {
        reqClass = (Class<Q>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    @Override
    public void message(ChannelHandlerContext ctx, TextWebSocketFrame frame) {
        Q request = JsonUtils.toObj(frame.text(), reqClass);
        R response = onMessage(ctx, request);
        ctx.writeAndFlush(new TextWebSocketFrame(JsonUtils.toJson(response)));
    }

    @Override
    public void binary(ChannelHandlerContext ctx, BinaryWebSocketFrame frame) {
        ByteBuf byteBuf = frame.content();
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);

        byte[] response = onBinary(ctx, bytes);

        ByteBuf resp = Unpooled.buffer(response.length);
        resp.writeBytes(bytes);
        ctx.writeAndFlush(new BinaryWebSocketFrame(resp));
    }

    @Override
    public void close(ChannelHandlerContext ctx) {
        onClose(ctx);
    }

    @Override
    public void open(ChannelHandlerContext ctx) {
        onOpen(ctx);
    }

    @Override
    public void error(ChannelHandlerContext ctx, Throwable cause) {
        onError(ctx, cause);
    }

    @Override
    public void event(ChannelHandlerContext ctx, Object evt) {
        onEvent(ctx, evt);
    }

    /**
     * 处理消息
     *
     * @param ctx
     * @param request
     * @return
     */
    protected abstract R onMessage(ChannelHandlerContext ctx, Q request);

    /**
     * 处理二进制消息
     *
     * @param ctx
     * @param content
     * @return
     */
    protected abstract byte[] onBinary(ChannelHandlerContext ctx, byte[] content);

    /**
     * 处理客户端关闭
     *
     * @param ctx
     */
    protected abstract void onClose(ChannelHandlerContext ctx);

    /**
     * 处理客户端第一次连接
     *
     * @param ctx
     */
    protected abstract void onOpen(ChannelHandlerContext ctx);

    /**
     * 处理错误
     *
     * @param ctx
     * @param cause
     */
    protected abstract void onError(ChannelHandlerContext ctx, Throwable cause);

    /**
     * 处理事件
     *
     * @param ctx
     * @param evt
     */
    protected abstract void onEvent(ChannelHandlerContext ctx, Object evt);

}
