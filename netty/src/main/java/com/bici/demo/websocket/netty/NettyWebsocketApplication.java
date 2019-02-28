package com.bici.demo.websocket.netty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync(proxyTargetClass=true)
@SpringBootApplication
public class NettyWebsocketApplication {

    public static void main(String[] args) {
        SpringApplication.run(NettyWebsocketApplication.class, args);
    }
}
