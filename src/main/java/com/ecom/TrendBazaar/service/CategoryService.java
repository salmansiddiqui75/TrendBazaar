package com.ecom.TrendBazaar.service;

import com.ecom.TrendBazaar.model.Category;

import java.util.List;

public interface CategoryService
{
    public Category saveCategory(Category category);

    public boolean exitCategory(String name);

    public List<Category> getAllCategory();
}
