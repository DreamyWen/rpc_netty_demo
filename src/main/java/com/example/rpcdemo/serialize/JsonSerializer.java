package com.example.rpcdemo.serialize;


import com.alibaba.fastjson.JSON;

/**
 * json序列化器
 *
 * @author zhangjiawen
 * @date 2021/06/25
 */
public class JsonSerializer implements Serializer{

    @Override
    public byte[] serialize(Object object) {
        return JSON.toJSONBytes(object);
    }

    @Override
    public <T> T deserialize(Class<T> clazz, byte[] bytes) {
        return JSON.parseObject(bytes, clazz);
    }
}