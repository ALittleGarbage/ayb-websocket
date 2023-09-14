package com.ayb.websocket.server;


import com.ayb.websocket.server.handler.WebSocketServerHandler;
import com.ayb.websocket.server.mapping.MappingProvider;
import com.ayb.websocket.server.mapping.WebSocketMapping;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * WebSocketServer
 *
 * @author ayb
 * @date 2023/9/11
 */
public class WebSocketServer {

    private final MappingProvider mappingProvider;
    private final String host;
    private final int port;
    private final int bossGroupCount;
    private final int workerGroupCount;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    public WebSocketServer(MappingProvider mappingProvider, String host, int port, int bossGroup, int workerGroup) {
        this.host = host;
        this.port = port;
        this.mappingProvider = mappingProvider;
        this.bossGroupCount = bossGroup;
        this.workerGroupCount = workerGroup;
    }

    public void start() throws InterruptedException {
        bossGroup = new NioEventLoopGroup(bossGroupCount);
        workerGroup = new NioEventLoopGroup(workerGroupCount);

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        //添加http消息的转换，将socket数据流 转换为 HttpRequest， websocket协议添加这个是为了
                        //握手时候使用。 HttpServerCodec 与 HttpObjectAggregator会在第一次http请求后，被移除掉，握手结束了
                        // http编解码器
                        pipeline.addLast(new HttpServerCodec());
                        // 支持大数据流
                        pipeline.addLast(new ChunkedWriteHandler());
                        //将拆分的http消息聚合成一个消息。
                        pipeline.addLast(new HttpObjectAggregator(1024 * 64));
                        // 添加handler处理
                        pipeline.addLast(new WebSocketServerHandler(mappingProvider));
                    }
                });

        serverBootstrap.bind(host, port).sync();
    }

    public void stop() {
        if (!bossGroup.isShutdown()) {
            bossGroup.shutdownGracefully();
        }
        if (!workerGroup.isShutdown()) {
            workerGroup.shutdownGracefully();
        }
    }

    public String listenAddress() {
        return host + ":" + port;
    }

    public void addMapping(String path, WebSocketMapping webSocketMapping) {
        mappingProvider.set(path, webSocketMapping);
    }

}
