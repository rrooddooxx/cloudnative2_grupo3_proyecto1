package com.cn2g3;

import com.cn2g3.product.model.Bodega;
import com.cn2g3.product.model.Product;
import com.cn2g3.product.repository.ProductsRepository;
import com.cn2g3.utils.JsonUtils;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class HttpTriggerFunction {

  @FunctionName("get-products")
  public HttpResponseMessage run(
      @HttpTrigger(
              name = "req",
              methods = {HttpMethod.GET},
              authLevel = AuthorizationLevel.ANONYMOUS)
          HttpRequestMessage<Optional<String>> request,
      final ExecutionContext context) {
    context.getLogger().info("GetProducts | Starting get products function...");

    String warehousesParam = request.getQueryParameters().getOrDefault("warehouses", null);
    System.out.println(warehousesParam);
    if (warehousesParam != null && warehousesParam.equals("show")) {
      return request
          .createResponseBuilder(HttpStatus.OK)
          .header("Content-Type", "application/json")
          .body(JsonUtils.mapToJson(getWarehouses(context.getLogger())))
          .build();
    }

    return request
        .createResponseBuilder(HttpStatus.OK)
        .header("Content-Type", "application/json")
        .body(JsonUtils.mapToJson(getProducts(context.getLogger())))
        .build();
  }

  private List<Product> getProducts(Logger logger) {
    List<Product> productList = new ArrayList<>();
    try {
      ProductsRepository productsRepository = new ProductsRepository();
      productList = productsRepository.getProducts();
    } catch (SQLException ex) {
      logger.info("Error establishing SQL connection:" + ex.getMessage());
    }
    return productList;
  }

  private List<Bodega> getWarehouses(Logger logger) {
    List<Bodega> warehouseList = new ArrayList<>();
    try {
      ProductsRepository productsRepository = new ProductsRepository();
      warehouseList = productsRepository.getWarehouses();
    } catch (SQLException ex) {
      logger.info("Error establishing SQL connection:" + ex.getMessage());
    }
    return warehouseList;
  }
}
