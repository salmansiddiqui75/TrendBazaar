package com.ecom.TrendBazaar.service.OrderService;

import com.ecom.TrendBazaar.model.OrderRequest;
import com.ecom.TrendBazaar.model.ProductOrder;
import jakarta.mail.MessagingException;
import org.springframework.data.domain.Page;

import java.io.UnsupportedEncodingException;
import java.util.List;

public interface OrderService
{
    public void saveOrder(int userId, OrderRequest orderRequest) throws MessagingException, UnsupportedEncodingException;
    public List<ProductOrder> getOrderByUserId(int id);
    public ProductOrder updateOrderStatus(int id,String status);

    public List<ProductOrder> getAllOrders();

    public ProductOrder findOrderByOrderId(String orderId);

    public Page<ProductOrder> getAllOrdersPagination(int pageNo,int pageSize);




}
