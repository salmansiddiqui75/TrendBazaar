package com.ecom.TrendBazaar.service.UserService;

import com.ecom.TrendBazaar.model.User;
import com.ecom.TrendBazaar.repository.UserRepository.UserRepository;
import com.ecom.TrendBazaar.service.AwsService.FileService;
import com.ecom.TrendBazaar.util.AppConstant;
import com.ecom.TrendBazaar.util.BucketType;
import com.ecom.TrendBazaar.util.CommonUtil;
import jakarta.mail.Multipart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository repository;

    @Autowired
    public PasswordEncoder passwordEncoder;
    @Autowired
    @Lazy
    private CommonUtil commonUtil;

    @Autowired
    private FileService fileService;

    @Override
    public User saveUser(User user) {
        user.setAccountNonLocked(true);
        user.setFailedAttempts(0);
        user.setIsEnable(true);
        user.setRole("ROLE_USER");
        String encoded = passwordEncoder.encode(user.getPassword());
        user.setPassword(encoded.trim());
        String confirmPassword = passwordEncoder.encode( user.getConfirmPassword());
        user.setConfirmPassword(confirmPassword.trim());
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
    public User updateUserProfile(User user,MultipartFile file) throws IOException {
        User dbUser = repository.findById(user.getId()).get();
        //String imageName = file.isEmpty() ? dbUser.getImage() : file.getOriginalFilename();
        String imageUrl = commonUtil.getImageUrl(file, BucketType.PROFILE.getId());
        if(!ObjectUtils.isEmpty(dbUser))
        {
            dbUser.setName(user.getName());
            dbUser.setMobileNumber(user.getMobileNumber());
            dbUser.setAddress(user.getAddress());
            dbUser.setCity(user.getCity());
            dbUser.setState(user.getState());
            dbUser.setPincode(user.getPincode());
            dbUser.setImage(imageUrl);
            dbUser = repository.save(dbUser);
        }
        if (!file.isEmpty()) {
//            File saveFile = new ClassPathResource("static/img/img").getFile();
//            Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "profile_img" + File.separator + file.getOriginalFilename());
//            System.out.println(path);
//            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            fileService.uploadFileS3(file,BucketType.PROFILE.getId());

        }
        return dbUser;
    }

    @Override
    public User addAdmin(User user) {
        user.setAccountNonLocked(true);
        user.setFailedAttempts(0);
        user.setIsEnable(true);
        user.setRole("ROLE_ADMIN");
        String encoded = passwordEncoder.encode(user.getPassword());
        user.setPassword(encoded.trim());
        User saved = repository.save(user);
        return saved;
    }

    @Override
    public Boolean existsEmail(String email) {
        return repository.existsByEmail(email);
    }
}
