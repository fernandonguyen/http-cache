package com.example.productservice.service;

import com.example.productservice.domain.Product;
import org.springframework.util.MultiValueMap;

import java.time.ZonedDateTime;
import java.util.List;

public interface ProductService {
    List<Product> searchProducts(MultiValueMap<String, String> params);
    ZonedDateTime getProductTableLastModifiedDate();
}
