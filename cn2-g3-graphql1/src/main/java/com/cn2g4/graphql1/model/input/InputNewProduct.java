package com.cn2g4.graphql1.model.input;

public record InputNewProduct(
    String marca,
    String nombreProducto,
    Integer precio,
    String categoria,
    String bodegaId
) {}
