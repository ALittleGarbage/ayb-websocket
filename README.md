# ayb-websocket
基于netty实现的websocket框架

## 介绍
ayb-websocket基于netty实现了websocket框架，集成了spring-boot，开箱即用

## 快速开始
1. 导入依赖
   ```
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter</artifactId>
   </dependency>
   <dependency>
       <groupId>com.ayb</groupId>
       <artifactId>ayb-websocket-spring-boot-starter</artifactId>
   </dependency>
   ```
2. 实现`AbstractWebSocketMapping<Q, R>`类，搭配`@WebSocket()`配置监听地址以及路径，使用`@Component`装配到Spring容器中
   ```
   @Component
   @WebSocket(port = 9999, path = "/ws")
   public class MyMapping extends AbstractWebSocketMapping<String, String> {

       /**
        * 接收消息
        * @param ctx
        * @param request
        * @return
        */
       @Override
       protected String onMessage(ChannelHandlerContext ctx, String request) {
           // 发送事件
           ctx.pipeline().fireUserEventTriggered("接收消息:" + request);
           return "Hello, WebSocket";
       }
   
       /**
        * 接收二进制消息
        * @param ctx
        * @param content
        * @return
        */
       @Override
       protected byte[] onBinary(ChannelHandlerContext ctx, byte[] content) {
           return null;
       }
   
       /**
        * 客户端下线
        * @param ctx
        */
       @Override
       protected void onClose(ChannelHandlerContext ctx) {
           System.out.println("客户端" + ctx.channel().remoteAddress() + "下线");
       }
   
       /**
        * 客户端上线
        * @param ctx
        */
       @Override
       protected void onOpen(ChannelHandlerContext ctx) {
           System.out.println("客户端" + ctx.channel().remoteAddress() + "上线");
       }
   
       /**
        * 错误处理
        * @param ctx
        * @param cause
        */
       @Override
       protected void onError(ChannelHandlerContext ctx, Throwable cause) {
           System.out.println("出现错误,原因:" + cause.getMessage());
       }
   
       /**
        * 监听事件
        * @param ctx
        * @param evt
        */
       @Override
       protected void onEvent(ChannelHandlerContext ctx, Object evt) {
           if (evt instanceof String) {
               System.out.println("事件:" + evt);
           }
       }
   }
   ```
