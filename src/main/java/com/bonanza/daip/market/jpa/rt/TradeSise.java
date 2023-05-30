package com.bonanza.daip.market.jpa.rt;

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
        name = "tb_sise_trade",
        indexes = {
                @Index(name = "tb_sise_trade_idx1", columnList = "code,trade_date"),
                @Index(name = "tb_sise_trade_idx2", columnList = "sequential_id")
        }
)
@Entity
public class TradeSise {

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

    @Comment("체결 가격")
    @Column(name = "trade_price")
    private Double trade_price;                              // 현재가
    
    @Comment("체결량")
    @Column(name = "trade_volume")
    private Double trade_volume;
    
    @Comment("매수/매도 구분(ASK:매도 BID:매수)")
    @Column(name = "ask_bid")
    private String ask_bid;
    
    @Comment("전일 종가")
    @Column(name = "prev_closing_price")
    private Double prev_closing_price;
    
    @Comment("전일 대비(RISE:상승 EVEN:보합 FALL:하락)")
    @Column(name = "sise_change")
    private String change;
    
    @Comment("부호 없는 전일 대비 값")
    @Column(name = "change_price")
    private Double change_price;    
    
    @Comment("체결 일자(UTC 기준)")
    @Column(name = "trade_date", length = 10)
    private String trade_date;
    
    @Comment("체결 시각(UTC 기준)")
    @Column(name = "trade_time", length = 8)
    private String trade_time;
    
    @Comment("체결 타임스탬프 (milliseconds)")
    @Column(name = "trade_timestamp")
    private Long trade_timestamp;
    
    @Comment("타임스탬프 (millisecond)")
    private Long timestamp;
    
    @Comment("체결 번호 (Unique)")
    @Column(name = "sequential_id")
    private Long sequential_id;
    
//    @Comment("스트림 타입")
//    @Column(name = "stream_type")
//    private String stream_type; 
}
