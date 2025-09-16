package com.cn2g4.graphql1.product.model;

import java.util.UUID;

public record Product(
    UUID id,
    String marca,
    String nombreProducto,
    Integer precio,
    String categoria,
    String nombreBodega
) {}