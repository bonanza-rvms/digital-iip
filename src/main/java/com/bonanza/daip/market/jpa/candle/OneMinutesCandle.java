package com.bonanza.daip.market.jpa.candle;

import static java.math.RoundingMode.HALF_EVEN;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.Comment;

import com.bonanza.daip.market.upbit.candle.Candle;
import com.bonanza.daip.market.upbit.candle.MinuteCandle;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(of = "id")
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "tb_one_minutes_candle",
        indexes = {
                @Index(name = "tb_one_minutes_candle_idx1", columnList = "market,candle_date_time_utc"),
                @Index(name = "tb_one_minutes_candle_idx2", columnList = "market,candle_date_time_kst desc,timestamp"),
                @Index(name = "tb_one_minutes_candle_idx3", columnList = "market,candle_date_time_kst"),
                @Index(name = "tb_one_minutes_candle_idx4", columnList = "market,timestamp"),
                @Index(name = "tb_one_minutes_candle_idx5", columnList = "market,timestamp,trade_market")
        }
)
@Entity
public class OneMinutesCandle implements Candle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "market", length = 20)
    private String market;
    
    @Comment("거래소 : 업비트:UB, 빗썸:BT, 코인원:CO")
    @Column(name = "trade_market", length = 2)
    private String tradeMarket;

    @Column(name = "candle_date_time_utc")
    private LocalDateTime candleDateTimeUtc;                // 캔들 생성 UTC 시간

    @Column(name = "candle_date_time_kst")
    private LocalDateTime candleDateTimeKst;                // 캔들 생성 KST 시간

    @Column(name = "opening_price")
    private Double openingPrice;                            // 시가

    @Column(name = "high_price")
    private Double highPrice;                               // 고가

    @Column(name = "low_price")
    private Double lowPrice;                                // 저가

    @Column(name = "trade_price")
    private Double tradePrice;                              // 종가

    private Long timestamp;                                 // 해당 캔들에서 마지막 틱이 저장된 시각

    @Column(name = "candle_acc_trade_price")
    private Double candleAccTradePrice;                     // 누적 거래 금액

    @Column(name = "candle_acc_trade_volume")
    private Double candleAccTradeVolume;                    // 누적 거래량

    public static OneMinutesCandle of(MinuteCandle candle) {
        return new OneMinutesCandle(null,
                candle.getMarket(),
                candle.getTradeMarket(),
                candle.getCandleDateTimeUtc(),
                candle.getCandleDateTimeKst(),
                candle.getOpeningPrice(),
                candle.getHighPrice(),
                candle.getLowPrice(),
                candle.getTradePrice(),
                candle.getTimestamp(),
                candle.getCandleAccTradePrice(),
                candle.getCandleAccTradeVolume());
    }
    
    // 캔들의 상승률
    public double getCandlePercent() {
        return BigDecimal.valueOf((this.tradePrice / this.openingPrice * 100) - 100)
                .setScale(2, HALF_EVEN)
                .doubleValue();
    }

    // 캔들 양봉 여부
    public boolean isPositive() {
        return getCandlePercent() > 0;
    }

    // 캔들 음봉 여부
    public boolean isNegative() {
        return !isPositive();
    }

    public double getMedian() {
        return (this.openingPrice + this.tradePrice) / 2;
    }

	@Override
	public Double getTradePrice() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Double getOpeningPrice() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Double getLowPrice() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Double getHighPrice() {
		// TODO Auto-generated method stub
		return null;
	}
}
