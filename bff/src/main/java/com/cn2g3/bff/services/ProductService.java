package com.cn2g3.bff.services;

import com.cn2g3.bff.client.ProductWebClient;
import com.cn2g3.bff.model.Bodega;
import com.cn2g3.bff.model.NewProductDto;
import com.cn2g3.bff.model.Product;
import com.cn2g3.bff.model.UpdateProductPrice;
import com.cn2g3.bff.model.UpdateProductPriceRequestDto;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
  private final ProductWebClient productWebClient;

  public Mono<ResponseEntity<Flux<Bodega>>> getWarehouses() {
    Flux<Bodega> products = productWebClient.getWarehouses();
    return Mono.just(ResponseEntity.ok(products));
  }

  public Mono<ResponseEntity<Flux<Product>>> getProducts() {
    Flux<Product> products = productWebClient.getAllProducts();
    return Mono.just(ResponseEntity.ok(products));
  }

  public Mono<ResponseEntity<Flux<Product>>> getProductById(String productId) {
    Flux<Product> products = productWebClient.getAllProducts();
    return Mono.just(
        ResponseEntity.ok(
            products.filter(product -> product.id().equals(UUID.fromString(productId)))));
  }

  public ResponseEntity<Map<String, String>> addProduct(NewProductDto newProductDto) {
    Mono<HttpStatusCode> newProduct = productWebClient.addNewProduct(newProductDto);
    return getMapResponseEntity(newProduct, "Evento enviado correctamente!");
  }

  public ResponseEntity<Map<String, String>> updateProductPrice(
      String productId, UpdateProductPriceRequestDto updateProductPriceDto) {
    log.info("Into update product price... Id: {}", productId);
    UpdateProductPrice toUpdate = new UpdateProductPrice(productId, updateProductPriceDto.precio());
    log.info("Update request: {}", toUpdate);
    Mono<HttpStatusCode> responseStatus = productWebClient.updateProduct(toUpdate);
    return getMapResponseEntity(responseStatus, "Evento enviado correctamente!");
  }

  public ResponseEntity<Map<String, String>> deleteProduct(String productId) {
    Mono<HttpStatusCode> responseStatus = productWebClient.deleteProduct(productId);
    return getMapResponseEntity(responseStatus, "Evento enviado correctamente!");
  }

  public ResponseEntity<Map<String, String>> deleteWarehouse(String warehouseId) {
    Mono<HttpStatusCode> responseStatus = productWebClient.deleteWarehouse(warehouseId);
    return getMapResponseEntity(responseStatus, "Operación ejecutada exitosamente!");
  }

  public Mono<JsonNode> executeProductQuery(String query) {
    return productWebClient.executeProductsQuery(query);
  }

  public Mono<JsonNode> executePriceMutation(String query) {
    return productWebClient.executePriceMutation(query);
  }

  private ResponseEntity<Map<String, String>> getMapResponseEntity(
      Mono<HttpStatusCode> responseStatus, String successMessage) {
    Optional<HttpStatusCode> status = responseStatus.blockOptional();
    Map<String, String> responseBody =
        Map.of(
            "status",
            status
                .filter(HttpStatusCode::is2xxSuccessful)
                .map(s -> successMessage)
                .orElse("Error!"));

    log.info("Update/Delete Response status: {}", status.orElse(HttpStatus.UNPROCESSABLE_ENTITY));
    return ResponseEntity.status(status.orElse(HttpStatus.INTERNAL_SERVER_ERROR))
        .body(responseBody);
  }
}
