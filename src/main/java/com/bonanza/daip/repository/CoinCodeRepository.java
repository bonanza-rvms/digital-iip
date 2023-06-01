package com.bonanza.daip.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.bonanza.daip.market.jpa.CoinCode;



public interface CoinCodeRepository extends JpaRepository<CoinCode, Long> {

    @Query(nativeQuery = true, value = "select if(exists(select 1 from tb_coin_code where code = :code), 'true', 'false') as result ")
    boolean existsByCodeCode(@Param("code") String code);

    CoinCode findByCode(@Param(value = "code") String code);

    @Query(value = "select c from CoinCode c order by c.code limit 50 offset :idx")
	List<CoinCode> findByList(@Param("idx") int idx);

    @Transactional
    @Modifying
    @Query("UPDATE CoinCode c SET c.logo = :logo , c.description = :description, c.logo_exts = :logoExts, c.english_name = :engName  WHERE c.code = :code")
    void updateSomeFieldByCode(@Param("code") String code, @Param("logo") String logo, @Param("description") String description, @Param("logoExts") String logoExts, @Param("engName") String engName);
}
