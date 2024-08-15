package com.ecom.TrendBazaar.controllers;

import com.ecom.TrendBazaar.model.Category;
import com.ecom.TrendBazaar.model.Product;
import com.ecom.TrendBazaar.service.CategoryService;
import com.ecom.TrendBazaar.service.ProductService.ProductService;
import jakarta.servlet.http.HttpSession;
import jakarta.websocket.Session;
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
@RequestMapping("/admin")
public class AdminController
{
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ProductService productService;
    @GetMapping("/index")
    public String getIndex()
    {
        return "admin/index";
    }

    @GetMapping("/getAddProduct")
    public String getAddProduct(Model model)
    {
        List<Category> categories=categoryService.getAllCategory();
        model.addAttribute("categories",categories);
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
    @GetMapping("/deleteCategory/{id}")
    public String deleteCategory(@PathVariable int id, HttpSession httpSession)
    {
        boolean categoty = categoryService.deleteCategoty(id);
        if(categoty)
        {
            httpSession.setAttribute("deleteMsg","Category delete successfully");
        }
        else {
            httpSession.setAttribute("errorMsg","Something went wrong try again !!");
        }
        return "redirect:/admin/getAddCategory";
    }
    @GetMapping("/loadEditCategory/{id}")
    public String loadEditCategory(@PathVariable int id,Model model)
    {
        Category byIdCategory = categoryService.getByIdCategory(id);
        model.addAttribute("category",byIdCategory);
        return "admin/edit_category";
    }

    @PostMapping("/updateCategory")
    public String updateCategory(@ModelAttribute Category category, @RequestParam("file")MultipartFile file,HttpSession session) throws IOException {
        Category oldCategory = categoryService.getByIdCategory(category.getId());
        String imageName= file.isEmpty() ?oldCategory.getImage() : file.getOriginalFilename()  ;

        if(!ObjectUtils.isEmpty(oldCategory))
        {
            oldCategory.setName(category.getName());
            oldCategory.setImage(imageName);
            oldCategory.setActive(category.isActive());
        }
        Category updateCategory = categoryService.saveCategory(oldCategory);
        if(!ObjectUtils.isEmpty(updateCategory))
        {
            if(!file.isEmpty())
            {
                File saveFile = new ClassPathResource("static/img/img").getFile();
                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "category_img" + File.separator + file.getOriginalFilename());
                System.out.println(path);
                Files.copy(file.getInputStream(),path, StandardCopyOption.REPLACE_EXISTING);
                session.setAttribute("successMsg","Category save successfully ");
            }
            session.setAttribute("successMsg","Category update successfully");
        }
        else {
            session.setAttribute("errorMsg","Something went wrong");
        }
        return "redirect:/admin/loadEditCategory/"+category.getId();
    }

    @PostMapping("/saveProduct")
    public String saveProduct(@ModelAttribute Product product,HttpSession httpSession,@RequestParam("file")MultipartFile file) throws IOException {
        String imageName = file.isEmpty() ? "default.jpg" : file.getOriginalFilename();
        product.setImage(imageName);
        Product saveProduct = productService.saveProduct(product);
        if(!ObjectUtils.isEmpty(saveProduct))
        {
            File saveFile = new ClassPathResource("static/img/img").getFile();
            Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "product_img" + File.separator + file.getOriginalFilename());
            System.out.println(path);
            Files.copy(file.getInputStream(),path, StandardCopyOption.REPLACE_EXISTING);
            httpSession.setAttribute("successMsg","Product save successfully");

            System.out.println(path);
        }
        else {
            httpSession.setAttribute("errorMsg","Something went wrong");
        }
        return "redirect:/admin/getAddProduct";

    }
}
