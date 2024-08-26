package com.ecom.TrendBazaar.controllers;

import com.ecom.TrendBazaar.model.Category;
import com.ecom.TrendBazaar.model.Product;
import com.ecom.TrendBazaar.model.User;
import com.ecom.TrendBazaar.service.CartService.CartService;
import com.ecom.TrendBazaar.service.CategoryService;
import com.ecom.TrendBazaar.service.ProductService.ProductService;
import com.ecom.TrendBazaar.service.UserService.UserService;
import com.ecom.TrendBazaar.util.CommonUtil;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

@Controller
public class HomeController {
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private CommonUtil commonUtil;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;
    @Autowired
    private CartService cartService;

    @GetMapping("/")
    public String getIndex(Model model)
    {
        List<Category> categoryList = categoryService.getAllActiveCategory().stream().limit(6).toList();
        List<Product> productList = productService.getAllActiveProduct("").stream()
                .limit(8).toList();
        model.addAttribute("category",categoryList);
        model.addAttribute("product",productList);
        return "index";
    }

    @GetMapping("/signin")
    public String login() {
        return "login";
    }


    @ModelAttribute
    public void getLoginUserDetails(Principal principal, Model model) {
        if (principal != null) {
            String email = principal.getName();
            User userByEmail = userService.findByEmail(email);
            model.addAttribute("loginUser", userByEmail);
            int cartCount = cartService.getCartCount(userByEmail.getId());
            model.addAttribute("cartCount",cartCount);

        }
        List<Category> allActiveCategory = categoryService.getAllActiveCategory();
        model.addAttribute("category", allActiveCategory);
    }

    @GetMapping("/register")
    public String getRegister() {
        return "register";
    }

    @GetMapping("/product")
    public String getProduct(Model model, @RequestParam(value = "category", defaultValue = "") String category,
    @RequestParam(name="pageNo",defaultValue = "0")int pageNo,
     @RequestParam(name = "pageSize",defaultValue = "9")int pageSize,@RequestParam(defaultValue = "") String ch)
    {
        List<Category> allActiveCategory = categoryService.getAllActiveCategory();
        //List<Product> allActiveProduct = productService.getAllActiveProduct(category);
        //model.addAttribute("product", allActiveProduct);
        model.addAttribute("category", allActiveCategory);
        model.addAttribute("paramValue", category);
        Page<Product> page =null;
        if (StringUtils.isEmpty(ch))
        {
            page = productService.getAllActiveProductPagination(pageNo, pageSize, category);
        }else{
            page=productService.searchActiveProductPagination(pageNo,pageSize,category,ch);
        }
        List<Product> products = page.getContent();
        model.addAttribute("product", products);
        model.addAttribute("productSize", products.size());

        model.addAttribute("pageNo", page.getNumber());
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalElements", page.getTotalElements());
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("isFirst", page.isFirst());
        model.addAttribute("isLast", page.isLast());
        return "product";
    }

    @GetMapping("/view_product/{id}")
    public String viewProductDetails(@PathVariable int id, Model model) {
        Product productById = productService.getProductById(id);
        model.addAttribute("product", productById);
        return "view_product_details";
    }


    @PostMapping("/saveUser")
    public String saveUser(@ModelAttribute User user, @RequestParam("file") MultipartFile file, HttpSession session) throws IOException {
        String image = file.isEmpty() ? "default.jpg" : file.getOriginalFilename();
        user.setImage(image);
        User savedUser = userService.saveUser(user);
        if (!ObjectUtils.isEmpty(savedUser)) {
            if (!file.isEmpty()) {
                File saveFile = new ClassPathResource("static/img/img").getFile();
                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "profile_img" + File.separator + file.getOriginalFilename());
                System.out.println(path);
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            }
            session.setAttribute("successMsg", "User register successfully");
        } else {
            session.setAttribute("errorMsg", "Something went wrong");
        }
        return "redirect:/register";
    }

    @GetMapping("/forgetPassword")
    public String getForgetPassword() {
        return "forget_password.html";
    }

    @PostMapping("/forget_password")
    public String processForgetPassword(@RequestParam String email, HttpSession session, HttpServletRequest request) throws MessagingException, UnsupportedEncodingException {
        User user = userService.findByEmail(email);
        if (ObjectUtils.isEmpty(user)) {
            session.setAttribute("errorMsg", "Invalid email");
        } else {
            String resetToken = UUID.randomUUID().toString();
            userService.updateUserResetToken(email, resetToken);

            String url = CommonUtil.generateUrl(request) + "/reset_password?token=" + resetToken;

            Boolean mailSend = commonUtil.sendMail(url, email);
            if (mailSend) {
                session.setAttribute("successMsg", "Password Reset Link Sent!! Pls check your mail");
            } else {
                session.setAttribute("errorMsg", "Something went wrong");
            }
        }

        return "redirect:/forgetPassword";
    }

    @GetMapping("/reset_password")
    public String getResetPassword(@RequestParam String token, HttpSession session, Model model) {
        User userByToken = userService.getUserByToken(token);
        if (userByToken == null) {
            model.addAttribute("msg", "Your link is Invalid or Expired");
            return "message";
        }
        model.addAttribute("token", token);
        return "reset_password.html";
    }

    @PostMapping("/reset_password")
    public String resetPassword(@RequestParam String token, @RequestParam String password, HttpSession session, Model model) {
        User userByToken = userService.getUserByToken(token);
        if (userByToken == null) {
            model.addAttribute("msg", "Your link is Invalid or Expired");
            return "message";
        } else {
            userByToken.setPassword(passwordEncoder.encode(password));
            userByToken.setResetToken(null);
            userService.updateUserPassword(userByToken);
            model.addAttribute("msg", "Password change successfully");
            return "message";
        }

    }
@GetMapping("/search")
    public String searchProduct(@RequestParam String ch,Model model)
    {
        List<Product> searchProduct = productService.searchProduct(ch);
        List<Category> allActiveCategory = categoryService.getAllActiveCategory();
        model.addAttribute("category", allActiveCategory);
        model.addAttribute("product",searchProduct);
        return "/product";
    }


}
