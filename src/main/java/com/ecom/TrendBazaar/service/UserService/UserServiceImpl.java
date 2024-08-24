package com.ecom.TrendBazaar.service.UserService;

import com.ecom.TrendBazaar.model.User;
import com.ecom.TrendBazaar.repository.UserRepository.UserRepository;
import com.ecom.TrendBazaar.util.AppConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository repository;

    @Autowired
    public PasswordEncoder passwordEncoder;

    @Override
    public User saveUser(User user) {
        user.setAccountNonLocked(true);
        user.setFailedAttempts(0);
        user.setIsEnable(true);
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

    @Override
    public List<User> getAllUser(String role) {
        return repository.findByRole(role);
    }

    @Override
    public Boolean updateUserAccountStatus(int id, Boolean status) {
        Optional<User> byId = repository.findById(id);
        if (byId.isPresent()) {
            User user = byId.get();
            user.setIsEnable(status);
            repository.save(user);
            return true;
        }
        return false;
    }

    @Override
    public void increaseFailedAttempts(User user) {
        int failedAttempts = user.getFailedAttempts() + 1;
        user.setFailedAttempts(failedAttempts);
        repository.save(user);
    }

    @Override
    public void userAccountLock(User user) {
        user.setAccountNonLocked(false);
        user.setLockTime(new Date());
        repository.save(user);

    }

    @Override
    public Boolean unlockedAccountTimeExpire(User user) {
        long time = user.getLockTime().getTime();
        long unlockTime = time + AppConstant.UNLOCK_DURATION_TIME;
        long currentTime = System.currentTimeMillis();
        if (unlockTime < currentTime) {
            user.setAccountNonLocked(true);
            user.setFailedAttempts(0);
            user.setLockTime(null);
            repository.save(user);
            return true;
        }
        return false;
    }

    @Override
    public void resetAttempts(int userId) {

    }

    @Override
    public void updateUserResetToken(String email, String resetToken) {
        User byEmail = repository.findByEmail(email);
        byEmail.setResetToken(resetToken);
        repository.save(byEmail);
    }

    @Override
    public User getUserByToken(String token) {
        return repository.findByResetToken(token);
    }

    @Override
    public User updateUserPassword(User user) {
        return repository.save(user);
    }

    @Override
    public User updateUserProfile(User user) {
        return null;
    }
}
