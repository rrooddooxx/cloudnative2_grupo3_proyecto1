package com.cn2g3.eventcrud2;

import com.cn2g3.eventcrud2.product.model.ProductEventSchema;
import com.cn2g3.eventcrud2.service.ProductHandlerService;
import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.*;

public class EventGridTriggerJava {

  private ProductHandlerService service;

  @FunctionName("BorrarProducto")
  public void run(
      @EventGridTrigger(name = "event") ProductEventSchema productEvent,
      final ExecutionContext context) {
    context.getLogger().info("BorrarProducto Event Grid trigger function executed.");
    context.getLogger().info("Product ID: %s".formatted(productEvent.data()));
    var productService = getProductService();
    productService.handleDeleteProduct(productEvent.data());
  }

  private ProductHandlerService getProductService() {
    if (service == null) {
      this.service = new ProductHandlerService();
      return this.service;
    }
    return this.service;
  }
}
