package com.bonanza.daip.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.bonanza.daip.market.upbit.candle.Orders;

public interface OrdersRepository extends JpaRepository<Orders, Long> {
}
