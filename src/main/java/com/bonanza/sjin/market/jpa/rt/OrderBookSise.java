package com.bonanza.sjin.market.jpa.rt;

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
        name = "tb_sise_order_book",
        indexes = {
                @Index(name = "tb_sise_order_book_idx1", columnList = "code,timestamp")
        }
)
@Entity
public class OrderBookSise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Comment("거래소 : 업비트:UB, 빗썸:BT, 코인원:CO")
    @Column(name = "trade_market", length = 2)
    private String trade_market;

    @Comment("마켓 코드 (ex. KRW-BTC)")
    @Column(name = "code", length = 30)
    private String code;									//마켓 코드 (ex. KRW-BTC)

    @Comment("호가 매도 총 잔량")
    @Column(name = "total_ask_size")
    private Double total_ask_size;
    
    @Comment("호가 매수 총 잔량")
    @Column(name = "total_bid_size")
    private Double total_bid_size;
    
    @Comment("매도 호가")
    @Column(name = "ask_price")
    private Double ask_price;
    
    @Comment("매수 호가")
    @Column(name = "bid_price")
    private Double bid_price;
    
    @Comment("매도 잔량")
    @Column(name = "ask_size")
    private Double ask_size;    
    
    @Comment("매수 잔량")
    @Column(name = "bid_size")
    private Double bid_size;
    
    @Comment("타임스탬프 (millisecond)")
    private Long timestamp;

}
