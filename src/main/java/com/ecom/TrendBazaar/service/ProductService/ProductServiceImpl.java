package com.ecom.TrendBazaar.service.ProductService;

import com.ecom.TrendBazaar.model.Product;
import com.ecom.TrendBazaar.repository.productRepository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

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
        if (!ObjectUtils.isEmpty(product)) {
            productRepository.delete(product);
            return true;
        }
        return false;
    }

    @Override
    public Product getProductById(int id) {
        return productRepository.findById(id).orElse(null);
    }

    @Override
    public List<Product> getAllActiveProduct(String category) {
        List<Product> product = null;
        if (ObjectUtils.isEmpty(category)) {
            product = productRepository.findByIsActiveTrue();
        } else {
            product = productRepository.findByCategory(category);
        }
        return product;
    }

    @Override
    public List<Product> searchProduct(String ch) {
        return productRepository.findByTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(ch,ch);
    }

    @Override
    public Page<Product> searchProduct(int pageNo, int pageSize, String ch) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return productRepository.findByTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(ch,ch,pageable);

    }
    @Override
    public Page<Product> getAllActiveProductPagination(int pageNumber, int pageSize,String category)
    {
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
        Page<Product> productPage=null;
        if(ObjectUtils.isEmpty(category))
        {
            productPage=productRepository.findByIsActiveTrue(pageRequest);
        }
        else{
            productPage=productRepository.findByCategory(pageRequest,category);
        }
        return productPage;
    }

    @Override
    public Page<Product> searchActiveProductPagination(int pageNo, int pageSize, String category, String ch) {
        Page<Product> productPage=null;
        Pageable pageable=PageRequest.of(pageNo,pageSize);
        productPage= productRepository.findByIsActiveTrueAndTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(ch,ch,pageable);

//        if(ObjectUtils.isEmpty(category))
//        {
//            productPage=productRepository.findByIsActiveTrue(pageable);
//        }
//        else{
//            productPage=productRepository.findByCategory(pageable,category);
//        }
        return productPage;
    }


}
