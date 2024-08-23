package com.ecom.TrendBazaar.service.CartService;

import com.ecom.TrendBazaar.model.Cart;
import com.ecom.TrendBazaar.model.Product;
import com.ecom.TrendBazaar.model.User;
import com.ecom.TrendBazaar.repository.CartRepository.CartRepository;
import com.ecom.TrendBazaar.repository.UserRepository.UserRepository;
import com.ecom.TrendBazaar.repository.productRepository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CartServiceImpl implements CartService
{
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;

    @Override
    public Cart saveCart(int productId, int userId) {
        User user = userRepository.findById(userId).get();
        Product product = productRepository.findById(productId).get();
        Cart cartStatus=cartRepository.findByProductIdAndUserId(productId,userId);
        Cart cart=null;
        if(ObjectUtils.isEmpty(cartStatus))
        {
            cart=new Cart();
            cart.setProduct(product);
            cart.setUser(user);
            cart.setQuantity(1);
            cart.setTotalPrice(1*product.getDiscountPrice());
        }else{
            cart=cartStatus;
            cart.setQuantity(cart.getQuantity()+1);
            cart.setTotalPrice(cart.getQuantity()*cart.getProduct().getDiscountPrice());
        }
        Cart saveCart=cartRepository.save(cart);
        return saveCart;
    }

    @Override
    public List<Cart> getCartByUserId(int userId) {
        List<Cart> byUserId = cartRepository.findByUserId(userId);
        Double totalOrderPrice=0.0;
        List<Cart> updateCart=new ArrayList<>();
        for(Cart c:byUserId)
        {
           Double totalPrice=(c.getProduct().getDiscountPrice()*c.getQuantity());
           c.setTotalPrice(totalPrice);
           totalOrderPrice+=totalPrice;
           c.setTotalOrderPrice(totalOrderPrice);
           updateCart.add(c);
        }
        return updateCart;
    }

    @Override
    public int getCartCount(int userId) {
        return cartRepository.countByUserId(userId);
    }

    @Override
    public void updateQuantity(String sy, int cid)
    {
        Cart cart = cartRepository.findById(cid).get();
        int updateQuantity;
        if(sy.equalsIgnoreCase("de"))
        {
            updateQuantity=cart.getQuantity()-1;
            if(updateQuantity<=0)
            {
                cartRepository.deleteById(cid);
            }else{
                cart.setQuantity(updateQuantity);
                cartRepository.save(cart);
            }
        }
        else {
            updateQuantity = cart.getQuantity() + 1;
            cart.setQuantity(updateQuantity);
            cartRepository.save(cart);
        }
    }
}
