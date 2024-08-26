package com.ecom.TrendBazaar.controllers;

import com.ecom.TrendBazaar.model.*;
import com.ecom.TrendBazaar.service.CartService.CartService;
import com.ecom.TrendBazaar.service.CategoryService;
import com.ecom.TrendBazaar.service.OrderService.OrderService;
import com.ecom.TrendBazaar.service.UserService.UserService;
import com.ecom.TrendBazaar.util.CommonUtil;
import com.ecom.TrendBazaar.util.OrderStatus;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CartService cartService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private CommonUtil commonUtil;

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
    public String getOrderPage(Principal principal,Model model)
    {
        User userDetails=getLoggedInUserDetails(principal);
        List<Cart> cartByUserId = cartService.getCartByUserId(userDetails.getId());
        model.addAttribute("carts",cartByUserId);
        if(cartByUserId.size()>0) {
            Double orderPrice = cartByUserId.get(cartByUserId.size() - 1).getTotalOrderPrice();
            Double TotalOrderPrice = cartByUserId.get(cartByUserId.size() - 1).getTotalOrderPrice()+200+100;

            model.addAttribute("orderPrice", orderPrice);
            model.addAttribute("TotalOrderPrice", TotalOrderPrice);

        }
        return "/user/order";
    }

    @PostMapping("save-order")
    public String saveOrder(@ModelAttribute OrderRequest orderRequest,Principal principal) throws MessagingException, UnsupportedEncodingException {
        User userDetails = getLoggedInUserDetails(principal);
        orderService.saveOrder(userDetails.getId(),orderRequest);
        return "redirect:/user/success";
    }

    @GetMapping("/success")
    public String loadSuccess()
    {
        return "/user/success";
    }
    @GetMapping("/user-orders")
    public String myOrder(Principal principal,Model model)
    {
        User userDetails = getLoggedInUserDetails(principal);
        List<ProductOrder> orderByUserId = orderService.getOrderByUserId(userDetails.getId());
        model.addAttribute("orders",orderByUserId);
        return "/user/my_order";
    }

    @GetMapping("/update-status")
    public String updateOrderStatus(@RequestParam int id , @RequestParam int st,HttpSession session) throws MessagingException, UnsupportedEncodingException {
        OrderStatus[] values = OrderStatus.values();
        String status=null;
        for(OrderStatus orderStatus : values)
        {
            if(orderStatus.getId()==st)
            {
                status=orderStatus.getName();
            }
        }
        ProductOrder productOrder = orderService.updateOrderStatus(id, status);
        commonUtil.sendMailForProductOrder(productOrder,status);

        if(!ObjectUtils.isEmpty(productOrder))
        {
            session.setAttribute("successMsg","Status update sucessfully");
        }
        else{
            session.setAttribute("errorMsg","Something went wrong");
        }
        return "redirect:/user/user-orders";
    }

    @GetMapping("/profile")
    public String profile()
    {
        return "/user/profile";
    }

    @PostMapping("/update-profile")
    public String updateProfile(@ModelAttribute User user, @RequestParam("file") MultipartFile file,HttpSession httpSession) throws IOException {
        User updateUserProfile = userService.updateUserProfile(user, file);
        if (ObjectUtils.isEmpty(updateUserProfile))
        {
            httpSession.setAttribute("errorMsg","Something went wrong!! Profile not updated");
        }
        else{
            httpSession.setAttribute("successMsg","Profile updated successfully");
        }

        return "redirect:/user/profile";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam String currentPassword,@RequestParam String newPassword,Principal principal,
                                 HttpSession session)
    {
        User loggedInUserDetails = getLoggedInUserDetails(principal);
        boolean matches = passwordEncoder.matches(currentPassword, loggedInUserDetails.getPassword());

        if(matches)
        {
            String encoded = passwordEncoder.encode(newPassword);
            loggedInUserDetails.setPassword(encoded);
            User user = userService.updateUserPassword(loggedInUserDetails);
            if (ObjectUtils.isEmpty(user))
            {
                session.setAttribute("errorMsg","Something went wrong");
            }
            else {
                session.setAttribute("successMsg","Password update successfully");
            }
        }
        else {
            session.setAttribute("errorMsg","Current password is incorrect!!");
        }

        return "redirect:/user/profile";
    }
}
