package com.ayb.websocket.server.handler;

import cn.hutool.core.bean.BeanUtil;
import com.ayb.websocket.server.mapping.MappingProvider;
import com.ayb.websocket.server.mapping.WebSocketMapping;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * WebSocket处理类
 *
 * @author ayb
 * @date 2023/9/11
 */
@Slf4j
public class WebSocketServerHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

    private AttributeKey<String> PATH = AttributeKey.valueOf("WebSocketPath");

    private MappingProvider mappingProvider;

    public WebSocketServerHandler(MappingProvider mappingProvider) {
        this.mappingProvider = mappingProvider;
    }

    /**
     * 处理WebSocket握手认证
     *
     * @param ctx
     * @param msg
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            handleHandShake(ctx, (FullHttpRequest) msg);
        }

        super.channelRead(ctx, msg);
    }

    /**
     * 接收消息
     *
     * @param ctx
     * @param frame
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {
        handleWebSocketFrame(ctx, frame);
    }

    /**
     * 发生异常时
     *
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        WebSocketMapping mapping = getMapping(ctx);
        if (BeanUtil.isEmpty(mapping)) {
            return;
        }
        mapping.error(ctx, cause);
    }

    /**
     * 客户端下线时
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        WebSocketMapping mapping = getMapping(ctx);
        if (BeanUtil.isEmpty(mapping)) {
            return;
        }
        mapping.close(ctx);

    }

    /**
     * 事件监测
     *
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        WebSocketMapping mapping = getMapping(ctx);
        if (BeanUtil.isEmpty(mapping)) {
            return;
        }
        mapping.event(ctx, evt);

        super.userEventTriggered(ctx, evt);
    }

    private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
        if (frame instanceof TextWebSocketFrame) {
            WebSocketMapping mapping = getMapping(ctx);
            if (BeanUtil.isEmpty(mapping)) {
                return;
            }
            mapping.message(ctx, (TextWebSocketFrame) frame);
        } else if (frame instanceof BinaryWebSocketFrame) {
            WebSocketMapping mapping = getMapping(ctx);
            if (BeanUtil.isEmpty(mapping)) {
                return;
            }
            mapping.binary(ctx, (BinaryWebSocketFrame) frame);
        } else if (frame instanceof PingWebSocketFrame) {
            ctx.writeAndFlush(new PongWebSocketFrame(frame.content().retain()));
        } else if (frame instanceof CloseWebSocketFrame) {
            ctx.writeAndFlush(frame.retainedDuplicate()).addListener(ChannelFutureListener.CLOSE);
        }
    }

    private void handleHandShake(ChannelHandlerContext ctx, FullHttpRequest request) {
        QueryStringDecoder query = new QueryStringDecoder(request.uri());
        String path = query.path();

        if (!mappingProvider.isExist(path)) {
            log.error("path路径不匹配,请求path:{}", path);
            FullHttpResponse resp = new DefaultFullHttpResponse(HTTP_1_1, BAD_REQUEST);
            ctx.writeAndFlush(resp).addListener(ChannelFutureListener.CLOSE);
            return;
        }

        String wsUrl = "ws://" + request.headers().get(HttpHeaderNames.HOST) + request.uri();
        // websocket握手
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
                wsUrl, null, true, 5 * 1024 * 1024);
        WebSocketServerHandshaker handShaker = wsFactory.newHandshaker(request);
        if (handShaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        } else {
            handShaker.handshake(ctx.channel(), request);
        }

        ctx.channel().attr(PATH).set(path);

        WebSocketMapping mapping = getMapping(ctx);
        if (BeanUtil.isEmpty(mapping)) {
            return;
        }
        mapping.open(ctx);
    }

    private WebSocketMapping getMapping(ChannelHandlerContext ctx) {
        Attribute<String> pathAttr = ctx.channel().attr(PATH);
        String path = pathAttr.get();

        WebSocketMapping webSocketMapping = mappingProvider.get(path);
        if (BeanUtil.isEmpty(webSocketMapping)) {
            log.error("WebSocketMapping或者匹配的path不存在");
            return null;
        }

        return webSocketMapping;
    }
}
