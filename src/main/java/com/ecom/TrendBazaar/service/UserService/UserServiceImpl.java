package com.ecom.TrendBazaar.service.UserService;

import com.ecom.TrendBazaar.model.User;
import com.ecom.TrendBazaar.repository.UserRepository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository repository;

    @Autowired
    public PasswordEncoder passwordEncoder;

    @Override
    public User saveUser(User user) {
        user.setRole("ROLE_USER");
        String encoded = passwordEncoder.encode(user.getPassword());
        user.setPassword(encoded.trim());
        User saved = repository.save(user);
        return saved;
    }


    @Override
    public User findByEmail(String email) {
        return repository.findByEmail(email);
    }
}
