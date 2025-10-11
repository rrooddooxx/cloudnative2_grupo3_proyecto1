package com.cn2g3;

import com.cn2g3.product.model.Bodega;
import com.cn2g3.product.model.Product;
import com.cn2g3.product.repository.ProductsRepository;
import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.*;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Logger;

/**
 * GET /api/get-products Query params soportados: - ?search=texto - ?limit=50 (1..500) - ?offset=0
 * (>=0) - ?warehouses=show (si se pasa, lista bodegas en vez de productos)
 */
public class HttpTriggerFunction {

  private ProductsRepository repository = null;

  private static int parseInt(String val, int def, int min, int max) {
    if (val == null || val.isBlank()) return def;
    try {
      int n = Integer.parseInt(val);
      if (n < min || n > max) throw new IllegalArgumentException("Parámetro fuera de rango");
      return n;
    } catch (NumberFormatException nfe) {
      throw new IllegalArgumentException("Parámetro inválido");
    }
  }

  private ProductsRepository getRepository() {
    if (this.repository == null) {
      this.repository = new ProductsRepository();
    }
    return this.repository;
  }

  @FunctionName("get-products")
  public HttpResponseMessage run(
      @HttpTrigger(
              name = "req",
              methods = {HttpMethod.GET},
              authLevel = AuthorizationLevel.ANONYMOUS,
              route = "get-products")
          HttpRequestMessage<Optional<String>> request,
      final ExecutionContext context) {

    Logger logger = context.getLogger();
    try {
      String search = request.getQueryParameters().getOrDefault("search", "");
      int limit = parseInt(request.getQueryParameters().get("limit"), 50, 1, 500);
      int offset = parseInt(request.getQueryParameters().get("offset"), 0, 0, 100000);
      String warehousesParam = request.getQueryParameters().getOrDefault("warehouses", null);

      var repo = getRepository();

      if ("show".equalsIgnoreCase(warehousesParam)) {
        List<Bodega> data = repo.getWarehouses(search, limit, offset);
        return json(request, 200, data);
      } else {
        List<Product> data = repo.getProducts(search, limit, offset);
        return json(request, 200, data);
      }

    } catch (IllegalArgumentException bad) {
      return json(request, 400, Map.of("error", bad.getMessage()));
    } catch (SQLException ex) {
      logger.severe("SQL error: " + ex.getMessage());
      return json(request, 500, Map.of("error", "Error de base de datos"));
    } catch (Exception e) {
      logger.severe("GET PRODUCTS error: " + e);
      return json(request, 500, Map.of("error", "Error interno"));
    }
  }

  @FunctionName("delete-warehouse")
  public HttpResponseMessage runDeleteWarehouse(
      @HttpTrigger(
              name = "req",
              methods = {HttpMethod.DELETE},
              authLevel = AuthorizationLevel.ANONYMOUS,
              route = "delete-warehouse/{warehouseId}")
          HttpRequestMessage<Optional<String>> request,
      @BindingName("warehouseId") String warehouseId,
      final ExecutionContext context) {

    if (warehouseId == null) {
      context.getLogger().severe("Delete Warehouse | ERROR! No warehouseId given on request!");
      return request.createResponseBuilder(HttpStatus.BAD_REQUEST).build();
    }

    Logger logger = context.getLogger();
    try {

      var repo = getRepository();

      boolean successExecuted = repo.deleteWarehouses(warehouseId);
      return json(request, 200, successExecuted ? "SUCCESS" : "NO_OP");

    } catch (IllegalArgumentException bad) {
      return json(request, 400, Map.of("error", bad.getMessage()));
    } catch (SQLException ex) {
      logger.severe("SQL error: " + ex.getMessage());
      return json(request, 500, Map.of("error", "Error de base de datos"));
    } catch (Exception e) {
      logger.severe("GET PRODUCTS error: " + e);
      return json(request, 500, Map.of("error", "Error interno"));
    }
  }

  private HttpResponseMessage json(HttpRequestMessage<?> req, int status, Object body) {
    return req.createResponseBuilder(HttpStatus.valueOf(status))
        .header("Content-Type", "application/json")
        .header("Cache-Control", "no-store")
        .body(body)
        .build();
  }
}
