package com.ecom.TrendBazaar.security_config;

import com.ecom.TrendBazaar.model.User;
import com.ecom.TrendBazaar.repository.UserRepository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserRepository repository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = repository.findByEmail(username);
        if(user==null)
        {
            throw new UsernameNotFoundException("User not found");
        }
        return new CustomUser(user);
    }
}
