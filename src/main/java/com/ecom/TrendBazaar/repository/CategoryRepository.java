package com.ecom.TrendBazaar.repository;

import com.ecom.TrendBazaar.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category,Integer>
{
    public boolean existsByName(String name);
}
