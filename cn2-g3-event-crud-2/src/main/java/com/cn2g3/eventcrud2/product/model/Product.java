package com.cn2g3.crudfn2.product.model;

import java.util.UUID;

public record Product(
    UUID id, String marca, String nombreProducto, Integer precio, String categoria) {

  public static Product generateNew(
      String marca, String nombreProducto, Integer precio, String idCategoria) {
    return new Product(UUID.randomUUID(), marca, nombreProducto, precio, idCategoria);
  }
}
