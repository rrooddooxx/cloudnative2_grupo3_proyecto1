package com.cn2g3.crudfn2;

import com.cn2g3.crudfn2.product.model.NewProductDto;
import com.cn2g3.crudfn2.product.model.UpdateProductProductPriceDto;
import com.cn2g3.crudfn2.utils.JsonUtils;
import com.cn2g3.crudfn2.validations.HttpTriggerValidator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.*;
import java.util.*;

public class HttpTriggerFunction {
  @FunctionName("update-product")
  public HttpResponseMessage run(
      @HttpTrigger(
              name = "req",
              methods = {HttpMethod.POST},
              authLevel = AuthorizationLevel.ANONYMOUS)
          HttpRequestMessage<Optional<String>> request,
      final ExecutionContext context) {

    var action = request.getQueryParameters().getOrDefault("action", "add");
    var invalidResponse = HttpTriggerValidator.validate(request, context, action);
    if (invalidResponse != null) {
      return invalidResponse.build();
    }

    if (action.equals("add") && request.getBody().isPresent()) {
      return handleAddProduct(request, context);
    } else {
      return handleUpdateProduct(request, context);
    }
  }

  private HttpResponseMessage handleAddProduct(
      HttpRequestMessage<Optional<String>> request, ExecutionContext context) {
    try {
      NewProductDto newProduct =
          JsonUtils.mapToObject(request.getBody().get(), NewProductDto.class);

      ProductEventProducer.produceProductAddedEvent(newProduct);

      return request
          .createResponseBuilder(HttpStatus.CREATED)
          .header("Content-Type", "application/json")
          .body(
              JsonUtils.mapToJson(
                  new HashMap<>(Map.of("status", "Evento AGREGAR PRODUCTO enviado!"))))
          .build();
    } catch (JsonProcessingException ex) {
      context
          .getLogger()
          .severe("Add Product | ERROR deserializing payload:  %s".formatted(ex.getMessage()));
      return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR).build();
    } catch (Exception ex) {
      context.getLogger().severe("Add Product | Internal Error:  %s".formatted(ex.getMessage()));
      return request
          .createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
          .header("Content-Type", "application/json")
          .body(JsonUtils.mapToJson(new HashMap<>(Map.of("error", ex.getMessage()))))
          .build();
    }
  }

  private HttpResponseMessage handleUpdateProduct(
      HttpRequestMessage<Optional<String>> request, ExecutionContext context) {
    try {
      UpdateProductProductPriceDto updatedProduct =
          JsonUtils.mapToObject(request.getBody().get(), UpdateProductProductPriceDto.class);

      ProductEventProducer.produceProductUpdatedEvent(updatedProduct);

      return request
          .createResponseBuilder(HttpStatus.CREATED)
          .header("Content-Type", "application/json")
          .body(
              JsonUtils.mapToJson(
                  new HashMap<>(Map.of("status", "Evento ACTUALIZAR PRODUCTO enviado!"))))
          .build();
    } catch (JsonProcessingException ex) {
      context
          .getLogger()
          .severe("Update Product | ERROR deserializing payload:  %s".formatted(ex.getMessage()));
      return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR).build();
    } catch (Exception ex) {
      context.getLogger().severe("Update Product | Internal Error:  %s".formatted(ex.getMessage()));
      return request
          .createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
          .header("Content-Type", "application/json")
          .body(JsonUtils.mapToJson(new HashMap<>(Map.of("error", ex.getMessage()))))
          .build();
    }
  }
  
}
