package com.bonanza.sjin.market.upbit.client;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.bonanza.sjin.config.enums.UpbitConfig;
import com.bonanza.sjin.market.upbit.result.accounts.AccountsResult;
import com.bonanza.sjin.utils.JsonUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class UpbitAssetClient {

    private final UpbitConfig upbitProperties;
    private final RestTemplate restTemplate;

    public List<AccountsResult> getAllAssets() {
        try {
            Algorithm algorithm = Algorithm.HMAC256(upbitProperties.getSecretKey());
            String jwtToken = JWT.create()
                    .withClaim("access_key", upbitProperties.getAccessKey())
                    .withClaim("nonce", UUID.randomUUID().toString())
                    .sign(algorithm);

            String authenticationToken = "Bearer " + jwtToken;

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
            httpHeaders.set("Authorization", authenticationToken);

            URI uri = URI.create(upbitProperties.getApiUrl() + "/v1/accounts");

            ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, new HttpEntity(httpHeaders), String.class);
            if(response.getStatusCode() != HttpStatus.OK) {
                throw new Exception("StatusCode = " + response.getStatusCode().value());
            }

            try {
                return JsonUtil.listFromJson(response.getBody(), AccountsResult.class);
            } catch(Exception e) {
                throw e;
            }
        } catch (Exception e) {
        	log.error("e", e);
        }
        return null;
    }

}
