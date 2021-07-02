package com.example.rpcdemo.client;

import com.example.rpcdemo.coder.RpcDecoder;
import com.example.rpcdemo.coder.RpcEncoder;
import com.example.rpcdemo.domain.RpcRequest;
import com.example.rpcdemo.domain.RpcResponse;
import com.example.rpcdemo.handler.ClientHandler;
import com.example.rpcdemo.serialize.JsonSerializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PreDestroy;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author zhangjiawen
 * @version 1.0
 * @date 2021/6/25 6:14 下午
 */
@Slf4j
public class NettyClient {

    private EventLoopGroup eventLoopGroup;
    private Channel channel;
    private ClientHandler clientHandler;
    private String host;
    private Integer port;
    private static final int MAX_RETRY = 5;

    public NettyClient(String host, Integer port) {
        this.host = host;
        this.port = port;
    }

    public synchronized void connect() throws InterruptedException {
        clientHandler = new ClientHandler();
        eventLoopGroup = new NioEventLoopGroup();
        //启动
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                //nio,aio,bio
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        //添加编码器
                        pipeline.addLast(new RpcEncoder(RpcRequest.class, new JsonSerializer()));
                        //添加解码器
                        pipeline.addLast(new RpcDecoder(RpcResponse.class, new JsonSerializer()));
                        //请求处理类
                        pipeline.addLast(clientHandler);
                    }
                });
        connect(bootstrap, host, port, MAX_RETRY).sync();

    }


    /**
     * 失败重连机制，参考Netty入门实战掘金小册
     *
     * @param bootstrap
     * @param host
     * @param port
     * @param retry
     */
    private ChannelFuture connect(Bootstrap bootstrap, String host, int port, int retry) {
        ChannelFuture channelFuture = bootstrap.connect(host, port).addListener(future -> {
            if (future.isSuccess()) {
                log.info("连接服务端成功");
            } else if (retry == 0) {
                log.error("重试次数已用完，放弃连接");
            } else {
                //第几次重连：
                int order = (MAX_RETRY - retry) + 1;
                //本次重连的间隔
                int delay = 1 << order;
                log.error("{} : 连接失败，第 {} 重连....", new Date(), order);
                bootstrap.config().group().schedule(() -> connect(bootstrap, host, port, retry - 1), delay, TimeUnit.SECONDS);
            }
        });
        channel = channelFuture.channel();
        return channelFuture;
    }

    /**
     * 发送消息
     *
     * @param request
     * @return
     */
    public RpcResponse send(final RpcRequest request) {

        try {
            channel.writeAndFlush(request).await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return clientHandler.getRpcResponse(request.getRequestId());
    }

    @PreDestroy
    public void close() {
        eventLoopGroup.shutdownGracefully();
        channel.closeFuture().syncUninterruptibly();
    }
}
