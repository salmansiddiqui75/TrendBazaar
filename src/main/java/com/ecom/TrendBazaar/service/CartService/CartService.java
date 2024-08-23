package com.ecom.TrendBazaar.service.CartService;

import com.ecom.TrendBazaar.model.Cart;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface CartService
{
    public Cart saveCart(int productId,int userId);
    public List<Cart> getCartByUserId(int userId);

    public int getCartCount(int userId);
    void updateQuantity(String sy, int cid);
}
