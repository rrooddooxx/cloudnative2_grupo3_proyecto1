package com.cn2g3.product.model;

import java.util.UUID;

public record Product(
    UUID id, String marca, String nombreProducto, Integer precio, String categoria) {}
