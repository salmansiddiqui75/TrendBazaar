package com.ecom.TrendBazaar.service.OrderService;

import com.ecom.TrendBazaar.model.ProductOrder;
import lombok.Setter;


public interface OrderService
{
    public ProductOrder saveOrder(int userId);
}
