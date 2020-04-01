package com.bici.demo.websocket.netty.server;

import com.bici.demo.websocket.netty.server.handler.AuthenticationHandler;
import com.bici.demo.websocket.netty.server.handler.MessageHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * NettyWebSocketServer
 * @author: Overload
 * @review:
 * @date: 2018/10/31 17:47
 */
@Component
@Slf4j
public class NettyWebSocketServer implements ApplicationRunner {

    @Value("${netty.port}")
    private Integer port;

    @Value("${netty.thread.boss}")
    private Integer bossThread;

    @Value("${netty.thread.work}")
    private Integer workThread;

    @Value("${netty.maxContentLength}")
    private Integer maxContentLength;

    @Value("${netty.websocket.path}")
    private String path;

    private final MessageHandler messageHandler;

    private final AuthenticationHandler authenticationHandler;

    @Autowired
    public NettyWebSocketServer(MessageHandler messageHandler,
                                AuthenticationHandler authenticationHandler) {
        this.messageHandler = messageHandler;
        this.authenticationHandler = authenticationHandler;
    }

    @Async
    @Override
    public void run(ApplicationArguments args) throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup(bossThread);
        EventLoopGroup workGroup = new NioEventLoopGroup(workThread);
        log.info("开始实例化ServerBootstrap");
        ServerBootstrap serverBootstrap = new ServerBootstrap()
                .group(bossGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.DEBUG))
                .childHandler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ch.pipeline()
                                // 将请求和响应消息解码为HTTP协议消息
                                .addLast(new HttpServerCodec())
                                // 向客户端发送HTML5文件，大文件支持
                                .addLast(new ChunkedWriteHandler())
                                // 将HTTP消息的多个部分合成一条完整的HTTP消息
                                .addLast(new HttpObjectAggregator(maxContentLength, true))
                                // 登录认证
                                .addLast("authenticationHandler", authenticationHandler)
                                // 处理websocket扩展，WebSocket数据压缩
                                .addLast(new WebSocketServerCompressionHandler())
                                // 处理非 Text and Binary 数据格式
                                .addLast(new WebSocketServerProtocolHandler(path, null, true))
                                // 消息处理
                                .addLast("messageHandler", messageHandler);
                    }
                })
                .childOption(ChannelOption.SO_KEEPALIVE, true);
        log.info("ServerBootstrap实例化完成");
        try {
            log.info("WebSocket服务端开始启动");
            Channel channel = serverBootstrap.bind(port).sync().channel();
            log.info("WebSocket服务端启动成功");
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("netty发生异常：", e);
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }
}
