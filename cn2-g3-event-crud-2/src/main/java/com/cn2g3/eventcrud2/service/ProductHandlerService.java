package com.cn2g3.eventcrud2.service;

import com.cn2g3.eventcrud2.product.repository.ProductsRepository;
import java.util.UUID;

public class ProductHandlerService {
  public void handleDeleteProduct(UUID productId) {
    try {
      System.out.println("Delete Product | Executing DELETE for id: ".formatted(productId));
      productsRepository().deleteProductById(productId.toString());
    } catch (Exception ex) {
      System.out.println("ERROR: " + ex.getMessage());
    }
  }

  private ProductsRepository productsRepository() {
    return new ProductsRepository();
  }
}
