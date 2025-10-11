package com.cn2g3.bff.controller;

import com.cn2g3.bff.services.ProductService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/graphql")
@RequiredArgsConstructor
@Slf4j
public class ProductQueryController {

  private final ProductService productService;

  @PostMapping("/query/product")
  public ResponseEntity<Mono<JsonNode>> callProductQuery(@RequestBody() String query) {
    log.info(query);
    return ResponseEntity.ok(productService.executeProductQuery(query));
  }

  @PostMapping("/mutation/update-price")
  public ResponseEntity<Mono<JsonNode>> callPriceMutation(@RequestBody() String query) {
    log.info(query);
    return ResponseEntity.ok(productService.executePriceMutation(query));
  }
}
