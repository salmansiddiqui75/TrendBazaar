package com.ecom.TrendBazaar.security_config;

import com.ecom.TrendBazaar.model.User;
import com.ecom.TrendBazaar.repository.UserRepository.UserRepository;
import com.ecom.TrendBazaar.service.UserService.UserService;
import com.ecom.TrendBazaar.util.AppConstant;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AuthFailureHandlerImpl extends SimpleUrlAuthenticationFailureHandler {
    @Autowired
    private UserRepository repository;
    @Autowired
    private UserService userService;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        String email = request.getParameter("username");
        User user = repository.findByEmail(email);
        if (user != null) {
            if (user.getIsEnable()) {
                if (user.getAccountNonLocked()) {
                    if (user.getFailedAttempts() <= AppConstant.ATTEMPT_TIME) {
                        userService.increaseFailedAttempts(user);
                    } else {
                        userService.userAccountLock(user);
                        exception = new LockedException("Your account is Locked !! Due to 3 failed attempts");
                    }
                } else {
                    if (userService.unlockedAccountTimeExpire(user)) {
                        exception = new LockedException("Your account is unlock !! Pls try to login");
                    } else {
                        exception = new LockedException("Your Account is Locked !! Pls try after some time");
                    }
                    exception = new LockedException("Your Account is Locked");
                }
            } else {
                exception = new LockedException("Your account is InActive");
            }
        } else {
            exception = new LockedException("Invalid email or password. Please try again.");
        }
        super.setDefaultFailureUrl("/signin?error");
        super.onAuthenticationFailure(request, response, exception);
    }
}
