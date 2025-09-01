package com.cn2g3.bff.controller;

import com.cn2g3.bff.model.NewProductDto;
import com.cn2g3.bff.model.NewProductResponseDto;
import com.cn2g3.bff.model.Product;
import com.cn2g3.bff.model.UpdateProductPriceRequestDto;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/inventory/products")
public class ProductController {

  private final ProductService productService;

  @GetMapping
  public Mono<ResponseEntity<Flux<Product>>> getProducts() {
    return productService.getProducts();
  }

  @GetMapping("/{id}")
  public Mono<ResponseEntity<Flux<Product>>> getProductById(@PathVariable("id") String productId) {
    return productService.getProductById(productId);
  }

  @PostMapping("/add")
  public Mono<ResponseEntity<Mono<NewProductResponseDto>>> addProduct(
      @RequestBody() NewProductDto newProductDto) {
    return productService.addProduct(newProductDto);
  }

  @PatchMapping("/update-price/{id}")
  public ResponseEntity<Map<String, String>> updateProductPrice(
      @PathVariable("id") String productId,
      @RequestBody() UpdateProductPriceRequestDto updateProductPriceDto) {
    return productService.updateProductPrice(productId, updateProductPriceDto);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Map<String, String>> deleteProductById(
      @PathVariable("id") Optional<String> id) {
    if (id.isEmpty()) {
      return ResponseEntity.badRequest().build();
    }
    return productService.deleteProduct(id.get());
  }
}
