package com.ecom.TrendBazaar.repository.CartRepository;

import com.ecom.TrendBazaar.model.Cart;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Integer>
{
    public Cart findByProductIdAndUserId(int productId,int UserId);
    public int countByUserId(int userId);

    List<Cart> findByUserId(int userId);
}
