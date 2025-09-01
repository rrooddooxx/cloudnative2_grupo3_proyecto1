package com.cn2g3.bff.services;

import com.cn2g3.bff.config.BffConstants;
import com.cn2g3.bff.model.NewProductDto;
import com.cn2g3.bff.model.NewProductResponseDto;
import com.cn2g3.bff.model.Product;
import com.cn2g3.bff.model.UpdateProductPrice;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductWebService {

  @Qualifier("crudFunction1Client")
  private final WebClient getProductsClient;

  @Qualifier("crudFunction2Client")
  private final WebClient addOrUpdateProductsClient;

  @Qualifier("crudFunction3Client")
  private final WebClient deleteProductsClient;

  public Flux<Product> getAllProducts() {
    return getProductsClient
        .get()
        .uri("/api/get-products")
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .bodyToFlux(Product.class);
  }

  public Mono<NewProductResponseDto> addNewProduct(NewProductDto newProductDto) {
    return addOrUpdateProductsClient
        .post()
        .uri(BffConstants.FN2_UPDATE_PATH, BffConstants.FN2_ADD_ACTION)
        .bodyValue(newProductDto)
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .bodyToMono(NewProductResponseDto.class);
  }

  public Mono<HttpStatusCode> updateProduct(UpdateProductPrice updatedProduct) {
    return addOrUpdateProductsClient
        .post()
        .uri(BffConstants.FN2_UPDATE_PATH, BffConstants.FN2_UPDATE_ACTION)
        .bodyValue(updatedProduct)
        .httpRequest(
            clientHttpRequest -> {
              log.info("Uri: {} ", clientHttpRequest.getURI());
              log.info("Request: {}", (Object) clientHttpRequest.getNativeRequest());
              log.info("Method: {}", clientHttpRequest.getMethod());
              log.info("Attributes: {}", clientHttpRequest.getAttributes());
            })
        .exchangeToMono(clientResponse -> Mono.just(clientResponse.statusCode()));
  }

  public Mono<HttpStatusCode> deleteProduct(String productId) {
    return deleteProductsClient
        .delete()
        .uri(BffConstants.FN3_DELETE_PATH, productId)
        .httpRequest(
            clientHttpRequest -> {
              log.info("Uri: {} ", clientHttpRequest.getURI());
              log.info("Request: {}", (Object) clientHttpRequest.getNativeRequest());
              log.info("Method: {}", clientHttpRequest.getMethod());
              log.info("Attributes: {}", clientHttpRequest.getAttributes());
            })
        .exchangeToMono(clientResponse -> Mono.just(clientResponse.statusCode()));
  }
}
