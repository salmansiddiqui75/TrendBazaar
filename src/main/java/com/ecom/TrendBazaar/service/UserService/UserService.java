package com.ecom.TrendBazaar.service.UserService;

import com.ecom.TrendBazaar.model.User;

public interface UserService
{
    public User saveUser(User user);
    public User findByEmail(String email);

}
