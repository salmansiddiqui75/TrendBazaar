package com.ecom.TrendBazaar.controllers;

import com.ecom.TrendBazaar.model.Category;
import com.ecom.TrendBazaar.model.User;
import com.ecom.TrendBazaar.service.CategoryService;
import com.ecom.TrendBazaar.service.UserService.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController
{
    @Autowired
    private UserService userService;

    @Autowired
    private CategoryService categoryService;
    @RequestMapping("/")
    public String home()
    {
        return "user/home";
    }

    @ModelAttribute
    public void getLoginUserDetails(Principal principal, Model model)
    {
        if(principal!=null)
        {
            String email = principal.getName();
            User userByEmail = userService.findByEmail(email);
            model.addAttribute("loginUser",userByEmail);
        }
        List<Category> allActiveCategory = categoryService.getAllActiveCategory();
        model.addAttribute("category",allActiveCategory);
    }
}
