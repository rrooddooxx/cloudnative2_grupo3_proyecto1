package org.cn2g4.eventcrud1.product.model;

public record NewProductDto(
    String marca, String nombreProducto, Integer precio, String categoria, String bodegaId) {}
