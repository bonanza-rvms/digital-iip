package com.bonanza.sjin.market.upbit.client;


import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import com.bonanza.sjin.config.enums.UpbitConfig;
import com.bonanza.sjin.market.upbit.enums.MarketUnit;
import com.bonanza.sjin.market.upbit.result.market.MarketResult;
import com.bonanza.sjin.utils.JsonUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class UpbitAllMarketClient {

    private final UpbitConfig properties;
    private final RestTemplate restTemplate;

    public List<MarketResult> getAllMarketInfo(MarketUnit marketUnit) {
        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

            URI uri = URI.create(properties.getApiUrl() + "/v1/market/all?isDetails=false");

            ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, new HttpEntity(httpHeaders), String.class);

            if (response.getStatusCode() != HttpStatus.OK) {
                throw new Exception("StatusCode = " + response.getStatusCode().value());
            }
            try {
                List<MarketResult> marketResults = JsonUtil.listFromJson(response.getBody(), MarketResult.class);
                if (CollectionUtils.isEmpty(marketResults)) {
                    return Collections.emptyList();
                }
                return marketResults.stream()
                                    .filter(market -> market.getMarket() != null)
                                    .filter(market -> market.getMarket().contains(marketUnit))
                                    .toList();
            } catch (Exception e) {
                throw e;
            }
        } catch(Exception e) {
        	log.error("e", e);
        }
        return null;
    }

}
