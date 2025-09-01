package org.cn2g3.crudfn3;

import com.cn2g3.crudfn2.product.repository.ProductsRepository;
import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.*;
import java.util.*;

public class HttpTriggerFunction {
  @FunctionName("delete-product")
  public HttpResponseMessage run(
      @HttpTrigger(
              name = "req",
              methods = {HttpMethod.DELETE},
              authLevel = AuthorizationLevel.ANONYMOUS,
              route = "delete-product/{productId}")
          HttpRequestMessage<Optional<String>> request,
      @BindingName("productId") String productId,
      final ExecutionContext context) {

    if (productId == null || productId.isBlank()) {
      context.getLogger().severe("Delete Product | ERROR! No product id given on request!");
      return request.createResponseBuilder(HttpStatus.BAD_REQUEST).build();
    }

    context
        .getLogger()
        .info("Delete Product | Starting request for product id %s".formatted(productId));
    ProductsRepository productsRepository = new ProductsRepository();

    try {
      boolean updated = productsRepository.deleteProductById(productId);
      return request.createResponseBuilder(updated ? HttpStatus.OK : HttpStatus.NOT_FOUND).build();
    } catch (Exception ex) {
      context.getLogger().severe("Delete Product | ERROR %s".formatted(ex.getMessage()));
      return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }
}
