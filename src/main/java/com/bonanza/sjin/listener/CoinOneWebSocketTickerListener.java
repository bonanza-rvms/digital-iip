package com.bonanza.sjin.listener;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.bonanza.sjin.config.enums.CoinOneConfig;
import com.bonanza.sjin.config.enums.TradeMarketType;
import com.bonanza.sjin.market.coinone.result.CoinOneTickerResult;
import com.bonanza.sjin.market.jpa.rt.TickerSise;
import com.bonanza.sjin.market.type.CoinOneSiseType;
import com.bonanza.sjin.repository.TickerSiseRepository;
import com.bonanza.sjin.utils.JsonUtil;
import com.fasterxml.jackson.databind.JsonNode;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;

@Slf4j
@Component
@Qualifier("CoinOneTicker")
public class CoinOneWebSocketTickerListener extends WebSocketManager {

	@Autowired
	private TickerSiseRepository tickerSiseRepository;

	@Autowired
	private CoinOneConfig coinOneProperties;

	private Timer pingTimer;

	@PostConstruct
	@Override
	public void init() {
		super.setWssUrl(coinOneProperties.getWssUrl());
		super.declare();
	}

	/**
	 * PING을 수동으로 전송하기 때문에 재선언
	 */
	public void declare2() {
		super.setClient(new OkHttpClient.Builder()
				.readTimeout(0, TimeUnit.MILLISECONDS)
				.build());
		super.setRequest(new Request.Builder().url(super.getWssUrl())
				.build());
	}

	@Override
	public void receiveDataProc(String text) {
    	JsonNode node = JsonUtil.fromJson(text, JsonNode.class);
    	log.debug("receiveData::"+node.toPrettyString());
    	CoinOneTickerResult tickerResult = JsonUtil.fromJson(text, CoinOneTickerResult.class);

    	if("DATA".equals(tickerResult.getResponseType())) {

    		TickerSise target = new TickerSise();
    		target.setTrade_market(TradeMarketType.COINONE.getType());
    		target.setCode(tickerResult.getData().getPriceCurrency() + "-" + tickerResult.getData().getProductCurrency());
    		target.setOpening_price(Double.parseDouble(tickerResult.getData().getOpen())); // 시가
    		target.setHigh_price(Double.parseDouble(tickerResult.getData().getHigh())); // 고가
    		target.setLow_price(Double.parseDouble(tickerResult.getData().getLow())); // 저가
    		target.setPrev_closing_price(Double.parseDouble(tickerResult.getData().getYesterdayClose())); // 전일 종가
    		target.setTimestamp(tickerResult.getData().getTimestamp()); // 타임스템프
    		tickerSiseRepository.save(target);
        }
    }

    @Override
    public void setParameter(List<String> codes) {
    	//코인 목록을 저장
    	super.setJson(JsonUtil.toJson(List.of(codes)));
    }

    /**
     * PING 전송을 위한 Timer
     */
	public void startPingTimer() {
		pingTimer = new Timer();
		pingTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                sendPingMessage();
            }
        }, 0, 60 * 1000); // 1분마다 실행 (밀리초 단위)
	}

	/**
	 * PING 메시지 전송
	 */
	public void sendPingMessage() {
		log.debug("sendData1 ::" + JsonUtil.toJson(Ping.of("PING")));
		super.getWebSocket().send(JsonUtil.toJson(Ping.of("PING")));
	}

	/**
	 * 코인원은 PING을 보내는 타입이 달라 재설정
	 * 한꺼번에 여러코인을 요청할 수 없어서 반복문을통해 하나씩 구독 요청
	 */
    @Override
    public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
    	//PING 전송
    	sendPingMessage();

    	//PING TIMER 시작
    	this.startPingTimer();

    	//코인 거래체결 구독 시작
    	String[] list = JsonUtil.fromJson(super.getJson(), String[].class);
    	for(String item : list) {
    		log.debug("sendData2 ::" + JsonUtil.toJson(Ticket.of(Body.of(CoinOneSiseType.TICKER.getType(), Topic.of("KRW", item)))));
    		String sendData = JsonUtil.toJson(Ticket.of(Body.of(CoinOneSiseType.TICKER.getType(), Topic.of("KRW", item))));
    		super.getWebSocket().send(sendData);
    	}
    }


    @Data(staticConstructor = "of")
    private static class Ping {
        private final String requestType;
    }


    @Data(staticConstructor = "of")
    private static class Ticket {
        private final String requestType = "SUBSCRIBE";
        private final Body body;
    }

    @Data(staticConstructor = "of")
    private static class Body {
        private final String channel;
        private final Topic topic;
    }

    @Data(staticConstructor = "of")
    private static class Topic {
        private final String priceCurrency;
        private final String productCurrency;
    }
}
