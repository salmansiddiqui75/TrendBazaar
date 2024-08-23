package com.ecom.TrendBazaar.service.OrderService;

import com.ecom.TrendBazaar.model.OrderRequest;

public interface OrderService
{
    public void saveOrder(int userId, OrderRequest orderRequest);

}
