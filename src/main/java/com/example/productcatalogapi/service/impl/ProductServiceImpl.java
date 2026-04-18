package com.example.productcatalogapi.service.impl;

import com.example.productcatalogapi.dto.PagedResponse;
import com.example.productcatalogapi.dto.ProductRequest;
import com.example.productcatalogapi.dto.ProductResponse;
import com.example.productcatalogapi.entity.Product;
import com.example.productcatalogapi.exception.ResourceNotFoundException;
import com.example.productcatalogapi.repository.ProductRepository;
import com.example.productcatalogapi.service.ProductService;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "id",
            "name",
            "description",
            "category",
            "price",
            "stockQuantity"
    );

    private final ProductRepository productRepository;

    @Override
    public ProductResponse createProduct(ProductRequest request) {
        Product product = Product.builder()
                .name(request.name())
                .description(request.description())
                .category(request.category())
                .price(request.price())
                .stockQuantity(request.stockQuantity())
                .build();

        Product savedProduct = productRepository.save(product);
        return mapToResponse(savedProduct);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ProductResponse> getAllProducts(int page, int size, String sortBy, String direction) {
        validateSortField(sortBy);
        validateSortDirection(direction);

        Sort sort = direction.equalsIgnoreCase(Sort.Direction.DESC.name())
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        PageRequest pageRequest = PageRequest.of(page, size, sort);
        Page<Product> productPage = productRepository.findAll(pageRequest);
        List<ProductResponse> productResponses = productPage.getContent()
                .stream()
                .map(this::mapToResponse)
                .toList();

        return new PagedResponse<>(
                productResponses,
                productPage.getNumber(),
                productPage.getSize(),
                productPage.getTotalElements(),
                productPage.getTotalPages(),
                productPage.isLast()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        return mapToResponse(getProductEntityById(id));
    }

    @Override
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product existingProduct = getProductEntityById(id);
        existingProduct.setName(request.name());
        existingProduct.setDescription(request.description());
        existingProduct.setCategory(request.category());
        existingProduct.setPrice(request.price());
        existingProduct.setStockQuantity(request.stockQuantity());

        Product updatedProduct = productRepository.save(existingProduct);
        return mapToResponse(updatedProduct);
    }

    @Override
    public void deleteProduct(Long id) {
        Product existingProduct = getProductEntityById(id);
        productRepository.delete(existingProduct);
    }

    private Product getProductEntityById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    private ProductResponse mapToResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getCategory(),
                product.getPrice(),
                product.getStockQuantity()
        );
    }

    private void validateSortField(String sortBy) {
        if (!ALLOWED_SORT_FIELDS.contains(sortBy)) {
            throw new IllegalArgumentException("Invalid sortBy value: " + sortBy);
        }
    }

    private void validateSortDirection(String direction) {
        if (!direction.equalsIgnoreCase(Sort.Direction.ASC.name())
                && !direction.equalsIgnoreCase(Sort.Direction.DESC.name())) {
            throw new IllegalArgumentException("Invalid direction value. Use 'asc' or 'desc'.");
        }
    }
}
