package com.example.crud_mvc_spingboost.Controller;


import com.example.crud_mvc_spingboost.models.Products;
import com.example.crud_mvc_spingboost.models.Products_DTO;
import com.example.crud_mvc_spingboost.services.ProductsRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductsControlller {

    @Autowired
    private ProductsRepository proRepo;


    @GetMapping()
    public String showProducts(Model model) {
        List<Products> products = proRepo.findAll(Sort.by(Sort.Direction.DESC, "id"));
        model.addAttribute("products", products);
        return "products/index";
    }

    @GetMapping("/create")
    public String showCreatePage(Model model) {
        Products_DTO productsDto = new Products_DTO();
        model.addAttribute("productDto", productsDto);
        return "products/Create_products";
    }

    @PostMapping("/create")
    public String createProduct(@Valid @ModelAttribute("productDto") Products_DTO productsDto, BindingResult result, Model model) throws IOException {
        if (productsDto.getImageFile().isEmpty()) {
            result.addError(new FieldError("productDto", "imageFile", "Image file is required"));
        }
        if (result.hasErrors()) {
            return "products/Create_products";

        }

        MultipartFile imageFile = productsDto.getImageFile();
        String storageFileName =  imageFile.getOriginalFilename();

        try {
            String uploadDir = "/public/images";
            Path uploadPath = Paths.get(uploadDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            try (InputStream input = imageFile.getInputStream()) {
                Files.copy(input, Paths.get(uploadDir + storageFileName),
                        StandardCopyOption.REPLACE_EXISTING);

            }
        }catch (Exception e) {
            System.out.println("Exception" + e.getMessage());
        }

        Products pRoducts = new Products();
        pRoducts.setName(productsDto.getName());
        pRoducts.setBrand(productsDto.getBrand());
        pRoducts.setCategory(productsDto.getCategory());
        pRoducts.setPrice(productsDto.getPrice());
        pRoducts.setDate(LocalDate.now());
        pRoducts.setDescription(productsDto.getDescription());
        pRoducts.setImageFilename(storageFileName);

        proRepo.save(pRoducts);

        return "redirect:/products";
    }

    @GetMapping("/edit")
    public String showEditPage(Model model,
        @RequestParam int id )
    {
        try {
            Products products_editG = proRepo.findById(id).get();
            model.addAttribute("product",products_editG);

            Products_DTO  productsDto = new Products_DTO();
            productsDto.setName(products_editG.getName());
            productsDto.setBrand(products_editG.getBrand());
            productsDto.setCategory(products_editG.getCategory());
            productsDto.setPrice(products_editG.getPrice());
            productsDto.setDescription(products_editG.getDescription());

            model.addAttribute("productDto", productsDto);

        }catch (Exception e) {
            System.out.println("Exception" + e.getMessage());
            return "redirect:/products";
        }
        return "products/Edit_product";
    }
        @PostMapping("/edit")
        public String updatePro(Model model,
                                @RequestParam int id ,
                                @Valid @ModelAttribute Products_DTO productsDto,
                                BindingResult result, InputStream inputStream)
        {
            try {
                Products products_editP = proRepo.findById(id).get();
                model.addAttribute("product",products_editP);

                if (result.hasErrors()) {
                    return "products/Edit_product";
                }
                if (!productsDto.getImageFile().isEmpty()) {
                    //Delete old image
                    String uploadDir = "/public/images";
                    Path oldImagePath = Paths.get(uploadDir + products_editP.getImageFilename());

                    try {
                        Files.delete(oldImagePath);

                    }
                    catch (Exception e) {
                        System.out.println("Exception" + e.getMessage());
                    }
                    //save new Image
                    MultipartFile imageFile = productsDto.getImageFile();
                    String storageFileName =  imageFile.getOriginalFilename();

                    try(InputStream input = imageFile.getInputStream()) {
                        Files.copy(inputStream,Paths.get(uploadDir + storageFileName),
                                StandardCopyOption.REPLACE_EXISTING);
                    }
                    products_editP.setImageFilename(storageFileName);

                }
                products_editP.setName(productsDto.getName());
                products_editP.setBrand(productsDto.getBrand());
                products_editP.setCategory(productsDto.getCategory());
                products_editP.setPrice(productsDto.getPrice());
                products_editP.setDescription(productsDto.getDescription());
                LocalDate localDate = LocalDate.now();
                products_editP.setDate(localDate);

                proRepo.save(products_editP);
            }
            catch (Exception e) {
                System.out.println("Exception" + e.getMessage());
            }
            return "redirect:/products";
        }
        @GetMapping("/delete")
        public String deleteProduct(
                @RequestParam int id)
        {
            try{
                Products products_deleteG = proRepo.findById(id).get();

                //delete product image
                Path imagePath = Paths.get("public/images"+products_deleteG.getImageFilename());
                try {
                    Files.delete(imagePath);
                }
                catch (Exception e) {
                    System.out.println("Exception" + e.getMessage());
                }

                //delete the product
                proRepo.delete(products_deleteG);
            }
            catch (Exception e) {
                System.out.println("Exception" + e.getMessage());
            }
            return "redirect:/products";
        }
}