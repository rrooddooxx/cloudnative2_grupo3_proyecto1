package org.cn2g4.eventcrud1.product.model;

import java.util.UUID;

public record Product(
    UUID id, String marca, String nombreProducto, Integer precio, String categoria) {
  public static Product generateNew(
      String marca, String nombreProducto, Integer precio, String idCategoria) {
    return new Product(UUID.randomUUID(), marca, nombreProducto, precio, idCategoria);
  }
}
