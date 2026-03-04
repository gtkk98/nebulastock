package com.nebulastock.nebulastock.service;

import com.nebulastock.nebulastock.dto.request.ProductRequest;
import com.nebulastock.nebulastock.dto.response.ProductResponse;
import com.nebulastock.nebulastock.entity.Product;
import com.nebulastock.nebulastock.exception.ApiException;
import com.nebulastock.nebulastock.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    //CREATE
    @Transactional // if anything fails, the whole operation rolls back
    public ProductResponse create(ProductRequest request) {
        if (productRepository.existsBySku(request.getSku())) {
            throw new ApiException("SKU already exists: " + request.getSku(), HttpStatus.CONFLICT);
        }
        Product product = Product.builder()
                .sku(request.getSku())
                .name(request.getName())
                .description(request.getDescription())
                .unitPrice(request.getUnitPrice())
                .category(request.getCategory())
                .build();

        return toResponse(productRepository.save(product));
    }

    // READ ALL with pagination and filtering
    public Page<ProductResponse> getAll(String search, String category, int page, int size) {
        // Sort by name ascending by default — professional APIs always sort
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        return productRepository.findWithFilters(search, category, pageable)
                .map(this::toResponse); // map() transforms each Product → ProductResponse
    }

    //READ ONLY
    public ProductResponse getById(Integer id) {
        return toResponse(findOrThrow(id));
    }

    // UPDATE
    @Transactional
    public ProductResponse update(Integer id, ProductRequest request) {
        Product product = findOrThrow(id);

        // Check SKU conflict only if SKU is being changed
        if (!product.getSku().equals(request.getSku())
                && productRepository.existsBySku(request.getSku())) {
            throw new ApiException("SKU already exists: " + request.getSku(), HttpStatus.CONFLICT);
        }

        product.setSku(request.getSku());
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setUnitPrice(request.getUnitPrice());
        product.setCategory(request.getCategory());

        return toResponse(productRepository.save(product));
    }

    // DELETE
    @Transactional
    public void delete(Integer id) {
        Product product = findOrThrow(id);
        productRepository.delete(product);
    }

    // Private helper
    private Product findOrThrow(Integer id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ApiException(
                        "Product not found with id " + id, HttpStatus.NOT_FOUND));
    }

    // Manual mapping: Entity → DTO
    // This is what MapStruct auto-generates, but doing it manually first teaches you what's happening
    private ProductResponse toResponse(Product p) {
        return ProductResponse.builder()
                .id(p.getId())
                .sku(p.getSku())
                .name(p.getName())
                .description(p.getDescription())
                .unitPrice(p.getUnitPrice())
                .category(p.getCategory())
                .build();
    }
}
