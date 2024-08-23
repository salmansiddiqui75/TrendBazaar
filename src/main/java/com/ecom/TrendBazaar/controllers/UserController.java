package com.ecom.TrendBazaar.controllers;

import com.ecom.TrendBazaar.model.Cart;
import com.ecom.TrendBazaar.model.Category;
import com.ecom.TrendBazaar.model.OrderRequest;
import com.ecom.TrendBazaar.model.User;
import com.ecom.TrendBazaar.service.CartService.CartService;
import com.ecom.TrendBazaar.service.CategoryService;
import com.ecom.TrendBazaar.service.OrderService.OrderService;
import com.ecom.TrendBazaar.service.UserService.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CartService cartService;
    @Autowired
    private OrderService orderService;

    @RequestMapping("/")
    public String home() {
        return "user/home";
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

    @GetMapping("/addCart")
    public String addToCart(@RequestParam int pid, @RequestParam int uid, HttpSession session) {
        Cart savedCart = cartService.saveCart(pid, uid);
        if (!ObjectUtils.isEmpty(savedCart)) {
            session.setAttribute("successMsg", "Product added to cart successfully");
        } else {
            session.setAttribute("errorMsg", "Something went wrong");
        }
        return "redirect:/view_product/" + pid;
    }
    @GetMapping("/cart")
    public String getCartPage(Principal principal,Model model)
    {
        User userDetails=getLoggedInUserDetails(principal);
        List<Cart> cartByUserId = cartService.getCartByUserId(userDetails.getId());
        model.addAttribute("carts",cartByUserId);
        if(cartByUserId.size()>0) {
            Double totalOrderPrice = cartByUserId.get(cartByUserId.size() - 1).getTotalOrderPrice();
            model.addAttribute("totalOrderPrice", totalOrderPrice);
        }
        return "user/cart";
    }

    @GetMapping("/cartQuantityUpdate")
    public String cartQuantityUpdate(@RequestParam String sy, @RequestParam int cid)
    {
       cartService.updateQuantity(sy,cid);
        return "redirect:/user/cart";
    }
    private User getLoggedInUserDetails(Principal principal)
    {
        String name = principal.getName();
        User user = userService.findByEmail(name);
        return user;
    }

    @GetMapping("order")
    public String getOrderPage()
    {
        return "/user/order";
    }

    @PostMapping("save-order")
    public String saveOrder(@ModelAttribute OrderRequest orderRequest,Principal principal)
    {
        User userDetails = getLoggedInUserDetails(principal);
        orderService.saveOrder(userDetails.getId(),orderRequest);
        return "/user/success";
    }
}
