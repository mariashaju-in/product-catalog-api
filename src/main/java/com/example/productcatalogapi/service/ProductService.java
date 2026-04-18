package com.example.productcatalogapi.service;

import com.example.productcatalogapi.dto.PagedResponse;
import com.example.productcatalogapi.dto.ProductRequest;
import com.example.productcatalogapi.dto.ProductResponse;

public interface ProductService {

    ProductResponse createProduct(ProductRequest request);

    PagedResponse<ProductResponse> getAllProducts(int page, int size, String sortBy, String direction);

    ProductResponse getProductById(Long id);

    ProductResponse updateProduct(Long id, ProductRequest request);

    void deleteProduct(Long id);
}
