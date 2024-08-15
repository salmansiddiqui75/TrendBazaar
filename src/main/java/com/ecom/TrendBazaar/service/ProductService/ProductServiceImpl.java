package com.ecom.TrendBazaar.service.ProductService;

import com.ecom.TrendBazaar.model.Product;
import com.ecom.TrendBazaar.repository.productRepository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService
{

    @Autowired
    private ProductRepository productRepository;
    @Override
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }
}
