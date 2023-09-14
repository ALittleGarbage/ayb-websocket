package com.ayb.example;


import com.ayb.example.mapping.MyMapping;
import com.ayb.websocket.server.WebSocketServer;
import com.ayb.websocket.server.mapping.MappingProvider;

/**
 * @author ayb
 * @date 2023/9/14
 */
public class WebSocketApplication {

    public static void main(String[] args) throws InterruptedException {
        WebSocketServer webSocketServer = new WebSocketServer(
                new MappingProvider(), "0.0.0.0", 9999, 1, 0);

        webSocketServer.addMapping("/ws", new MyMapping());

        webSocketServer.start();

        Runtime.getRuntime().addShutdownHook(new Thread(webSocketServer::stop));
    }
}
