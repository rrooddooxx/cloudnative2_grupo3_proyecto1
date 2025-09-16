package com.cn2g4.graphql1.resolver;

import com.cn2g4.graphql1.product.model.Product;
import com.cn2g4.graphql1.product.repository.ProductsRepository;
import graphql.schema.DataFetcher;
import java.util.List;

public class ProductsQueryResolver {

  private final ProductsRepository productsRepository;

  public ProductsQueryResolver(ProductsRepository productsRepository) {
    this.productsRepository = productsRepository;
  }

  public DataFetcher<List<Product>> productsDataFetcher() {
    return environment -> {
      String id = environment.getArgument("id");
      String name = environment.getArgument("nombreProducto");
      String marca = environment.getArgument("marca");
      String precio = environment.getArgument("precio");
      String categoria = environment.getArgument("categoria");
      String nombreBodega = environment.getArgument("nombreBodega");

      return productsRepository.getProducts(id, name, marca, categoria, nombreBodega);
    };
  }
}
