package com.example.rpcdemo.proxy;

import com.example.rpcdemo.client.NettyClient;
import com.example.rpcdemo.domain.RpcRequest;
import com.example.rpcdemo.domain.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.UUID;

/**
 * rpc客户端动态代理
 *
 * @author zhangjiawen
 * @date 2021/07/02
 */
@Slf4j
public class RpcClientDynamicProxy<T> implements InvocationHandler {
    private Class<T> clazz;
    public RpcClientDynamicProxy(Class<T> clazz) throws Exception {
        this.clazz = clazz;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest request = new RpcRequest();
        String requestId = UUID.randomUUID().toString();

        String className = method.getDeclaringClass().getName();
        String methodName = method.getName();

        Class<?>[] parameterTypes = method.getParameterTypes();

        request.setRequestId(requestId);
        request.setClassName(className);
        request.setMethodName(methodName);
        request.setParameterTypes(parameterTypes);
        request.setParameters(args);
        log.info("请求内容: {}",request);

        //开启Netty 客户端，直连
        NettyClient nettyClient = new NettyClient("127.0.0.1", 8888);
        log.info("开始连接服务端：{}",new Date());
        nettyClient.connect();
        RpcResponse send = nettyClient.send(request);
        log.info("请求调用返回结果：{}", send.getResult());
        return send.getResult();
    }
}
