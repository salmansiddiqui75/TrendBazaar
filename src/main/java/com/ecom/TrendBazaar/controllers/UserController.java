package com.ecom.TrendBazaar.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class UserController
{
    @RequestMapping("/home")
    public String home()
    {
        return "user/home";
    }
}
