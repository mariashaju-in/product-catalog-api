package com.example.productcatalogapi.repository;

import com.example.productcatalogapi.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
