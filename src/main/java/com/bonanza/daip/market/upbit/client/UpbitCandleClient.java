package com.bonanza.daip.market.upbit.client;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.yaml.snakeyaml.util.UriEncoder;

import com.bonanza.daip.config.enums.UpbitConfig;
import com.bonanza.daip.market.upbit.candle.MinuteCandle;
import com.bonanza.daip.market.upbit.enums.MarketType;
import com.bonanza.daip.utils.JsonUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class UpbitCandleClient {

    private final UpbitConfig upbitProperties;
    private final RestTemplate restTemplate;

    public List<MinuteCandle> getMinuteCandles(int minutes,
                                               MarketType market,
                                               int count,
                                               LocalDateTime to) {
        if (count < 1) {
            return Collections.emptyList();
        }
        try {
            String url = String.format("%s/v1/candles/minutes/%s",
                    upbitProperties.getApiUrl(),
                    minutes);
            String queryString = String.format("market=%s&count=%s&to=%s",
                    market.getType(),
                    count,
                    to.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            String path = String.format("%s?%s", url, UriEncoder.encode(queryString));
            
            log.debug("path ::" + path);

            ResponseEntity<String> response = restTemplate.exchange(URI.create(path), HttpMethod.GET, HttpEntity.EMPTY, String.class);
            if (response.getStatusCode() != HttpStatus.OK) {
                throw new Exception("StatusCode = " + response.getStatusCode().value());
            }

            try {
            	log.debug("response.getBody() ::" + response.getBody());
                return JsonUtil.listFromJson(response.getBody(), MinuteCandle.class);
            } catch (Exception e) {
                throw e;
            }
        } catch (Exception e) {
        	log.error("e", e);
        }
        return null;
    }

}
