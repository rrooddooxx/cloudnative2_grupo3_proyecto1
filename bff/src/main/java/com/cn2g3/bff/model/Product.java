package com.cn2g3.bff.model;

import java.util.UUID;

public record Product(
    UUID id,
    String marca,
    String nombreProducto,
    Integer precio,
    String categoria,
    String nombreBodega) {}
