package com.ayb.websocket.exception;

/**
 * WebSocket异常类
 *
 * @author ayb
 * @date 2023/9/11
 */
public class WebSocketException extends RuntimeException {

    public WebSocketException(String message) {
        super(message);
    }

    public static WebSocketException cast(String errMsg) {
        throw new WebSocketException(errMsg);
    }
}
