package org.cn2g4.eventcrud1;

import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.*;
import org.cn2g4.eventcrud1.product.model.AddProductEventSchema;
import org.cn2g4.eventcrud1.product.model.UpdateProductEventSchema;
import org.cn2g4.eventcrud1.service.ProductHandlerService;

public class EventGridTriggerJava {

  private ProductHandlerService productService;

  @FunctionName("AgregarProducto")
  public void runAgregarProducto(
      @EventGridTrigger(name = "event") AddProductEventSchema event,
      final ExecutionContext context) {
    context.getLogger().info("AgregarProducto Event Grid trigger function executed.");
    context.getLogger().info(event.id());
    context.getLogger().info(event.data().toString());
    var service = getProductService();
    service.handleAddProduct(event.data());
  }

  @FunctionName("ActualizarProducto")
  public void runActualizarProducto(
      @EventGridTrigger(name = "event") UpdateProductEventSchema event,
      final ExecutionContext context) {
    context.getLogger().info("ActualizarProducto Event Grid trigger function executed.");
    context.getLogger().info(event.id());
    context.getLogger().info(event.data().toString());
    var service = getProductService();
    service.handleUpdateProduct(event.data());
  }

  private ProductHandlerService getProductService() {
    if (this.productService == null) {
      this.productService = new ProductHandlerService();
    }

    return this.productService;
  }
}
