package com.ecom.TrendBazaar.service.ProductService;

import com.ecom.TrendBazaar.model.Product;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductService {
    public Product saveProduct(Product product);

    public List<Product> getAllProduct();

    Boolean deleteProduct(int id);

    Product getProductById(int id);

    List<Product> getAllActiveProduct(String category);

    List<Product> searchProduct(String ch);

    public Page<Product> getAllActiveProductPagination(int pageNumber, int pageSize, String category);

    Page<Product> searchProduct(int pageNo,int pageSize,String ch);

    Page<Product> searchActiveProductPagination(int pageNo, int pageSize, String category, String ch);
}
