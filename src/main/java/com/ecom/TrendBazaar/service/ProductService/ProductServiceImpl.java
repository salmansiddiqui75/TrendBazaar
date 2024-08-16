package com.ecom.TrendBazaar.service.ProductService;

import com.ecom.TrendBazaar.model.Product;
import com.ecom.TrendBazaar.repository.productRepository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService
{

    @Autowired
    private ProductRepository productRepository;
    @Override
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    public List<Product> getAllProduct() {
        return productRepository.findAll();
    }

    @Override
    public Boolean deleteProduct(int id) {
        Product product = productRepository.findById(id).orElse(null);
        if(!ObjectUtils.isEmpty(product))
        {
            productRepository.delete(product);
            return true;
        }
        return false;
    }

    @Override
    public Product getProductById(int id) {
       Product product= productRepository.findById(id).orElse(null);
       return product;
    }


//    public void getProductById(int id) {
//        Optional<Product> byId=productRepository.findById(id);
//
////        if(!ObjectUtils.isEmpty(byId))
////        {
////            return byId.orElseThrow();
////        }
////    }
}
