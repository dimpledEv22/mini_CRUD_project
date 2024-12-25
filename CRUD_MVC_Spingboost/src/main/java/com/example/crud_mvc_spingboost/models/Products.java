package com.example.crud_mvc_spingboost.models;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Date;


@Entity
@Table (name = "products")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Products {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
         int id;

        String name;
         String brand;
         String category;
         Double price;


        LocalDate date;
         String description;
         String imageFilename;


}
