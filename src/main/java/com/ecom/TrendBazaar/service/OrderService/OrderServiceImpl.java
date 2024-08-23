package com.ecom.TrendBazaar.service.OrderService;

import com.ecom.TrendBazaar.model.ProductOrder;
import com.ecom.TrendBazaar.repository.CartRepository.CartRepository;
import com.ecom.TrendBazaar.repository.OrderRepository.OrderRepository;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

@Setter
public class OrderServiceImpl implements OrderService
{
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CartRepository cartRepository;

    @Override
    public ProductOrder saveOrder(int userId) {
        return null;
    }
}
