package com.example.rpcdemo.service;

import org.springframework.stereotype.Service;

/**
 * 你好服务impl
 * 服务端实现
 *
 * @author zhangjiawen
 * @date 2021/07/02
 */
@Service
public class HelloServiceImpl implements HelloService {
    @Override
    public String hello(String name) {
        return "hello, " + name;
    }
}