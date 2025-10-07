package org.cn2g3.crudfn3;

import com.cn2g3.crudfn2.utils.JsonUtils;
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

    System.out.println(productId);
    if (productId == null) {
      context.getLogger().severe("Delete Product | ERROR! No product id given on request!");
      return request.createResponseBuilder(HttpStatus.BAD_REQUEST).build();
    }

    context
        .getLogger()
        .info("Delete Product | Starting request for product id %s".formatted(productId));

    try {
      ProductEventProducer.produceProductDeletedEvent(productId);
      return request
          .createResponseBuilder(HttpStatus.CREATED)
          .header("Content-Type", "application/json")
          .body(
              JsonUtils.mapToJson(
                  new HashMap<>(Map.of("status", "Evento BORRAR PRODUCTO enviado!"))))
          .build();
    } catch (Exception ex) {
      context.getLogger().severe("Delete Product | Internal Error:  %s".formatted(ex.getMessage()));
      return request
          .createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
          .header("Content-Type", "application/json")
          .body(JsonUtils.mapToJson(new HashMap<>(Map.of("error", ex.getMessage()))))
          .build();
    }
  }
}
