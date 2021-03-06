package com.example.web.controller;

import com.example.web.dto.ProductDTO;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping("/")
public class ProductWebController {
    private RestTemplate restTemplate;

    public ProductWebController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("popular-products")
    public String renderPopularProductPage(Model model, HttpServletRequest request, HttpServletResponse response) {
        UriComponents uriComponents = UriComponentsBuilder.fromUriString("http://localhost:8081/products")
                .queryParam("page", 1)
                .queryParam("size", 10)
                .queryParam("purchaseCount", 50)
                .build();
        String ifModifiedSince = request.getHeader(HttpHeaders.IF_MODIFIED_SINCE);
        HttpHeaders headers = new HttpHeaders();
        if  (ifModifiedSince != null) {
            headers.set(HttpHeaders.IF_MODIFIED_SINCE, ifModifiedSince);
        }

        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);
        ResponseEntity<List<ProductDTO>> apiResource = restTemplate.exchange(
                uriComponents.toUri(),
                HttpMethod.GET,
                httpEntity,
                new ParameterizedTypeReference<List<ProductDTO>>() {
                }
        );

        if (apiResource.getStatusCode().equals(HttpStatus.OK)) {
            List<ProductDTO> productList = apiResource.getBody();
            model.addAttribute("productList", productList);
            String lastModified = apiResource.getHeaders().getFirst(HttpHeaders.CACHE_CONTROL);
            if (lastModified != null) {
                response.setHeader(HttpHeaders.LAST_MODIFIED, lastModified);
            }
            String cacheControl = apiResource.getHeaders().getFirst(HttpHeaders.CACHE_CONTROL);
            if (cacheControl != null) {
                response.setHeader(HttpHeaders.CACHE_CONTROL, cacheControl);
            }
            return "popular-products";
        } else if (apiResource.getStatusCode().equals(HttpStatus.NOT_MODIFIED)) {
            response.setStatus(HttpStatus.NOT_MODIFIED.value());
            return null;
        } else {
            throw  new RuntimeException("Got unexpected response from product service");
        }
    }
}
