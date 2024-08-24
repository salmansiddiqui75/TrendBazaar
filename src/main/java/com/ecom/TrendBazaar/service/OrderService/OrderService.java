package com.ecom.TrendBazaar.service.OrderService;

import com.ecom.TrendBazaar.model.OrderRequest;
import com.ecom.TrendBazaar.model.ProductOrder;

import java.util.List;

public interface OrderService
{
    public void saveOrder(int userId, OrderRequest orderRequest);
    public List<ProductOrder> getOrderByUserId(int id);
    public Boolean updateOrderStatus(int id,String status);

    public List<ProductOrder> getAllOrders();


}
