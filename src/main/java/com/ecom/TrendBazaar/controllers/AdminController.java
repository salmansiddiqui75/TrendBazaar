package com.ecom.TrendBazaar.controllers;

import com.ecom.TrendBazaar.model.Category;
import com.ecom.TrendBazaar.model.Product;
import com.ecom.TrendBazaar.service.CategoryService;
import com.ecom.TrendBazaar.service.ProductService.ProductService;
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
        String imageName= file.isEmpty() ? oldCategory.getImage() : file.getOriginalFilename()  ;

        if(!ObjectUtils.isEmpty(oldCategory))
        {
            oldCategory.setName(category.getName());
            oldCategory.setImage(imageName);
            oldCategory.setIsActive(category.getIsActive());
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
        product.setDiscount(0);
        product.setDiscountPrice(product.getPrice());
        Product saveProduct = productService.saveProduct(product);
        if(!ObjectUtils.isEmpty(saveProduct))
        {
            File saveFile = new ClassPathResource("static/img/img").getFile();
            Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "product_img" + File.separator + file.getOriginalFilename());
            System.out.println(path);
            Files.copy(file.getInputStream(),path, StandardCopyOption.REPLACE_EXISTING);
            httpSession.setAttribute("successMsg","Product save successfully");
        }
        else {
            httpSession.setAttribute("errorMsg","Something went wrong");
        }
        return "redirect:/admin/getAddProduct";

    }

    @GetMapping("/viewProduct")
    public String viewProduct(@ModelAttribute Product product, Model model)
    {
        List<Product> allProduct = productService.getAllProduct();
        model.addAttribute("allProduct",allProduct);
        return "admin/view_product";
    }

    @GetMapping("/deleteProduct/{id}")
    public String deleteProduct(@PathVariable int id,HttpSession session)
    {
        Boolean deletedProduct=productService.deleteProduct(id);
        if(deletedProduct)
        {
            session.setAttribute("successMsg","Product deleted successfully");
        }
        else{
            session.setAttribute("errorMsg","Something went wrong");
        }
        return "redirect:/admin/viewProduct";
    }

    @GetMapping("/loadEditProduct/{id}")
    public String loadEditProduct(@PathVariable int id,Model model)
    {
        Product productById = productService.getProductById(id);
        Category byIdCategory = categoryService.getByIdCategory(id);

        model.addAttribute("product",productById);
        model.addAttribute("category",byIdCategory);
        return "admin/edit_product";
    }
    @PostMapping("/updateProduct")
    public String updateProduct(@ModelAttribute Product product, Model model, @RequestParam("file") MultipartFile file, HttpSession session) throws IOException {
        // Retrieve the existing product
        Product oldProduct = productService.getProductById(product.getId());

        // Check if oldProduct is null
        if (oldProduct == null) {
            session.setAttribute("errorMsg", "Product not found with ID: " + product.getId());
            return "redirect:/admin/loadEditProduct/" + product.getId();  // Redirect to edit page or an appropriate error page
        }

        // Determine the image name
        String imageName = file.isEmpty() ? oldProduct.getImage() : file.getOriginalFilename();
        if(product.getDiscount()<0 || product.getDiscount()>100)
        {
            session.setAttribute("errorMsg","Invalid discount !! It should be between 0 to 100 %");
        }
        else{

            if(!ObjectUtils.isEmpty(product))
            {
                oldProduct.setTitle(product.getTitle());
                oldProduct.setCategory(product.getCategory());
                oldProduct.setDescription(product.getDescription());
                oldProduct.setPrice(product.getPrice());
                oldProduct.setStock(product.getStock());
                oldProduct.setImage(imageName);
                oldProduct.setDiscount(product.getDiscount());
                oldProduct.setIsActive(product.getIsActive());

                double discount = product.getPrice() * (product.getDiscount() / 100.0);
                double discountPrice = product.getPrice() - discount;
                oldProduct.setDiscountPrice(discountPrice);
            }


            // Save the updated product
            Product savedProduct = productService.saveProduct(oldProduct);

            // If the product is saved successfully
            if(!ObjectUtils.isEmpty(savedProduct))
            {
                if(!file.isEmpty())
                {
                    File saveFile = new ClassPathResource("static/img/img").getFile();
                    Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "product_img" + File.separator + file.getOriginalFilename());
                    System.out.println(path);
                    Files.copy(file.getInputStream(),path, StandardCopyOption.REPLACE_EXISTING);
                }

                session.setAttribute("successMsg","Product update successfully");
            }
            else {
                session.setAttribute("errorMsg","Something went wrong");
            }

        }

        // Update old product details

        // Redirect to the product edit page or another appropriate page
        return "redirect:/admin/loadEditProduct/" + product.getId();
    }

}
