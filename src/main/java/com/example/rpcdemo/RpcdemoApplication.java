package com.example.rpcdemo;

import com.example.rpcdemo.proxy.ProxyFactory;
import com.example.rpcdemo.service.HelloService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * rpcdemo应用程序
 *
 * @author zhangjiawen
 * @date 2021/06/25
 */
@SpringBootApplication
@Slf4j
public class RpcdemoApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(RpcdemoApplication.class, args);
        HelloService helloService = ProxyFactory.create(HelloService.class);
        log.info("响应结果“: {}",helloService.hello("seth"));
    }

}
