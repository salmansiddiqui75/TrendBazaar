package com.ecom.TrendBazaar.repository.OrderRepository;

import com.ecom.TrendBazaar.model.ProductOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<ProductOrder, Integer> {
    List<ProductOrder> findByUserId(int id);

    ProductOrder findByOrderId(String orderId);
}
