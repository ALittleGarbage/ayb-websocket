package com.ayb.websocket.server.mapping;

import java.util.HashMap;
import java.util.Map;

/**
 * Mapping管理类
 *
 * @author ayb
 * @date 2023/9/11
 */
public class MappingProvider {

    private final Map<String, WebSocketMapping> webSocketMappingMap = new HashMap<>(1024);

    public boolean isExist(String path) {
        return webSocketMappingMap.containsKey(path);
    }

    public WebSocketMapping get(String path) {
        return webSocketMappingMap.get(path);
    }

    public void set(String path, WebSocketMapping mapping) {
        webSocketMappingMap.put(path, mapping);
    }
}
