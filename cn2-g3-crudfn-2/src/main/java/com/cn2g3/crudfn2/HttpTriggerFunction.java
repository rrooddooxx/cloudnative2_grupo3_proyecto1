package com.cn2g3.crudfn2;

import com.cn2g3.crudfn2.product.model.NewProductDto;
import com.cn2g3.crudfn2.product.model.Product;
import com.cn2g3.crudfn2.product.model.UpdateProductProductPriceDto;
import com.cn2g3.crudfn2.product.repository.ProductsRepository;
import com.cn2g3.crudfn2.utils.JsonUtils;
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

    String action = request.getQueryParameters().getOrDefault("action", "add");
    context
        .getLogger()
        .info("Add or Update Product | Starting request... Action: %s.".formatted(action));

    if (!action.equals("add") && !action.equals("update")) {
      context.getLogger().severe("Add or Update Product | Invalid Action: %s".formatted(action));
      return request.createResponseBuilder(HttpStatus.BAD_REQUEST).build();
    }

    if (request.getBody().isEmpty()) {
      context
          .getLogger()
          .severe(
              "Add or Update Product | ERROR! Action: (%s) with no request body, Aborting..."
                  .formatted(action));
      return request.createResponseBuilder(HttpStatus.BAD_REQUEST).build();
    }

    ProductsRepository productsRepository = new ProductsRepository();

    if (action.equals("add") && request.getBody().isPresent()) {
      try {
        NewProductDto newProduct =
            JsonUtils.mapToObject(request.getBody().get(), NewProductDto.class);

        Optional<UUID> addedProductId =
            productsRepository.addProduct(
                Product.generateNew(
                    newProduct.marca(),
                    newProduct.nombreProducto(),
                    newProduct.precio(),
                    newProduct.categoria()),
                newProduct.bodegaId());

        return request
            .createResponseBuilder(HttpStatus.CREATED)
            .header("Content-Type", "application/json")
            .body(
                JsonUtils.mapToJson(
                    addedProductId.isPresent()
                        ? new HashMap<>(Map.of("id", addedProductId.get()))
                        : new HashMap<>(
                            Map.of("error", "No ID for added product. Check database"))))
            .build();
      } catch (JsonProcessingException ex) {
        context
            .getLogger()
            .severe("Add Product | ERROR deserializing payload:  %s".formatted(ex.getMessage()));
        return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR).build();
      } catch (Exception ex) {
        context.getLogger().severe("Add Product | Internal Error:  %s".formatted(ex.getMessage()));
        return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR).build();
      }
    }

    try {
      UpdateProductProductPriceDto updatedProduct =
          JsonUtils.mapToObject(request.getBody().get(), UpdateProductProductPriceDto.class);
      boolean wasUpdated =
          productsRepository.updateProductPriceById(updatedProduct.id(), updatedProduct.precio());
      return request
          .createResponseBuilder(wasUpdated ? HttpStatus.CREATED : HttpStatus.EXPECTATION_FAILED)
          .build();
    } catch (JsonProcessingException ex) {
      context
          .getLogger()
          .severe("Update Product | ERROR deserializing payload:  %s".formatted(ex.getMessage()));
      request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR).build();
    } catch (Exception ex) {
      context.getLogger().severe("Update Product | Internal Error:  %s".formatted(ex.getMessage()));
      return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    return request.createResponseBuilder(HttpStatus.BAD_REQUEST).build();
  }
}
