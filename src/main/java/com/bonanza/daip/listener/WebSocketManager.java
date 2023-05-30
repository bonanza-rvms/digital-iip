package com.bonanza.daip.listener;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import org.jetbrains.annotations.NotNull;

import jakarta.annotation.Nullable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

@Slf4j
@Data
@EqualsAndHashCode(callSuper=false)
public abstract class WebSocketManager extends WebSocketListener implements WebSocketService{
	
	private WebSocket webSocket;
	private OkHttpClient client;
	private Request request;
	private String json;
	private String wssUrl;
    
	@Override
	public void declare() {
		this.client = new OkHttpClient.Builder()
				.readTimeout(0, TimeUnit.MILLISECONDS)
				.pingInterval(60, TimeUnit.SECONDS)	//websocket연결 유지를 위해 60초에 한번씩 PING 전송
				.build();
		this.request = new Request.Builder().url(wssUrl)
				.build();
	}
    
	@Override
    public void connectWebSocket() {
		
		if(this.wssUrl == null || "".equals(wssUrl)) {
			log.error("WebSocket URL is NULL.");
			throw new RuntimeException("접속 정보가 존재하지 않습니다.(URL : " + wssUrl+")");
		}
		
    	if(this.request == null || this.client == null) {
    		log.error("Socket is NULL");
    		throw new RuntimeException("Socket is NULL");
    	}
    	this.webSocket = this.client.newWebSocket(this.request, this);
    	this.client.dispatcher().executorService().shutdown();
    }
	
    @Override
    public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
        log.debug("Socket Closed : %s / %s\r\n", code, reason);
    }

    @Override
    public void onClosing(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
        log.debug("Socket Closing : %s / %s\n", code, reason);
        reconnectWebSocket(); // 연결 다시 시도
    }

    @Override
    public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, @Nullable Response response) {
        log.debug("Socket Error : " + t.getMessage());
//        reconnectWebSocket(); // 연결 다시 시도
    }

    @Override
    public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
        receiveDataProc(text);
    }

    @Override
    public void onMessage(@NotNull WebSocket webSocket, @NotNull ByteString bytes) {
        receiveDataProc(bytes.string(StandardCharsets.UTF_8));
    }
    
    @Override
    public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
    	webSocket.send(getParameter());
        //webSocket.close(NORMAL_CLOSURE_STATUS, null); // 없을 경우 끊임없이 서버와 통신함
    }
    
    @Override
    public String getParameter() {
        return this.json;
    }
    
    @Override
    public void reconnectWebSocket() {
        if (webSocket != null) {
            webSocket.cancel();
        }
        connectWebSocket();
    }
    
    public Object getParent() {
    	return this;
    }
}
