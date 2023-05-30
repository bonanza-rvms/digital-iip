package com.bonanza.daip.listener;

import java.util.List;

public interface WebSocketService {
	
	/**
	 * 최초 서버 구동시 호출됩니다.
	 * 연결URL 및 요청설정을 합니다.
	 */
	void init();
	
	/**
	 * websocket 요청설정을 합니다.
	 */
	void declare();
	
	/**
	 * 상대서버에 연결을 합니다.
	 */
	void connectWebSocket();
	
	/**
	 * 데이터 수신 처리하는 함수입니다.
	 * @param recvData
	 */
	void receiveDataProc(String recvData);
	
	/**
	 * Websocket 연결시 전송 파라미터를 셋팅하는 함수입니다.
	 * @param codes
	 */
	void setParameter(List<String> codes);
	
	String getParameter();
	
	void reconnectWebSocket();
}
