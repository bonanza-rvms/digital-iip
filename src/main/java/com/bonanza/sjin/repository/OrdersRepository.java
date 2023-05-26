package com.bonanza.sjin.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.bonanza.sjin.market.upbit.candle.Orders;

public interface OrdersRepository extends JpaRepository<Orders, Long> {
}
