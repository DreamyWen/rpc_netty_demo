package com.example.rpcdemo.server;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.example.rpcdemo.coder.RpcDecoder;
import com.example.rpcdemo.coder.RpcEncoder;
import com.example.rpcdemo.domain.RpcRequest;
import com.example.rpcdemo.domain.RpcResponse;
import com.example.rpcdemo.handler.ServerHandler;
import com.example.rpcdemo.serialize.JsonSerializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import javax.imageio.spi.ServiceRegistry;

/**
 * 网状的服务器
 *
 * @author zhangjiawen
 * @date 2021/07/02
 */
@Slf4j
@Component
public class NettyServer implements InitializingBean {
    private EventLoopGroup boss = null;
    private EventLoopGroup worker = null;
    @Autowired
    private ServerHandler serverHandler;

    @Override
    public void afterPropertiesSet() throws Exception {
        //此处使用了zookeeper做注册中心，本文不涉及，可忽略
//        ServiceRegistry registry = new ZkServiceRegistry("127.0.0.1:2181");
        start(null);
    }

    public void start(ServiceRegistry registry) throws Exception {
        //负责处理客户端连接的线程池
        boss = new NioEventLoopGroup();
        //负责处理读写操作的线程池
        worker = new NioEventLoopGroup();
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        //添加解码器
                        pipeline.addLast(new RpcEncoder(RpcResponse.class, new JsonSerializer()));
                        //添加编码器
                        pipeline.addLast(new RpcDecoder(RpcRequest.class, new JsonSerializer()));
                        //添加请求处理器
                        pipeline.addLast(serverHandler);

                    }
                });
        bind(serverBootstrap, 8888);
    }

    /**
     * 如果端口绑定失败，端口数+1,重新绑定
     *
     * @param serverBootstrap
     * @param port
     */
    public void bind(final ServerBootstrap serverBootstrap, int port) {
        serverBootstrap.bind(port).addListener(future -> {
            if (future.isSuccess()) {
                log.info("端口[ {} ] 绑定成功", port);
            } else {
                log.error("端口[ {} ] 绑定失败", port);
                bind(serverBootstrap, port + 1);
            }
        });
    }

    @PreDestroy
    public void destory() throws InterruptedException {
        boss.shutdownGracefully().sync();
        worker.shutdownGracefully().sync();
        log.info("关闭Netty");
    }
}