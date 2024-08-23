package com.ecom.TrendBazaar.repository.OrderRepository;

import com.ecom.TrendBazaar.model.ProductOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<ProductOrder, Integer> {
}
