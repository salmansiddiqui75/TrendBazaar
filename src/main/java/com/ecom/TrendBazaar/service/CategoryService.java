package com.ecom.TrendBazaar.service;

import com.ecom.TrendBazaar.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryService
{
    public Category saveCategory(Category category);

    public boolean exitCategory(String name);

    public List<Category> getAllCategory();
    public boolean deleteCategoty(int id);

    Category getByIdCategory(int id);
    public List<Category> getAllActiveCategory();

    public Page<Category> getAllCategoryPagination(int pageNo ,int pageSize);
}
