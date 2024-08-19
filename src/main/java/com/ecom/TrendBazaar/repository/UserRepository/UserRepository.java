package com.ecom.TrendBazaar.repository.UserRepository;

import com.ecom.TrendBazaar.model.User;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer>
{
    public User findByEmail(String email);

}
