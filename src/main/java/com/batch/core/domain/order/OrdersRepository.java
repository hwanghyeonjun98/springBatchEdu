package com.batch.core.domain.order;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdersRepository extends JpaRepository<Orders, Integer> {
}
