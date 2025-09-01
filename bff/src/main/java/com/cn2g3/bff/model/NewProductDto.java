package com.cn2g3.bff.model;

public record NewProductDto(
    String marca, String nombreProducto, Integer precio, String categoria, String bodegaId) {}
