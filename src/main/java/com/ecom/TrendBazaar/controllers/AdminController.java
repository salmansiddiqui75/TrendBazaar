package com.ecom.TrendBazaar.controllers;

import com.ecom.TrendBazaar.model.Category;
import com.ecom.TrendBazaar.model.Product;
import com.ecom.TrendBazaar.model.ProductOrder;
import com.ecom.TrendBazaar.model.User;
import com.ecom.TrendBazaar.service.CartService.CartService;
import com.ecom.TrendBazaar.service.CategoryService;
import com.ecom.TrendBazaar.service.OrderService.OrderService;
import com.ecom.TrendBazaar.service.ProductService.ProductService;
import com.ecom.TrendBazaar.service.UserService.UserService;
import com.ecom.TrendBazaar.util.CommonUtil;
import com.ecom.TrendBazaar.util.OrderStatus;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
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

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private UserService userService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ProductService productService;
    @Autowired
    private CartService cartService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/")
    public String getIndex() {
        return "admin/index";
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

    @GetMapping("/getAddProduct")
    public String getAddProduct(Model model) {
        List<Category> categories = categoryService.getAllCategory();
        model.addAttribute("categories", categories);
        return "admin/add_product";
    }

    @GetMapping("/getAddCategory")
    public String getAddCategory(Model model,@RequestParam(name="pageNo",defaultValue = "0")int pageNo,
                                 @RequestParam(name = "pageSize",defaultValue = "2")int pageSize)
    {
        Page<Category> page = categoryService.getAllCategoryPagination(pageNo, pageSize);

        //model.addAttribute("categories", categoryService.getAllCategory());

        List<Category> categories = page.getContent();
        model.addAttribute("categories", categories);
        model.addAttribute("pageNo", page.getNumber());
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalElements", page.getTotalElements());
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("isFirst", page.isFirst());
        model.addAttribute("isLast", page.isLast());
        return "admin/add_category";
    }

    @PostMapping("/saveCategory")
    public String saveCategory(@ModelAttribute Category category, @RequestParam("file") MultipartFile file, HttpSession httpSession) throws IOException {
        String image = file != null ? file.getOriginalFilename() : "default.jpg";
        category.setImage(image);
        if (categoryService.exitCategory(category.getName())) {
            httpSession.setAttribute("errorMsg", "Category already exits");
        } else {
            Category saveCategory = categoryService.saveCategory(category);
            if (ObjectUtils.isEmpty(saveCategory)) {
                httpSession.setAttribute("errorMsg", "Not saved ! Facing some issue");
            } else {
                File saveFile = new ClassPathResource("static/img/img").getFile();
                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "category_img" + File.separator + file.getOriginalFilename());
                System.out.println(path);
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                httpSession.setAttribute("successMsg", "Category save successfully ");
            }
        }
        return "redirect:/admin/getAddCategory";
    }

    @GetMapping("/deleteCategory/{id}")
    public String deleteCategory(@PathVariable int id, HttpSession httpSession) {
        boolean categoty = categoryService.deleteCategoty(id);
        if (categoty) {
            httpSession.setAttribute("deleteMsg", "Category delete successfully");
        } else {
            httpSession.setAttribute("errorMsg", "Something went wrong try again !!");
        }
        return "redirect:/admin/getAddCategory";
    }

    @GetMapping("/loadEditCategory/{id}")
    public String loadEditCategory(@PathVariable int id, Model model) {
        Category byIdCategory = categoryService.getByIdCategory(id);
        model.addAttribute("category", byIdCategory);
        return "admin/edit_category";
    }

    @PostMapping("/updateCategory")
    public String updateCategory(@ModelAttribute Category category, @RequestParam("file") MultipartFile file, HttpSession session) throws IOException {
        Category oldCategory = categoryService.getByIdCategory(category.getId());
        String imageName = file.isEmpty() ? oldCategory.getImage() : file.getOriginalFilename();

        if (!ObjectUtils.isEmpty(oldCategory)) {
            oldCategory.setName(category.getName());
            oldCategory.setImage(imageName);
            oldCategory.setIsActive(category.getIsActive());
        }
        Category updateCategory = categoryService.saveCategory(oldCategory);
        if (!ObjectUtils.isEmpty(updateCategory)) {
            if (!file.isEmpty()) {
                File saveFile = new ClassPathResource("static/img/img").getFile();
                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "category_img" + File.separator + file.getOriginalFilename());
                System.out.println(path);
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                session.setAttribute("successMsg", "Category save successfully ");
            }
            session.setAttribute("successMsg", "Category update successfully");
        } else {
            session.setAttribute("errorMsg", "Something went wrong");
        }
        return "redirect:/admin/loadEditCategory/" + category.getId();
    }

    @PostMapping("/saveProduct")
    public String saveProduct(@ModelAttribute Product product, HttpSession httpSession, @RequestParam("file") MultipartFile file) throws IOException {
        String imageName = file.isEmpty() ? "default.jpg" : file.getOriginalFilename();
        product.setImage(imageName);
        product.setDiscount(0);
        product.setDiscountPrice(product.getPrice());
        Product saveProduct = productService.saveProduct(product);
        if (!ObjectUtils.isEmpty(saveProduct)) {
            File saveFile = new ClassPathResource("static/img/img").getFile();
            Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "product_img" + File.separator + file.getOriginalFilename());
            System.out.println(path);
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            httpSession.setAttribute("successMsg", "Product save successfully");
        } else {
            httpSession.setAttribute("errorMsg", "Something went wrong");
        }
        return "redirect:/admin/getAddProduct";

    }

    @GetMapping("/viewProduct")
    public String viewProduct(@ModelAttribute Product product,@RequestParam(defaultValue = "")String ch, Model model,@RequestParam(name="pageNo",defaultValue = "0")int pageNo,
                              @RequestParam(name = "pageSize",defaultValue = "8")int pageSize)
    {
//        List<Product> allProduct = productService.getAllProduct();
//        model.addAttribute("allProduct", allProduct);

        Page<Product> page = productService.getAllActiveProductPagination(pageNo, pageSize, ch);

        List<Product> products = page.getContent();
        model.addAttribute("allProduct", products);
        model.addAttribute("pageNo", page.getNumber());
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalElements", page.getTotalElements());
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("isFirst", page.isFirst());
        model.addAttribute("isLast", page.isLast());

        return "admin/view_product";
    }

    @GetMapping("/deleteProduct/{id}")
    public String deleteProduct(@PathVariable int id, HttpSession session) {
        Boolean deletedProduct = productService.deleteProduct(id);
        if (deletedProduct) {
            session.setAttribute("successMsg", "Product deleted successfully");
        } else {
            session.setAttribute("errorMsg", "Something went wrong");
        }
        return "redirect:/admin/viewProduct";
    }

    @GetMapping("/loadEditProduct/{id}")
    public String loadEditProduct(@PathVariable int id, Model model) {
        Product productById = productService.getProductById(id);
        Category byIdCategory = categoryService.getByIdCategory(id);

        model.addAttribute("product", productById);
        model.addAttribute("category", byIdCategory);
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
        if (product.getDiscount() < 0 || product.getDiscount() > 100) {
            session.setAttribute("errorMsg", "Invalid discount !! It should be between 0 to 100 %");
        } else {

            if (!ObjectUtils.isEmpty(product)) {
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
            if (!ObjectUtils.isEmpty(savedProduct)) {
                if (!file.isEmpty()) {
                    File saveFile = new ClassPathResource("static/img/img").getFile();
                    Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "product_img" + File.separator + file.getOriginalFilename());
                    System.out.println(path);
                    Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                }

                session.setAttribute("successMsg", "Product update successfully");
            } else {
                session.setAttribute("errorMsg", "Something went wrong");
            }

        }

        // Update old product details

        // Redirect to the product edit page or another appropriate page
        return "redirect:/admin/loadEditProduct/" + product.getId();
    }

    @GetMapping("/users")
    public String getAllUser(Model model,@RequestParam int type) {
        List<User>users=null;
        if (type==1)
        {
            users = userService.getAllUser("ROLE_USER");
        }else{
            users=userService.getAllUser("ROLE_ADMIN");
        }
        model.addAttribute("userType",type);
        model.addAttribute("user", users);
        return "/admin/all_user";
    }

    @GetMapping("/updateStatus")
    public String updateUserAccountStatus(@RequestParam Boolean status, @RequestParam int id,
                                          @RequestParam int type,HttpSession session) {
        Boolean b = userService.updateUserAccountStatus(id, status);

        if (b) {
            session.setAttribute("successMsg", "Account status updated");
        } else {
            session.setAttribute("errorMsg", "Something went wrong");
        }

        return "redirect:/admin/users?type="+type;
    }

    @GetMapping("/orders")
    public String getAllOrder(Model model,@RequestParam(name="pageNo",defaultValue = "0")int pageNo,
                              @RequestParam(name = "pageSize",defaultValue = "4")int pageSize)
    {
//        List<ProductOrder> allOrders = orderService.getAllOrders();
//        model.addAttribute("orders", allOrders);

        Page<ProductOrder> page = orderService.getAllOrdersPagination(pageNo, pageSize);
        model.addAttribute("srch", false);

        List<ProductOrder> orders= page.getContent();
        model.addAttribute("orders", orders);
        model.addAttribute("orderSize", orders.size());
        model.addAttribute("pageNo", page.getNumber());
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalElements", page.getTotalElements());
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("isFirst", page.isFirst());
        model.addAttribute("isLast", page.isLast());

        return "/admin/order";
    }

    @PostMapping("/update-order-status")
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
            session.setAttribute("successMsg","Status update successfully");
        }
        else{
            session.setAttribute("errorMsg","Something went wrong");
        }
        return "redirect:/admin/orders";
    }


    @GetMapping("/search-order")
    public String searchOrder(@RequestParam String orderId, Model m, HttpSession session,@RequestParam(name="pageNo",defaultValue = "0")int pageNo,
                              @RequestParam(name = "pageSize",defaultValue = "9")int pageSize) {

        if (orderId != null && orderId.length() > 0) {

            ProductOrder order = orderService.findOrderByOrderId(orderId.trim());

            if (ObjectUtils.isEmpty(order)) {
                session.setAttribute("errorMsg", "Incorrect orderId");
                m.addAttribute("order", null);
            } else {
                m.addAttribute("order", order);
            }

            m.addAttribute("srch", true);
        } else {
//			List<ProductOrder> allOrders = orderService.getAllOrders();
//			m.addAttribute("orders", allOrders);
//			m.addAttribute("srch", false);

            Page<ProductOrder>page = orderService.getAllOrdersPagination(pageNo,pageSize);
            m.addAttribute("orders", page);
            m.addAttribute("srch", false);

            m.addAttribute("pageNo", page.getNumber());
            m.addAttribute("pageSize", pageSize);
            m.addAttribute("totalElements", page.getTotalElements());
            m.addAttribute("totalPages", page.getTotalPages());
            m.addAttribute("isFirst", page.isFirst());
            m.addAttribute("isLast", page.isLast());
        }
        return "/admin/order";
    }

    @GetMapping("/add-admin")
    public String loadAddAdmin()
    {
        return "/admin/add_admin";
    }

    @PostMapping("/save-admin")
    public String addAdmin(@ModelAttribute User user, @RequestParam("file") MultipartFile file, HttpSession session) throws IOException {
        String image = file.isEmpty() ? "default.jpg" : file.getOriginalFilename();
        user.setImage(image);
        User addAdmin = userService.addAdmin(user);
        if (!ObjectUtils.isEmpty(addAdmin)) {
            if (!file.isEmpty()) {
                File saveFile = new ClassPathResource("static/img/img").getFile();
                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "profile_img" + File.separator + file.getOriginalFilename());
                System.out.println(path);
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            }
            session.setAttribute("successMsg", "New admin added successfully");
        } else {
            session.setAttribute("errorMsg", "Something went wrong");
        }
        return "redirect:/admin/add-admin";
    }
    @GetMapping("/profile")
    public String adminProfile()
    {
        return "/admin/profile";
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

        return "redirect:/admin/profile";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam String currentPassword,@RequestParam String newPassword,Principal principal,
                                 HttpSession session)
    {
        User loggedInUserDetails =commonUtil.getLoggedInUserDetails(principal);

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

        return "redirect:/admin/profile";
    }

}
