package com.cn2g3.crudfn2.product.model;

public record NewProductDto(
    String marca, String nombreProducto, Integer precio, String categoria, String bodegaId) {}
