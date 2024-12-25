package com.example.crud_mvc_spingboost.services;

import com.example.crud_mvc_spingboost.models.Products;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductsRepository  extends JpaRepository<Products, Integer> {

}
