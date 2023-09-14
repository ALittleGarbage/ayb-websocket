package com.ayb.websocket.spring.boot.starter.provider;

import com.ayb.websocket.exception.WebSocketException;
import com.ayb.websocket.server.WebSocketServer;
import com.ayb.websocket.server.mapping.MappingProvider;
import com.ayb.websocket.server.mapping.WebSocketMapping;
import com.ayb.websocket.spring.boot.starter.annotation.WebSocket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.HashMap;
import java.util.Map;

/**
 * 初始化WebSocketServer
 *
 * @author ayb
 * @date 2023/9/11
 */
@Slf4j
public class WebSocketProvider implements SmartInitializingSingleton, ApplicationContextAware {

    private final Map<String, WebSocketServer> serverMap = new HashMap<>(16);

    private ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    @Override
    public void afterSingletonsInstantiated() {
        String[] webSocketMappings = context.getBeanNamesForAnnotation(WebSocket.class);

        for (String webSocketMapping : webSocketMappings) {
            WebSocketMapping mapping = context.getBean(webSocketMapping, WebSocketMapping.class);

            WebSocket webSocket = mapping.getClass().getAnnotationsByType(WebSocket.class)[0];

            String key = String.valueOf(webSocket.port());

            if (!serverMap.containsKey(key)) {
                WebSocketServer webSocketServer = new WebSocketServer(new MappingProvider(), webSocket.host(),
                        webSocket.port(), webSocket.bossGroup(), webSocket.workerGroup());
                serverMap.put(key, webSocketServer);
            }

            WebSocketServer webSocketServer = serverMap.get(key);
            webSocketServer.addMapping(webSocket.path(), mapping);
        }

        for (WebSocketServer server : serverMap.values()) {
            try {
                server.start();
            } catch (Exception e) {
                log.error("服务端{}启动失败,原因:{}", server.listenAddress(), e.getMessage());
                WebSocketException.cast("服务端" + server.listenAddress() + "启动失败,原因:" + e.getMessage());
            }
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            for (WebSocketServer server : serverMap.values()) {
                server.stop();
            }
        }));

        log.info("WebSocketServer启动成功");
    }
}
