package com.ecom.TrendBazaar.repository.productRepository;

import com.ecom.TrendBazaar.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product,Integer> {
}
