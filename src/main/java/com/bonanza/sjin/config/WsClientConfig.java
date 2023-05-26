package com.bonanza.sjin.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.bonanza.sjin.listener.WebSocketService;
import com.bonanza.sjin.market.upbit.result.MarketCode;
import com.bonanza.sjin.utils.JsonUtil;

import io.ous.jtoml.ParseException;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

@Slf4j
@Component
public class WsClientConfig {

	@Autowired
	@Qualifier("BithumbTrade")
	WebSocketService bithumpWebSocketTradeListener;
	
	@Autowired
	@Qualifier("BithumbTicker")
	WebSocketService bithumpWebSocketTickerListener;
	
	@Autowired
	@Qualifier("BithumbOrder")
	WebSocketService bithumpWebSocketOrderListener;
	
	@Autowired
	@Qualifier("UpbitTrade")
	WebSocketService upbitWebSocketTradeListener;
	
	@Autowired
	@Qualifier("UpbitTicker")
	WebSocketService upbitWebSockeTickertListener;
	
	@Autowired
	@Qualifier("UpbitOrder")
	WebSocketService upbitWebSockeOrdertListener;
	
	@Autowired
	@Qualifier("CoinOneTrade")
	WebSocketService coinOneWebSocketTradeListener;
	
	/**
	 * 웹소켓 스타터
	 */
	public void start() {
		try {
//			startWsBithumb();
//			startWsUpbit();
			startWsCoinOne();
		} catch (IOException e) {
			log.error("error", e);
		}
	}
	
	/**
	 * 업비트 웹소켓 시작
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public void startWsUpbit() throws IOException {
		OkHttpClient client = new OkHttpClient();
		////////////////////////////////////////////////
		//거래 가능 코인 조회
		Request marketIn = new Request.Builder().url("https://api.upbit.com/v1/market/all?isDetails=false")
				.get()
				.addHeader("accept", "application/json")
				.build();
		Response marketOut = client.newCall(marketIn).execute();
		ResponseBody  body = marketOut.body();
		MarketCode[] list = JsonUtil.fromJson(body.string(), MarketCode[].class);
		
		List<String> target = new ArrayList<String>();
		for(MarketCode code: list) {
			target.add(code.getMarket());
		}
		
		log.debug("target :" + target);

		////////////////////////////////////////////////
		/****************************************************************/
		//웹소켓 수신
		//체결(TRADE)
		upbitWebSocketTradeListener.setParameter(List.of("KRW-BTC"));
		upbitWebSocketTradeListener.connectWebSocket();		
		
		/****************************************************************/
		//현재가(TICKER)
//		upbitWebSockeTickertListener.setParameter(List.of("KRW-BTC"));
//		upbitWebSockeTickertListener.connectWebSocket();	
		
		/****************************************************************/
		//호가(ORDER BOOK)
//		upbitWebSockeOrdertListener.setParameter(List.of("KRW-BTC"));
//		upbitWebSockeOrdertListener.connectWebSocket();		
	}
		
	
	/**
	 * 빗썸 웹소켓 시작
	 */
	public void startWsBithumb() throws IOException {
		//체결가
//		bithumpWebSocketTradeListener.setUrl("wss://pubwss.bithumb.com/pub/ws");
//		bithumpWebSocketTradeListener.setParameter(List.of("BTC_KRW"));
//		bithumpWebSocketTradeListener.connectWebSocket();
		
		//현재가
//		bithumpWebSocketTickerListener.setUrl("wss://pubwss.bithumb.com/pub/ws");
//		bithumpWebSocketTickerListener.setParameter(List.of("BTC_KRW"), List.of("30M"));
//		bithumpWebSocketTickerListener.connectWebSocket();
		
		//호가
//		bithumpWebSocketOrderListener.setParameter(List.of("BTC_KRW"));
//		bithumpWebSocketOrderListener.connectWebSocket();		
	}
	
	public void startWsCoinOne() throws IOException {
		//체결가
		coinOneWebSocketTradeListener.setParameter(List.of("BTC_KRW"));
		coinOneWebSocketTradeListener.connectWebSocket();
	}
}
