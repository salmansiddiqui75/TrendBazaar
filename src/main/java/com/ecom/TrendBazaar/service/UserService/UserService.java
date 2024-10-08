package com.ecom.TrendBazaar.service.UserService;

import com.ecom.TrendBazaar.model.User;
import jakarta.mail.Multipart;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface UserService {
    public User saveUser(User user);

    public User findByEmail(String email);

    public List<User> getAllUser(String role);

    Boolean updateUserAccountStatus(int id, Boolean status);

    public void increaseFailedAttempts(User user);

    public void userAccountLock(User user);

    public Boolean unlockedAccountTimeExpire(User user);

    public void resetAttempts(int userId);

    void updateUserResetToken(String email, String resetToken);

    public User getUserByToken(String token);

    public User updateUserPassword(User user);
    public User updateUserProfile(User user, MultipartFile file) throws IOException;

    public User addAdmin(User user);

    public Boolean existsEmail(String email);

}
