package com.auca.VotingApp2.controller;



import com.auca.VotingApp2.model.Product;
import com.auca.VotingApp2.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/products")
    public String listProducts(Model model) {
        List<Product> products = productService.getAllProducts();
        model.addAttribute("products", products);
        return "productList";
    }

    @GetMapping("/products/new")
    public String showCreateForm(Model model) {
        model.addAttribute("product", new Product());
        return "productForm";
    }

    @PostMapping("/products")
    public String saveProduct(@ModelAttribute("product") Product product) {
        productService.saveProduct(product);
        return "redirect:/products";
    }

    @GetMapping("/products/edit")
    public String showEditForm(@RequestParam("id") Long id, Model model) {
        Product product = productService.getProductById(id);
        if (product == null) {
            // Handle the case where the product does not exist
            // You can return an error page or redirect to the product list with a message
            return "redirect:/products"; // Redirecting back for simplicity
        }
        model.addAttribute("product", product);
        return "productForm";
    }

    @GetMapping("/products/delete")
    public String deleteProduct(@RequestParam("id") Long id) {
        productService.deleteProductById(id);
        return "redirect:/products";
    }
}
