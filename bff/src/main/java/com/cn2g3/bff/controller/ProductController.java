package com.cn2g3.bff.controller;

import com.cn2g3.bff.model.Bodega;
import com.cn2g3.bff.model.NewProductDto;
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
@RequestMapping("/api/v1/inventory")
public class ProductController {

  private final ProductService productService;

  @GetMapping("/warehouses")
  public Mono<ResponseEntity<Flux<Bodega>>> getWarehouses() {
    return productService.getWarehouses();
  }

  @GetMapping("/products")
  public Mono<ResponseEntity<Flux<Product>>> getProducts() {
    return productService.getProducts();
  }

  @GetMapping("/products/{id}")
  public Mono<ResponseEntity<Flux<Product>>> getProductById(@PathVariable("id") String productId) {
    return productService.getProductById(productId);
  }

  @PostMapping("/products/add")
  public ResponseEntity<Map<String, String>> addProduct(
      @RequestBody() NewProductDto newProductDto) {
    return productService.addProduct(newProductDto);
  }

  @PatchMapping("/products/update-price/{id}")
  public ResponseEntity<Map<String, String>> updateProductPrice(
      @PathVariable("id") String productId,
      @RequestBody() UpdateProductPriceRequestDto updateProductPriceDto) {
    return productService.updateProductPrice(productId, updateProductPriceDto);
  }

  @DeleteMapping("/products/{id}")
  public ResponseEntity<Map<String, String>> deleteProductById(
      @PathVariable("id") Optional<String> id) {
    if (id.isEmpty()) {
      return ResponseEntity.badRequest().build();
    }
    return productService.deleteProduct(id.get());
  }
}
