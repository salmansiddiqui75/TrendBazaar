package com.ecom.TrendBazaar.controllers;

import com.ecom.TrendBazaar.model.Category;
import com.ecom.TrendBazaar.model.Product;
import com.ecom.TrendBazaar.model.User;
import com.ecom.TrendBazaar.service.CategoryService;
import com.ecom.TrendBazaar.service.ProductService.ProductService;
import com.ecom.TrendBazaar.service.UserService.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Controller
public class HomeController
{
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;
    @GetMapping("/index")
    public String getIndex()
    {
        return "index";
    }

    @GetMapping("/signin")
    public String getLogin()
    {
        return "login";
    }
    @GetMapping("/register")
    public String getRegister()
    {
        return "register";
    }

    @GetMapping("/product")
    public String getProduct(Model model, @RequestParam(value = "category",defaultValue = "") String category)
    {
        System.out.println("cat=" +" ="+category);
        List<Category> allActiveCategory = categoryService.getAllActiveCategory();
        List<Product> allActiveProduct = productService.getAllActiveProduct(category);
        model.addAttribute("product",allActiveProduct);
        model.addAttribute("category",allActiveCategory);
        model.addAttribute("paramValue",category);
        return "product";
    }
    @GetMapping("/view_product/{id}")
    public String viewProductDetails(@PathVariable int id,Model model)
    {
        Product productById = productService.getProductById(id);
        model.addAttribute("product",productById);
        return "view_product_details";
    }



    @PostMapping("/saveUser")
    public String saveUser(@ModelAttribute User user, @RequestParam("file")MultipartFile file, HttpSession session) throws IOException {
        String image = file.isEmpty() ? "default.jpg" : file.getOriginalFilename();
        user.setImage(image);
        User savedUser = userService.saveUser(user);
        if(!ObjectUtils.isEmpty(savedUser))
        {
            if(!file.isEmpty())
            {
                File saveFile = new ClassPathResource("static/img/img").getFile();
                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "profile_img" + File.separator + file.getOriginalFilename());
                System.out.println(path);
                Files.copy(file.getInputStream(),path, StandardCopyOption.REPLACE_EXISTING);
            }
            session.setAttribute("successMsg","User register successfully");
        }
        else {
            session.setAttribute("errorMsg","Something went wrong");
        }
        return "redirect:/register";
    }


}
