package com.ecom.TrendBazaar.controllers;

import com.ecom.TrendBazaar.model.Category;
import com.ecom.TrendBazaar.service.CategoryService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/admin")
public class AdminController
{
    @Autowired
    private CategoryService categoryService;
    @GetMapping("/index")
    public String getIndex()
    {
        return "admin/index";
    }

    @GetMapping("/getAddProduct")
    public String getAddProduct()
    {
        return "admin/add_product";
    }

    @GetMapping("/getAddCategory")
    public String getAddCategory()
    {
        return "admin/add_category";
    }

    @PostMapping("/saveCategory")
    public String saveCategory(@ModelAttribute Category category , @RequestParam("image") MultipartFile file, HttpSession httpSession)
    {
        String imageName = file != null ? file.getOriginalFilename():"default.jpg";
        category.setImage(imageName);
        if(categoryService.exitCategory(category.getName()))
        {
            httpSession.setAttribute("errorMsg","Category already exits");
        }
        else{
            Category saveCategory=categoryService.saveCategory(category);
            if (ObjectUtils.isEmpty(saveCategory))
            {
                httpSession.setAttribute("errorMsg","Not saved ! Facing some issue");
            }
            else{
                httpSession.setAttribute("successMsg","Category save successfully ");
            }
        }
        return "redirect:/getAddCategory";
    }
}
