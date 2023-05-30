package com.bonanza.daip.market.upbit.candle;


import static lombok.AccessLevel.PROTECTED;

import java.time.LocalDateTime;

import com.bonanza.daip.market.upbit.enums.OrdSideType;
import com.bonanza.daip.market.upbit.result.orders.SingleOrderResult;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
@Table
@Entity
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "market", length = 10)
    private String market;

    @Column(name = "side")
    private OrdSideType side;

    @Column(name = "price")
    private Double price; // 1코인 당 거래가격

    @Column(name = "volume")
    private Double volume; // 거래량

    @Column(name = "fee")
    private Double fee; // 수수료

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public static Orders of(SingleOrderResult singleOrderResult) {
        return new Orders(
                null,
                singleOrderResult.getMarket(),
                singleOrderResult.getSide(),
                singleOrderResult.getTrades().get(0).getPrice().doubleValue(),
                singleOrderResult.getExecutedVolume().doubleValue(),
                singleOrderResult.getPaidFee().doubleValue(),
                LocalDateTime.now()
        );
    }

}
