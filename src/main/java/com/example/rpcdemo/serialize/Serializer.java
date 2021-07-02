package com.example.rpcdemo.serialize;

import java.io.IOException;

/**
 * 序列化器
 *
 * @author zhangjiawen
 * @date 2021/06/25
 */
public interface Serializer {
    /**
     * 序列化
     * java对象转换为二进制
     *
     * @param object 对象
     * @return {@link byte[]}
     * @throws IOException ioexception
     */
    byte[] serialize(Object object) throws IOException;

    /**
     * 反序列化
     * 二进制转换成java对象
     *
     * @param clazz clazz
     * @param bytes 字节
     * @return {@link T}
     * @throws IOException ioexception
     */
    <T> T deserialize(Class<T> clazz, byte[] bytes) throws IOException;
}