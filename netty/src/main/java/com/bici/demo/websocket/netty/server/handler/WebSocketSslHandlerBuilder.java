package com.bici.demo.websocket.netty.server.handler;

import io.netty.handler.ssl.SslHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;

/**
 * websocket的SSL加密处理构造器
 * @author Administrator
 * @date 2018/11/17 15:29
 */
@Component
public class WebSocketSslHandlerBuilder {

	/** 证书类型 */
	@Value("${ssl.type}")
	private String type;

	@Value("${ssl.path}")
	private String path;

	@Value("${ssl.keypass}")
	private String keypass;

	/**
	 * 根据配置文件构造一个SslHandler
	 */
	public SslHandler build() throws Exception {
		return new SslHandler(createSSLEngine());
	}

	private SSLEngine createSSLEngine() throws Exception {
		KeyStore ks = KeyStore.getInstance(type); // "JKS"
		InputStream ksInputStream = new FileInputStream(path); // 证书存放地址
		ks.load(ksInputStream, keypass.toCharArray());
		// KeyManagerFactory充当基于密钥内容源的密钥管理器的工厂。
		// getDefaultAlgorithm:获取默认的 KeyManagerFactory 算法名称。
		KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		kmf.init(ks, keypass.toCharArray());
		//SSLContext的实例表示安全套接字协议的实现，它充当用于安全套接字工厂或 SSLEngine 的工厂。
		SSLContext sslContext = SSLContext.getInstance("TLS");
		sslContext.init(kmf.getKeyManagers(), null, null);
		SSLEngine sslEngine = sslContext.createSSLEngine();
		sslEngine.setUseClientMode(false);
		return sslEngine;
	}
}
