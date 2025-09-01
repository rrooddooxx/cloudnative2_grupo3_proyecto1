package com.cn2g3;

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
    List<Product> productList = getProducts(context.getLogger());
    return request
        .createResponseBuilder(HttpStatus.OK)
        .header("Content-Type", "application/json")
        .body(JsonUtils.mapToJson(productList))
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
}
