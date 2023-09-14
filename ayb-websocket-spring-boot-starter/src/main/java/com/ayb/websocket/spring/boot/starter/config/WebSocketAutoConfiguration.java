package com.ayb.websocket.spring.boot.starter.config;

import com.ayb.websocket.spring.boot.starter.provider.WebSocketProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * WebSocket自动配置类
 *
 * @author ayb
 * @date 2023/9/11
 */
@Configuration
public class WebSocketAutoConfiguration {

    @Bean
    public WebSocketProvider serverProvider() {
        return new WebSocketProvider();
    }
}
