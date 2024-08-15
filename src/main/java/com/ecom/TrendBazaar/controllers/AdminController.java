package com.ecom.TrendBazaar.controllers;

import com.ecom.TrendBazaar.model.Category;
import com.ecom.TrendBazaar.service.CategoryService;
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
    public String getAddCategory(Model model)
    {
        model.addAttribute("categories",categoryService.getAllCategory());
        return "admin/add_category";
    }

    @PostMapping("/saveCategory")
    public String saveCategory(@ModelAttribute Category category , @RequestParam("file") MultipartFile file, HttpSession httpSession) throws IOException {
        String image = file != null ? file.getOriginalFilename() : "default.jpg";
        category.setImage(image);
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
                File saveFile = new ClassPathResource("static/img/img").getFile();
                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "category_img" + File.separator + file.getOriginalFilename());
                System.out.println(path);
                Files.copy(file.getInputStream(),path, StandardCopyOption.REPLACE_EXISTING);
                httpSession.setAttribute("successMsg","Category save successfully ");
            }
        }
        return "redirect:/admin/getAddCategory";
    }
}
