package com.bonanza.daip.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bonanza.daip.market.jpa.rt.TradeSise;

public interface TradeSiseRepository extends JpaRepository<TradeSise, Long> {
    @Query(nativeQuery = true, value = "select if(exists(select 1 from tb_sise_trade where market = :market and timestamp = :timestamp and trade_market = :trade_market and sequential_id = :sequential_id), 'true', 'false') as result ")
    boolean existsBySequentialIdAndMarket(@Param("market") String market,
                                       @Param("timestamp") Long timestamp,
                                       @Param("trade_market") String trade_market,
                                       @Param("sequential_id") Long sequential_id
                                       );
}
