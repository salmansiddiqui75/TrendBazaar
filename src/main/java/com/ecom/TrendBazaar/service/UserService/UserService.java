package com.ecom.TrendBazaar.service.UserService;

import com.ecom.TrendBazaar.model.User;

import java.util.List;

public interface UserService
{
    public User saveUser(User user);
    public User findByEmail(String email);
    public List<User> getAllUser(String role);
    Boolean updateUserAccountStatus(int id, Boolean status);

    public void increaseFailedAttempts(User user);
    public void userAccountLock(User user);
    public Boolean unlockedAccountTimeExpire(User user);
    public void resetAttempts(int userId);

    void updateUserResetToken(String email, String resetToken);
}
