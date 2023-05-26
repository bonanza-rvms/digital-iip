package com.bonanza.sjin.market.jpa.rt;

import java.time.LocalDateTime;

import org.hibernate.annotations.Comment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(of = "id")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(
        name = "tb_sise_ticker",
        indexes = {
                @Index(name = "tb_sise_ticker_idx1", columnList = "code,trade_date"),
                @Index(name = "tb_sise_ticker_idx2", columnList = "code,trade_timestamp")
        }
)
@Entity
public class TickerSise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Comment("거래소 : 업비트:UB, 빗썸:BT, 코인원:CO")
    @Column(name = "trade_market", length = 2)
    private String trade_market;
    
//    private String type; // ticker

    @Comment("마켓 코드 (ex. KRW-BTC)")
    @Column(name = "code", length = 30)
    private String code;									//마켓 코드 (ex. KRW-BTC)

    @Comment("시가")
    @Column(name = "opening_price")
    private Double opening_price;                			// 시가

    @Comment("고가")
    @Column(name = "high_price")
    private Double high_price;                               // 고가

    @Comment("저가")
    @Column(name = "low_price")
    private Double low_price;                                // 저가

    @Comment("현재가")
    @Column(name = "trade_price")
    private Double trade_price;                              // 현재가
    
    @Comment("전일 종가")
    @Column(name = "prev_closing_price")
    private Double prev_closing_price;                        // 전일 종가
    
    @Comment("전일 대비(RISE:상승 EVEN:보합 FALL:하락)")
    @Column(name = "sise_change")
    private String change;									//전일 대비(RISE : 상승, EVEN : 보합, FALL : 하락)

    @Comment("부호 없는 전일 대비 값")
    @Column(name = "change_price")
    private Double change_price;                             // 부호 없는 전일 대비 값

    @Comment("전일 대비 값")
    @Column(name = "signed_change_price")
    private Double signed_change_price;                    	// 전일 대비 값

    @Comment("부호 없는 전일 대비 등락율")
    @Column(name = "change_rate")
    private Double change_rate; 
    
    @Comment("전일 대비 등락율")
    @Column(name = "signed_change_rate")
    private Double signed_change_rate;
    
    @Comment("가장 최근 거래량")
    @Column(name = "trade_volume")
    private Double trade_volume;
    
    @Comment("누적 거래량(UTC 0시 기준)")
    @Column(name = "acc_trade_volume")
    private Double acc_trade_volume;
    
    @Comment("24시간 누적 거래량")
    @Column(name = "acc_trade_volume_24h")
    private Double acc_trade_volume_24h;
    
    @Comment("누적 거래대금(UTC 0시 기준)")
    @Column(name = "acc_trade_price")
    private Double acc_trade_price;
    
    @Comment("24시간 누적 거래대금")
    @Column(name = "acc_trade_price_24h")
    private Double acc_trade_price_24h;
    
    @Comment("최근 거래 일자(UTC)")
    @Column(name = "trade_date", length = 8)
    private String trade_date;
    
    @Comment("최근 거래 시각(UTC)")
    @Column(name = "trade_time", length = 6)
    private String trade_time;
    
    @Comment("체결 타임스탬프 (milliseconds)")
    @Column(name = "trade_timestamp")
    private Long trade_timestamp;
    
    @Comment("매수/매도 구분(ASK:매도 BID:매수)")
    @Column(name = "ask_bid", length = 3)
    private String ask_bid;
    
    @Comment("누적 매도량")
    @Column(name = "acc_ask_volume")
    private Double acc_ask_volume;
    
    @Comment("누적 매수량")
    @Column(name = "acc_bid_volume")
    private Double acc_bid_volume;
    
    @Comment("52주 최고가")
    @Column(name = "highest_52_week_price")
    private Double highest_52_week_price;
    
    @Comment("52주 최고가 달성일 yyyy-MM-dd")
    @Column(name = "highest_52_week_date", length = 10)
    private String highest_52_week_date;
    
    @Comment("52주 최저가")
    @Column(name = "lowest_52_week_price")
    private Double lowest_52_week_price;
    
    @Comment("52주 최저가 달성일 yyyy-MM-dd")
    @Column(name = "lowest_52_week_date", length = 10)
    private String lowest_52_week_date;
    
    @Comment("거래상태 - PREVIEW:입금지원 ACTIVE:거래지원가능 DELISTED:거래지원종료")
    @Column(name = "market_state", length = 20)
    private String market_state;
    
    @Comment("거래 정지 여부")
    @Column(name = "is_trading_suspended")
    private Boolean is_trading_suspended;
    
    @Comment("상장폐지일")
    @Column(name = "delisting_date")
    private LocalDateTime delisting_date;
    
    @Comment("유의 종목 여부")
    @Column(name = "market_warning")
    private String market_warning;
    
    @Comment("타임스탬프 (millisecond)")
    @Column(name = "timestamp")
    private Long timestamp;
    
//    @Comment("스트림 타입")
//    @Column(name = "stream_type")
//    private String stream_type;

}
