package com.ayb.websocket.utils;


import cn.hutool.json.JSONUtil;
import com.ayb.websocket.exception.WebSocketException;
import lombok.extern.slf4j.Slf4j;

/**
 * Json工具类
 *
 * @author ayb
 * @date 2023/9/12
 */
@Slf4j
public class JsonUtils {

    public static String toJson(Object obj) {
        return JSONUtil.toJsonStr(obj);
    }

    public static <T> T toObj(String json, Class<T> clazz) {
        try {
            if (clazz == String.class) {
                return (T) json;
            }
            return JSONUtil.toBean(json, clazz);
        } catch (Exception e) {
            log.error("json转对象时发生错误,json:{},class:{}", json, clazz);
            WebSocketException.cast("json转对象时,发生错误");
        }

        return null;
    }
}
