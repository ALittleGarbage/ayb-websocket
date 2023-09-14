package com.ayb.websocket.spring.boot.starter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author ayb
 * @date 2023/9/11
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface WebSocket {

    String path() default "/";

    String host() default "0.0.0.0";

    int port() default 9999;

    int bossGroup() default 1;

    int workerGroup() default 0;
}
