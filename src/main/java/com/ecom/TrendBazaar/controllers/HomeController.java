package com.ecom.TrendBazaar.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController
{
    @GetMapping("/index")
    public String getIndex()
    {
        return "index";
    }

    @GetMapping("/login")
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
    public String getProduct()
    {
        return "product";
    }
    @GetMapping("/view_product")
    public String viewProductDetails()
    {
        return "view_product_details";
    }


}
