package com.bonanza.sjin.market.bithumb.client;

import java.net.URI;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.bonanza.sjin.config.enums.BithumbConfig;
import com.bonanza.sjin.market.bithumb.candle.BithumbMinuteCandle;
import com.bonanza.sjin.market.bithumb.enums.BithumbMarketType;
import com.bonanza.sjin.utils.JsonUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class BithumbCandleClient {

    private final BithumbConfig bithumbProperties;
    private final RestTemplate restTemplate;

    public BithumbMinuteCandle getMinuteCandles(int interval,
    		BithumbMarketType market) {
        try {
            String url = String.format("%s/public/candlestick/%s/%s",
            		bithumbProperties.getApiUrl(),
            		market.getType(), 
            		interval)+"m";
            
            log.debug("path ::" + url);

            ResponseEntity<String> response = restTemplate.exchange(URI.create(url), HttpMethod.GET, HttpEntity.EMPTY, String.class);
            if (response.getStatusCode() != HttpStatus.OK) {
                throw new Exception("StatusCode = " + response.getStatusCode().value());
            }

            try {
            	//log.debug("response.getBody() ::" + response.getBody());
                return JsonUtil.fromJson(response.getBody(), BithumbMinuteCandle.class);
            } catch (Exception e) {
                throw e;
            }
        } catch (Exception e) {
        	log.error("e", e);
        }
        return null;
    }

}
