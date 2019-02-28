package com.bici.demo.websocket.netty.server.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * @author 無痕剑
 * @date 2018/11/4 10:55
 */
public class NettyWebSocketSession {

	/** 连接用户 */
	public final static Map<String, Set<Channel>> USER_CHANNEL = new ConcurrentHashMap<>();

	/**
	 * 判断用户是否在线
	 */
	public static boolean online(ChannelHandlerContext channelHandlerContext) {
		Channel curChannel = channelHandlerContext.channel();
		for (Set<Channel> channels : USER_CHANNEL.values()) {
			for (Channel channel : channels) {
				if (Objects.equals(channel, curChannel)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 增加用户
	 * @param group 用户组名称
	 * @param channel 用户通通
	 */
	public static synchronized void addUser(String group, Channel channel) {
		Set<Channel> channels = USER_CHANNEL.get(group);
		if (channels == null || channels.isEmpty()) {
			Set<Channel> channelSet = new ConcurrentSkipListSet<>();
			channelSet.add(channel);
			channels = Collections.synchronizedSet(channelSet);
			USER_CHANNEL.put(group, channels);
		} else {
			channels.add(channel);
		}
	}

	public static synchronized void addUser(ChannelHandlerContext channelHandlerContext) {
		Channel channel = channelHandlerContext.channel();
		addUser(channel.id().asShortText(), channel);
	}

	/**
	 * 移除用户
	 * @param group 用户组名称
	 * @param channel 用户通通
	 */
	public static synchronized void removeUser(String group, Channel channel) {
		Set<Channel> channels = USER_CHANNEL.get(group);
		if (channels == null || channels.isEmpty()) {
			return;
		}
		channels.remove(channel);
	}


	public static synchronized void removeUser(ChannelHandlerContext channelHandlerContext) {
		Channel channel = channelHandlerContext.channel();
		removeUser(channel.id().asShortText(), channel);
	}

	/**
	 * 向所有用户广播消息
	 * @param msg 消息
	 */
	public static void broadcastAll(String msg) {
		USER_CHANNEL.keySet().forEach(group -> broadcastGroup(msg, group));
	}

	/**
	 * 向group中的成员广播消息
	 * @param msg 消息
	 * @param group 组名称
	 */
	public static void broadcastGroup(String msg, String group) {
		USER_CHANNEL.get(group).forEach(channel -> sendMsg(msg, channel));
	}

	/**
	 * 向channel中的用户发送消息
	 * @param msg 消息
	 * @param channel 用户通道
	 */
	public static void sendMsg(String msg, Channel channel) {
		channel.writeAndFlush(new TextWebSocketFrame(msg));
	}

	public static String attrString(Channel channel, String key) {
        return (String) channel.attr(AttributeKey.valueOf(key)).get();
    }

	public static String group(ChannelHandlerContext ctx) {
        return group(ctx.channel());
    }

	public static String name(ChannelHandlerContext ctx) {
        return name(ctx.channel());
    }

	public static String group(Channel channel) {
        return attrString(channel, "group");
    }

	public static String name(Channel channel) {
        return attrString(channel, "name");
    }

    public static String groupName(ChannelHandlerContext ctx) {
		String group = group(ctx);
		String name = name(ctx);
		return MessageFormat.format("组【{0}】用户【{1}】", group, name);
	}
}
