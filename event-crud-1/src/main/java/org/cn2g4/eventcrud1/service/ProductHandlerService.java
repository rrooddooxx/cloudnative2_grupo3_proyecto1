package org.cn2g4.eventcrud1.service;

import org.cn2g4.eventcrud1.product.model.NewProductDto;
import org.cn2g4.eventcrud1.product.model.Product;
import org.cn2g4.eventcrud1.product.model.UpdateProductProductPriceDto;
import org.cn2g4.eventcrud1.product.repository.ProductsRepository;

public class ProductHandlerService {
  public void handleAddProduct(NewProductDto newProduct) {
    try {
      productsRepository()
          .addProduct(
              Product.generateNew(
                  newProduct.marca(),
                  newProduct.nombreProducto(),
                  newProduct.precio(),
                  newProduct.categoria()),
              newProduct.bodegaId());
    } catch (Exception ex) {
      System.out.println("ERROR: " + ex.getMessage());
    }
  }

  public void handleUpdateProduct(UpdateProductProductPriceDto updatedProduct) {
    try {
      productsRepository().updateProductPriceById(updatedProduct.id(), updatedProduct.precio());
    } catch (Exception ex) {
      System.out.println("ERROR: " + ex.getMessage());
    }
  }

  private ProductsRepository productsRepository() {
    return new ProductsRepository();
  }
}
