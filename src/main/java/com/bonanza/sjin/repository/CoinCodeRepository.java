package com.bonanza.sjin.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bonanza.sjin.market.jpa.CoinCode;

public interface CoinCodeRepository extends JpaRepository<CoinCode, Long> {
	
    @Query(nativeQuery = true, value = "select if(exists(select 1 from tb_coin_code where code = :code), 'true', 'false') as result ")
    boolean existsByCodeCode(@Param("code") String code);
    
    CoinCode findByCode(@Param(value = "code") String code);
}
