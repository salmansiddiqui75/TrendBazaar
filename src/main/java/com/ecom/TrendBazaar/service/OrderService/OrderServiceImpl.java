package com.ecom.TrendBazaar.service.OrderService;

import com.ecom.TrendBazaar.model.BillingAddress;
import com.ecom.TrendBazaar.model.Cart;
import com.ecom.TrendBazaar.model.OrderRequest;
import com.ecom.TrendBazaar.model.ProductOrder;
import com.ecom.TrendBazaar.repository.CartRepository.CartRepository;
import com.ecom.TrendBazaar.repository.OrderRepository.OrderRepository;
import com.ecom.TrendBazaar.util.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService
{
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CartRepository cartRepository;

    @Override
    public void saveOrder(int userId, OrderRequest orderRequest) {
        List<Cart> cartList = cartRepository.findByUserId(userId);
        for (Cart cart : cartList) {
            ProductOrder order = new ProductOrder();
            order.setOrderId(UUID.randomUUID().toString());
            order.setOrderDate(new Date());
            order.setProduct(cart.getProduct());
            order.setPrice(cart.getProduct().getDiscountPrice());
            order.setQuantity(cart.getQuantity());
            order.setUser(cart.getUser());
            order.setStatus(OrderStatus.IN_PROGRESS.getName());
            order.setPaymentType(orderRequest.getPaymentType());

            BillingAddress billingAddress = new BillingAddress();
            billingAddress.setFirstName(orderRequest.getFirstName());
            billingAddress.setLastName(orderRequest.getLastName());
            billingAddress.setEmail(orderRequest.getEmail());
            billingAddress.setMobileNumber(orderRequest.getMobileNumber());
            billingAddress.setAddress(orderRequest.getAddress());
            billingAddress.setCity(orderRequest.getCity());
            billingAddress.setState(orderRequest.getState());
            billingAddress.setPincode(orderRequest.getPincode());

            order.setBillingAddress(billingAddress);

            orderRepository.save(order);
        }
    }

    @Override
    public List<ProductOrder> getOrderByUserId(int id)
    {
        List<ProductOrder> orders=orderRepository.findByUserId(id);
        return orders;
    }

    @Override
    public Boolean updateOrderStatus(int id, String status) {
        Optional<ProductOrder> byUserId = orderRepository.findById(id);
        if(byUserId.isPresent())
        {
            ProductOrder order = byUserId.get();
            order.setStatus(status);
            orderRepository.save(order);
            return true;
        }
        return false;
    }

    @Override
    public List<ProductOrder> getAllOrders()
    {
        return orderRepository.findAll();
    }


}
