package com.bonanza.daip.task.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.net.ssl.SSLContext;

import org.apache.hc.client5.http.auth.AuthCache;
import org.apache.hc.client5.http.classic.methods.HttpGet;
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
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.net.URIBuilder;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CoinMarketCapServiceImpl implements CoinMarketCapService {
	
	@Value("${coinmarketcap.api.key}")
	private String apiKey;
	
	@Value("${coinmarketcap.api.url}")
	private String cmcUrl;
	
	private CloseableHttpClient myHttpClient;
	
	private static final int MAX_CONNECTIONS_PER_ROUTE = 1024;
	private static final int MAX_CONNECTIONS_TOTAL = 1024;
	
	@PostConstruct
	public void init() {

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
	
	/**
	 * 코인마켓 캡에서 코인정보를 조회하여
	 * 업데이트 합니다.
	 * @return
	 */
	@Override
	public String coinCheckIn() {
		String body = coinMetaData();
	    try {
	    	JSONParser jsonParser = new JSONParser();
	    	JSONObject obj = (JSONObject) jsonParser.parse(body);
	    	JSONObject data = (JSONObject)obj.get("data");
	    	Set<String> keys = data.keySet();
	    	for (String key : keys) {
	    		log.debug("key :: " + key);
	    		JSONObject item = (JSONObject)data.get(key);
	    		String engName = (String)item.get("name");
	    		String logo = (String)item.get("logo");
	    		String desc = (String)item.get("description");
	    		
	    		
	    	}
	    }catch(ParseException e) {
	    	log.error("error ::", e);
	    }
	    
		return "";
	}
	
	public String coinIdMap() {
		String apiUri = "/v1/cryptocurrency/map";
		List<NameValuePair> paratmers = new ArrayList<NameValuePair>();
	    paratmers.add(new BasicNameValuePair("listing_status","active"));
	    paratmers.add(new BasicNameValuePair("start","1"));
	    paratmers.add(new BasicNameValuePair("limit","5000"));
	    paratmers.add(new BasicNameValuePair("sort","id"));
	    paratmers.add(new BasicNameValuePair("aux","platform,first_historical_data,last_historical_data,is_active"));
	    try{
		    String body = makeApiCall(apiUri, paratmers);
		    JSONParser jsonParser = new JSONParser();
	    	JSONObject obj = (JSONObject) jsonParser.parse(body);
		    log.debug("coinIdMap body :: " + body);
		}catch(ParseException e) {
	    	log.error("error ::", e);
	    }
	
		return "";
	}
	/**
	 * 코인정보를 조회 합니다.
	 * @return
	 */
	@Override
	public String coinMetaData() {
		String apiUri = "/v2/cryptocurrency/info";
		List<NameValuePair> paratmers = new ArrayList<NameValuePair>();
		paratmers.add(new BasicNameValuePair("aux","logo,description"));
	    paratmers.add(new BasicNameValuePair("symbol","BTC,ETH"));
	    
		return makeApiCall(apiUri, paratmers);
	}

	private String makeApiCall(String apiUri, List<NameValuePair> params) {

		URL url;
		ClassicHttpResponse response = null;
		try {
			url = new URL(cmcUrl + apiUri);
			log.debug("sendGet apiUri : {}", apiUri);
			log.debug("sendGet url : {}", url.toURI());

			String hostName = url.getHost();
			int port = url.getPort();
			String protocol = url.getProtocol();

			HttpHost httpHost = new HttpHost(protocol, hostName, port);
			HttpClientContext context = getHttpClientContext(httpHost);

			URIBuilder uriBuilder = new URIBuilder(url.toURI());
			uriBuilder.addParameters(params);
			uriBuilder.setCharset(StandardCharsets.UTF_8);
			log.debug("URIBuilder = {}", uriBuilder.toString());
			
			HttpGet httpGet = new HttpGet(uriBuilder.build());
			httpGet.setHeader("X-CMC_PRO_API_KEY", apiKey);

			response = myHttpClient.executeOpen(httpHost, httpGet, context);
			String responseEntityString = EntityUtils.toString(response.getEntity());
			log.debug("response = {}", responseEntityString);
			
			return responseEntityString;
		} catch (MalformedURLException e) {
			log.error("잘못된 URL입니다. {}", apiUri);
			return String.format("500|{\"rtnMsg\":\"%s\"}", e.getMessage());
		} catch (IOException e) {
			log.error(e.getMessage());
			return String.format("500|{\"rtnMsg\":\"%s\"}", e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage());
			return String.format("500|{\"rtnMsg\":\"%s\"}", e.getMessage());
		}finally {
			try {
				if(response != null) response.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private HttpClientContext getHttpClientContext(HttpHost httpHost) {
		AuthCache authCache = new BasicAuthCache();
		BasicScheme basicAuth = new BasicScheme();
		authCache.put(httpHost, basicAuth);
		HttpClientContext context = HttpClientContext.create();
		context.setAuthCache(authCache);
		return context;
	}
}
