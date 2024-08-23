package com.ecom.TrendBazaar.service.ProductService;

import com.ecom.TrendBazaar.model.Product;

import java.util.List;

public interface ProductService {
    public Product saveProduct(Product product);

    public List<Product> getAllProduct();

    Boolean deleteProduct(int id);

    Product getProductById(int id);

    List<Product> getAllActiveProduct(String category);
}
