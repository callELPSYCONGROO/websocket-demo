package com.bici.demo.websocket.netty.config;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * NettyWebsocketConfig
 * @author: Overload
 * @review:
 * @date: 2018/10/31 17:41
 */
@Configuration
public class NettyWebsocketConfig {

    @Value("${netty.thread.boss}")
    private Integer bossThread;

    @Value("${netty.thread.work}")
    private Integer workThread;

    @Bean(name = "bossGroup", destroyMethod = "shutdownGracefully")
    @Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public EventLoopGroup bossGroup() {
        return new NioEventLoopGroup(bossThread);
    }

    @Bean(name = "workGroup", destroyMethod = "shutdownGracefully")
    @Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public EventLoopGroup workGroup() {
        return new NioEventLoopGroup(workThread);
    }
}
