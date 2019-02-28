package com.bici.demo.websocket.netty.server.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;

/**
 * 消息处理
 * @author: Overload
 * @review:
 * @date: 2018/10/31 17:57
 */
@ChannelHandler.Sharable
@Component
@Slf4j
public class MessageHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        String text = msg.text();
        String format = MessageFormat.format(NettyWebSocketSession.groupName(ctx) + "说：{0}", text);
        NettyWebSocketSession.broadcastAll(format);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.debug("发生异常：", cause);
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        log.debug(NettyWebSocketSession.groupName(ctx) + "登录");
        String msg = NettyWebSocketSession.groupName(ctx) + "登录";
        NettyWebSocketSession.broadcastAll(msg);
        NettyWebSocketSession.addUser(ctx);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        log.debug(NettyWebSocketSession.group(ctx) + "登出");
        String msg = NettyWebSocketSession.group(ctx) + "登出";
        NettyWebSocketSession.broadcastAll(msg);
        NettyWebSocketSession.removeUser(ctx);
    }
}
