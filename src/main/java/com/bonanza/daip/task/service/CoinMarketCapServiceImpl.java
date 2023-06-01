package com.bonanza.daip.task.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

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
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.bonanza.daip.market.jpa.CoinCode;
import com.bonanza.daip.repository.CoinCodeRepository;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CoinMarketCapServiceImpl implements CoinMarketCapService {

	@Value("${coinmarketcap.api.key}")
	private String apiKey;

	@Value("${coinmarketcap.api.url}")
	private String cmcUrl;

	@Autowired
	CoinCodeRepository coinCodeRepository;

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
		//tb_coin_code에서 코인정보 조회
		String coinEngName = "";
		String coinLogoUrl = "";
		int idx = 0;
		while(true) {
			List<CoinCode> coinList2 = coinCodeRepository.findByList(idx);
			if (coinList2 == null || coinList2.size() == 0) {
				break;
			}
			// 50건씩 조회
			idx += 50;

			List<String> codeList = new ArrayList<>();
			for (CoinCode coinInfo : coinList2) {
				coinEngName = coinInfo.getEnglish_name();
				coinLogoUrl = coinInfo.getLogo();
				// 1.영문 이름 , 로고가 하나라도 없으면 조회
				if (
					(coinEngName != null && !"".equals(coinEngName))
					&& (coinLogoUrl != null && !"".equals(coinLogoUrl))
				) {
					continue;
				}

				// TOMO '토모체인'
				if("TOMOE".equals(coinInfo.getCode())) {
					codeList.add("TOMO");
					continue;
				}
				// FCT2 '피르마체인'
				if("FCT2".equals(coinInfo.getCode())) {
					codeList.add("FCT");
					continue;
				}
				// IOTA '아이오타'
				if("IOTA".equals(coinInfo.getCode())) {
					codeList.add("MIOTA");
					continue;
				}

				// 코인마켓에 정보없음
				if("GRACY".equals(coinInfo.getCode())) {
					continue;
				}
				// 코인마켓에 정보없음
				if("LN".equals(coinInfo.getCode())) {
					continue;
				}
				// 코인마켓에 정보없음 ('알파 벤처 다오')
				if("ALPHA".equals(coinInfo.getCode())) {
					continue;
				}

				codeList.add(coinInfo.getCode());
			}

			if(codeList.size() == 0) {
				log.debug(idx + " : 업데이트 완료상태");
				continue;
			}

			String body = coinMetaData(codeList);

		    try {
		    	JSONParser jsonParser = new JSONParser();
		    	JSONObject obj = (JSONObject) jsonParser.parse(body);
		    	JSONObject data = (JSONObject)obj.get("data");
		    	List<String> keys = new ArrayList<>(data.keySet());
		        Collections.sort(keys);
		    	for (String key : keys) {
		    		log.debug("key :: " + key);
		    		JSONArray keyArray = (JSONArray)data.get(key);
		    		// 티커가 2개이상 일 경우
		    		if (keyArray.size() > 1) {
			    		for (int i = 0; i < keyArray.size(); i++) {
			    		    JSONObject item = (JSONObject)keyArray.get(i);
			    		    Long id = (Long) item.get("id");
			    		    String code = (String) item.get("symbol");

			    			if("ACH".equals(code) && id != 6958) {continue;}
			    			if("AHT".equals(code) && id != 6641) {continue;}
			    			if("ALT".equals(code) && id != 22765) {continue;}
			    			if("AOA".equals(code) && id != 2874) {continue;}
			    			if("APT".equals(code) && id != 21794) {continue;}
			    			if("ARB".equals(code) && id != 11841) {continue;}
			    			if("ASM".equals(code) && id != 6069) {continue;}
			    			if("ATOM".equals(code) && id != 3794) {continue;}
			    			if("ATT".equals(code) && id != 15918) {continue;}
			    			if("BAT".equals(code) && id != 1697) {continue;}
			    			if("BAL".equals(code) && id != 6928) {continue;}
			    			if("BFC".equals(code) && id != 7817) {continue;}
			    			if("BIT".equals(code) && id != 11221) {continue;}
			    			if("BLUR".equals(code) && id != 23121) {continue;}
			    			if("BNT".equals(code) && id != 1727) {continue;}
			    			if("BOA".equals(code) && id != 4217) {continue;}
			    			if("BOBA".equals(code) && id != 14556) {continue;}
			    			if("BSV".equals(code) && id != 3602) {continue;}
			    			if("BTG".equals(code) && id != 2083) {continue;}
			    			if("BTR".equals(code) && id != 13142) {continue;}
			    			if("BTT".equals(code) && id != 16086) {continue;}
			    			if("CAKE".equals(code) && id != 7186) {continue;}
			    			if("CBK".equals(code) && id != 8107) {continue;}
							if("CFX".equals(code) && id != 7334) {continue;}
							if("CHR".equals(code) && id != 3978) {continue;}
							if("CLV".equals(code) && id != 8384) {continue;}
							if("COMP".equals(code) && id != 5692) {continue;}
							if("CON".equals(code) && id != 3866) {continue;}
							if("COS".equals(code) && id != 4036) {continue;}
							if("CRE".equals(code) && id != 3946) {continue;}
							if("CRO".equals(code) && id != 3635) {continue;}
							if("CRU".equals(code) && id != 6747) {continue;}
							if("CRV".equals(code) && id != 6538) {continue;}
							if("CTC".equals(code) && id != 5198) {continue;}
							if("CTK".equals(code) && id != 4807) {continue;}
							if("CVC".equals(code) && id != 1816) {continue;}
							if("DAD".equals(code) && id != 4862) {continue;}
							if("DAI".equals(code) && id != 4943) {continue;}
							if("DAO".equals(code) && id != 8420) {continue;}
							if("DATA".equals(code) && id != 2143) {continue;}
							if("DIA".equals(code) && id != 6138) {continue;}
							if("DRC".equals(code) && id != 8124) {continue;}
							if("DRM".equals(code) && id != 5837) {continue;}
							if("EFI".equals(code) && id != 8985) {continue;}
							if("EGG".equals(code) && id != 4467) {continue;}
							if("EL".equals(code) && id != 5382) {continue;}
							if("ELF".equals(code) && id != 2299) {continue;}
							if("ERN".equals(code) && id != 8615) {continue;}
							if("ETC".equals(code) && id != 1321) {continue;}
							if("FANC".equals(code) && id != 17450) {continue;}
							if("FCT".equals(code) && id != 4953) {continue;}
							if("FIT".equals(code) && id != 8499) {continue;}
							if("FLOKI".equals(code) && id != 10804) {continue;}
							if("FLOW".equals(code) && id != 4558) {continue;}
							if("FLUX".equals(code) && id != 3029) {continue;}
							if("FNCY".equals(code) && id != 22847) {continue;}
							if("FTM".equals(code) && id != 3513) {continue;}
							if("GAS".equals(code) && id != 1785) {continue;}
							if("GET".equals(code) && id != 2354) {continue;}
							if("GMT".equals(code) && id != 18069) {continue;}
							if("GMX".equals(code) && id != 11857) {continue;}
							if("GO".equals(code) && id != 2861) {continue;}
							// GRACY 코인마켓에 없음
							if("GRS".equals(code) && id != 258) {continue;}
							if("GRT".equals(code) && id != 6719) {continue;}
							if("HIFI".equals(code) && id != 23037) {continue;}
							if("HOOK".equals(code) && id != 22764) {continue;}
							if("HOT".equals(code) && id != 2682) {continue;}
							if("HUM".equals(code) && id != 3600) {continue;}
							if("HUNT".equals(code) && id != 5380) {continue;}
							if("ICX".equals(code) && id != 2099) {continue;}
							if("IPX".equals(code) && id != 5103) {continue;}
							if("IQ".equals(code) && id != 2930) {continue;}
							if("ISR".equals(code) && id != 3466) {continue;}
							if("JST".equals(code) && id != 5488) {continue;}
							if("KAI".equals(code) && id != 5453) {continue;}
							if("KSC".equals(code) && id != 6493) {continue;}
							if("LDN".equals(code) && id != 7992) {continue;}
							if("LINA".equals(code) && id != 7102) {continue;}
							if("LIT".equals(code) && id != 6833) {continue;}
							// LN 코인마켓에 없음
							if("MANA".equals(code) && id != 1966) {continue;}
							if("MAP".equals(code) && id != 4956) {continue;}
							if("MASK".equals(code) && id != 8536) {continue;}
							if("MBL".equals(code) && id != 4038) {continue;}
							if("MBX".equals(code) && id != 18895) {continue;}
							if("MCH".equals(code) && id != 4844) {continue;}
							if("MED".equals(code) && id != 2303) {continue;}
							if("MEGA".equals(code) && id != 22802) {continue;}
							if("META".equals(code) && id != 3418) {continue;}
							if("MIX".equals(code) && id != 4366) {continue;}
							if("MNR".equals(code) && id != 6053) {continue;}
							if("MOC".equals(code) && id != 2915) {continue;}
							if("MTA".equals(code) && id != 5748) {continue;}
							if("MTS".equals(code) && id != 7778) {continue;}
							if("MVC".equals(code) && id != 7703) {continue;}
							if("NEO".equals(code) && id != 1376) {continue;}
							if("NFT".equals(code) && id != 9816) {continue;}
							if("NPT".equals(code) && id != 18966) {continue;}
							if("OAS".equals(code) && id != 22265) {continue;}
							if("OCEAN".equals(code) && id != 3911) {continue;}
							if("ONG".equals(code) && id != 3217) {continue;}
							if("ONX".equals(code) && id != 12816) {continue;}
							if("OP".equals(code) && id != 11840) {continue;}
							if("ORB".equals(code) && id != 18839) {continue;}
							if("ORC".equals(code) && id != 5326) {continue;}
							if("PEPE".equals(code) && id != 24478) {continue;}
							if("PIB".equals(code) && id != 3768) {continue;}
							if("PLA".equals(code) && id != 7461) {continue;}
							if("REAP".equals(code) && id != 7677) {continue;}
							if("REI".equals(code) && id != 19819) {continue;}
							if("REN".equals(code) && id != 2539) {continue;}
							if("RFR".equals(code) && id != 2553) {continue;}
							if("RUSH".equals(code) && id != 9920) {continue;}
							if("SBD".equals(code) && id != 1312) {continue;}
							if("SC".equals(code) && id != 1042) {continue;}
							if("SML".equals(code) && id != 20594) {continue;}
							if("SNT".equals(code) && id != 1759) {continue;}
							if("SNX".equals(code) && id != 2586) {continue;}
							if("SOFI".equals(code) && id != 16552) {continue;}
							if("SOL".equals(code) && id != 5426) {continue;}
							if("SPA".equals(code) && id != 6715) {continue;}
							if("SSX".equals(code) && id != 5612) {continue;}
							if("STRK".equals(code) && id != 8911) {continue;}
							if("STX".equals(code) && id != 4847) {continue;}
							if("SUI".equals(code) && id != 20947) {continue;}
							if("SUN".equals(code) && id != 10529) {continue;}
							if("SWAP".equals(code) && id != 5829) {continue;}
							if("T".equals(code) && id != 17751) {continue;}
							if("TARI".equals(code) && id != 20142) {continue;}
							if("TITAN".equals(code) && id != 7206) {continue;}
							if("TON".equals(code) && id != 11419) {continue;}
							if("TRV".equals(code) && id != 4060) {continue;}
							if("TT".equals(code) && id != 3930) {continue;}
							if("UNI".equals(code) && id != 7083) {continue;}
							if("UOS".equals(code) && id != 4189) {continue;}
							if("UPP".equals(code) && id != 2866) {continue;}
							if("VALOR".equals(code) && id != 3875) {continue;}
							if("VELO".equals(code) && id != 7127) {continue;}
							if("VIX".equals(code) && id != 13298) {continue;}
							if("VRA".equals(code) && id != 3816) {continue;}
							if("WAVES".equals(code) && id != 1274) {continue;}
							if("WE".equals(code) && id != 20456) {continue;}
							if("WOM".equals(code) && id != 5328) {continue;}
							if("XCN".equals(code) && id != 18679) {continue;}
							if("XRP".equals(code) && id != 52) {continue;}

				    		String engName = (String)item.get("name");
				    		String logo = (String)item.get("logo");
				    		String[] logoExts = logo.split("\\.");
				    		String logoExt = logoExts[logoExts.length-1];
				    		String description = (String)item.get("description");

				    		String logImage = "";
							try {
					    		// 로고 파일 이미지 저장 및 DB저장
								logImage = saveCoinLogoImage(key, logo, logoExt);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}

							if("FCT".equals(code)) {
								code = "FCT2";
							}

							CoinCode co = coinCodeRepository.findByCode(code);
							if(co.getEnglish_name() != null && !"".equals(co.getEnglish_name())) {
								engName = co.getEnglish_name();
							}

							coinCodeRepository.updateSomeFieldByCode(code, logImage, description, logoExt, engName);
			    		}
		    		} else {
		    		    JSONObject item = (JSONObject)keyArray.get(0);
		    		    String code = (String) item.get("symbol");
			    		String engName = (String)item.get("name");
			    		String logo = (String)item.get("logo");
			    		String[] logoExts = logo.split("\\.");
			    		String logoExt = logoExts[logoExts.length-1];
			    		String description = (String)item.get("description");

			    		String logImage = "";
						try {
				    		// 로고 파일 이미지 저장 및 DB저장
							logImage = saveCoinLogoImage(key, logo, logoExt);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}

						if("MIOTA".equals(code)) {
							code = "IOTA";
						}
						if("TOMO".equals(code)) {
							code = "TOMOE";
						}
			    		coinCodeRepository.updateSomeFieldByCode(code, logImage, description, logoExt, engName);
		    		}
		    	}
		    }catch(ParseException e) {
		    	log.error("error ::", e);
		    }

		}
		return "";
	}

	public String saveCoinLogoImage(String key, String logo, String logoExt) throws InterruptedException {
		StringBuilder contentBuilder = new StringBuilder();
		String destinationPath = "src/main/resources/static/img/";
		String[] fileUrlArr = logo.split("\\/");
		String fileName = fileUrlArr[fileUrlArr.length-1];
		int cnt = 0;
        try {
            URL url = new URL(logo);
            InputStream in = url.openStream();
            // 이미지 URL 못받는 경우가 있어서 못받을 경우 1초씩 30번 확인
            if (in.available() == 0) {
            	while(true) {
            		Thread.sleep(1000);
            		in = url.openStream();
            		if (in.available() != 0) {
            			log.debug("이미지 용량확인");
            			break;
            		} else {
            			log.debug("이미지 용량없음 재시도..");
            		}
            		cnt++;
            		if (cnt == 30) {
            			throw new IOException(key + " 이미지 다운로드 실패.");
            		}
            	}
            }

            // 이미지 다운로드
            Files.copy(in, Path.of(destinationPath + fileName), StandardCopyOption.REPLACE_EXISTING);
            log.debug(key + " 이미지 다운로드가 완료되었습니다.");

            // 이미지를 바이트 배열로 변환하여 문자열로 저장
            byte[] imageBytes = Files.readAllBytes(Path.of(destinationPath + fileName));
            String imageAsString = Base64.getEncoder().encodeToString(imageBytes);
            contentBuilder.append(imageAsString);
            log.debug(key + " 이미지 문자열로 저장했습니다.");

            // 문자열을 다시 바이트 배열로 변환하여 이미지로 저장
            // 서버에 이미지 저장할 파일 경로
            //String destinationPath2 = "/src/main/resources/images" + fileName;
            //byte[] imageBytes2 = Base64.getDecoder().decode(imageAsString);
            //Files.write(Path.of(destinationPath2), imageBytes2, StandardOpenOption.CREATE);
            //log.debug(key + " 이미지를 복원하여 저장했습니다..");

        } catch (IOException e) {
            log.error(key + " 이미지 다운로드 중 오류가 발생하였습니다: " + e.getMessage());
        }

        return contentBuilder.toString();
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
	public String coinMetaData(List<String> list) {
		String apiUri = "/v2/cryptocurrency/info";
		List<NameValuePair> paratmers = new ArrayList<NameValuePair>();
		paratmers.add(new BasicNameValuePair("aux","logo,description"));
	    paratmers.add(new BasicNameValuePair("symbol",String.join(",", list)));

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
