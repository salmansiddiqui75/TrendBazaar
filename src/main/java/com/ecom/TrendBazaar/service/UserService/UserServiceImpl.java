package com.ecom.TrendBazaar.service.UserService;

import com.ecom.TrendBazaar.model.User;
import com.ecom.TrendBazaar.repository.UserRepository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService
{
    @Autowired
    private UserRepository repository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User saveUser(User user)
    {
        String encoded = passwordEncoder.encode(user.getPassword());
        user.setPassword(encoded);
        user.setRole("USER_ROLE");
        return repository.save(user);
    }


}
