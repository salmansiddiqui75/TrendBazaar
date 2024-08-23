package com.ecom.TrendBazaar.repository;

import com.ecom.TrendBazaar.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    public boolean existsByName(String name);

    List<Category> findByIsActiveTrue();
}
