package com.bonanza.daip.utils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import javax.net.ssl.SSLContext;

import org.apache.hc.client5.http.ClientProtocolException;
import org.apache.hc.client5.http.auth.AuthCache;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.auth.BasicAuthCache;
import org.apache.hc.client5.http.impl.auth.BasicScheme;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SslClient {

	private static final int MAX_CONNECTIONS_PER_ROUTE = 1024;
	private static final int MAX_CONNECTIONS_TOTAL = 1024;

	private CloseableHttpClient myHttpClient;

	@PostConstruct
	public void init() {

//		log.info("== Start : SslClient HttpClient 초기화");

		try {

			SSLContext sslContext;
			sslContext = new SSLContextBuilder().loadTrustMaterial(null, (certificate, authType) -> true).build();

			RequestConfig requestConfig = RequestConfig.custom()
			          .setConnectionRequestTimeout(Timeout.ofSeconds(10L))		// 연결시간초과 타임아웃
			          .setResponseTimeout(Timeout.ofSeconds(30L)).build();		// 읽기시간초과 타임아웃

			myHttpClient = HttpClientBuilder.create()
			        .setConnectionManager(PoolingHttpClientConnectionManagerBuilder.create()
			                .useSystemProperties()
			                .setMaxConnPerRoute(MAX_CONNECTIONS_PER_ROUTE)		// 커넥션풀적용(IP:포트 1쌍에 대해 수행 할 연결 수제한)
			                .setMaxConnTotal(MAX_CONNECTIONS_TOTAL)				// 커넥션풀적용(최대 오픈되는 커넥션 수)
			                .setDefaultConnectionConfig(ConnectionConfig.custom()
										                .setValidateAfterInactivity(TimeValue.ofSeconds(2))	//풀 속에서 대기하는 동안 상태가 좋지 않거나 반쯤 닫힌 커넥션을 제거시간
										                .build())
			                .setSSLSocketFactory(new SSLConnectionSocketFactory(sslContext))
			                .build())
			        .setDefaultRequestConfig(requestConfig)
			        .useSystemProperties()
			        .evictExpiredConnections()
			        .evictIdleConnections(TimeValue.ofMilliseconds(2000L))		// 서버에서 keepalive시간동안 미 사용한 커넥션을 죽이는 등의 케이스 방어로 idle커넥션을 주기적으로 지움
			        .build();

		} catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
			e.printStackTrace();
		}

	}

	public boolean send(String callbackUrl, Map<String, Object> params) throws KeyManagementException,
			NoSuchAlgorithmException, KeyStoreException, ClientProtocolException, IOException {

		URL url;
		ClassicHttpResponse response = null;
		try {
			url = new URL(callbackUrl);

			String hostName = url.getHost();
			int port = url.getPort();
			String protocol = url.getProtocol();

			HttpHost httpHost = new HttpHost(protocol, hostName, port);
//			CloseableHttpClient client = getHttpClient();
			HttpClientContext context = getHttpClientContext(httpHost);

			HttpPost post = new HttpPost(callbackUrl);
			post.setHeader("Accept", "application/json");
			post.setHeader("Content-Type", "application/json; charset=utf-8");

			ObjectMapper mapper = new ObjectMapper();
			String entity = mapper.writeValueAsString(params);
			post.setEntity(new StringEntity(entity, ContentType.APPLICATION_JSON, "UTF-8", false));

			log.info("execute = {} {}", post, entity);
			response = myHttpClient.executeOpen(httpHost, post, context);
			
			String responseEntityString = EntityUtils.toString(response.getEntity());
			log.info("response = {}", responseEntityString);

			int statusCode = response.getCode();
			if (statusCode == 200) {
				return true;
			}
		} catch (MalformedURLException e) {
			log.error("MalformedURLException", e);
		} catch (Exception e) {
			log.error("Exception", e);
		}finally {
			try {
				if(response != null) response.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
		return false;
	}
	public String send2Result(String callbackUrl, Map<String, Object> params) throws KeyManagementException,
	NoSuchAlgorithmException, KeyStoreException, ClientProtocolException, IOException {
		
		URL url;
		ClassicHttpResponse response = null;
		try {
			url = new URL(callbackUrl);
			
			String hostName = url.getHost();
			int port = url.getPort();
			String protocol = url.getProtocol();
			
			HttpHost httpHost = new HttpHost(protocol, hostName, port);
			HttpClientContext context = getHttpClientContext(httpHost);
			
			HttpPost post = new HttpPost(callbackUrl);
			post.setHeader("Accept", "application/json");
			post.setHeader("Content-Type", "application/json; charset=utf-8");
			
			ObjectMapper mapper = new ObjectMapper();
			String entity = mapper.writeValueAsString(params);
			post.setEntity(new StringEntity(entity, ContentType.APPLICATION_JSON, "UTF-8", false));
			
			log.info("execute = {} {}", post, entity);
			response = myHttpClient.executeOpen(httpHost, post, context);
			
			String responseEntityString = EntityUtils.toString(response.getEntity());
			log.info("response = {}", responseEntityString);
			
			int statusCode = response.getCode();
			if (statusCode == 200) {
				return responseEntityString;
			}
		} catch (MalformedURLException e) {
			log.error("MalformedURLException", e);
		} catch (Exception e) {
			log.error("Exception", e);
		}finally {
			try {
				if(response != null) response.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return "";
	}

	private HttpClientContext getHttpClientContext(HttpHost httpHost) {
		AuthCache authCache = new BasicAuthCache();
		BasicScheme basicAuth = new BasicScheme();
		authCache.put(httpHost, basicAuth);
		HttpClientContext context = HttpClientContext.create();
		context.setAuthCache(authCache);
		return context;
	}

//	private CloseableHttpClient getHttpClient()
//			throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
//		SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, (certificate, authType) -> true)
//				.build();
//
//		int timeout = 10000;
//		RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(timeout)
//				.setSocketTimeout(timeout).setConnectTimeout(timeout).build();
//
//		CloseableHttpClient client = HttpClients.custom().setDefaultRequestConfig(requestConfig)
//				.setSSLSocketFactory(new SSLConnectionSocketFactory(sslContext)).build();
//		return client;
//	}

}
