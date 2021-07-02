package com.example.rpcdemo.coder;

import com.example.rpcdemo.serialize.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * rpc编码器
 *
 * @author zhangjiawen
 * @date 2021/06/25
 */
public class RpcEncoder extends MessageToByteEncoder<Object> {
    private final Class<?> clazz;
    private final Serializer serializer;

    public RpcEncoder(Class<?> clazz, Serializer serializer) {
        this.clazz = clazz;
        this.serializer = serializer;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object msg, ByteBuf byteBuf) throws Exception {
        if (clazz != null && clazz.isInstance(msg)) {
            byte[] bytes = serializer.serialize(msg);
            byteBuf.writeInt(bytes.length);
            byteBuf.writeBytes(bytes);
        }

    }
}