package com.cn2g3.crudfn2.product.repository;

import com.cn2g3.crudfn2.infrastructure.DataSourceProvider;
import com.cn2g3.crudfn2.product.model.Product;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.UUID;
import javax.sql.DataSource;

public class ProductsRepository {
  DataSource dataSource = DataSourceProvider.get();

  public Optional<UUID> addProduct(Product newProduct, String bodegaId) throws SQLException {
    System.out.println("SQL | Executing add product... " + newProduct);

    String insertProductQuery =
        """
           insert into producto (id, precio, nombre, marca, categoria_id) values
             (?, ?, ?, ?, ?)
        """;

    String insertProductIntoWarehouseQuery =
        """
               insert into producto_bodega (producto_id, bodega_id) values
                 (?, ?)
            """;

    UUID addedProductId = UUID.randomUUID();

    Connection cnx = null;
    try {
      cnx = dataSource.getConnection();
      cnx.setAutoCommit(false);

      try (PreparedStatement psProduct =
          cnx.prepareStatement(insertProductQuery, Statement.RETURN_GENERATED_KEYS)) {

        psProduct.setObject(1, addedProductId);
        psProduct.setInt(2, newProduct.precio());
        psProduct.setString(3, newProduct.nombreProducto());
        psProduct.setString(4, newProduct.marca());
        psProduct.setObject(5, UUID.fromString(newProduct.categoria()));
        psProduct.addBatch();

        int rows = psProduct.executeUpdate();
        System.out.println("rows: %s".formatted(rows));
        if (rows < 1) {
          System.out.println("SQL | Error creating product, rolling back");
          cnx.rollback();
          return Optional.empty();
        }

        try (PreparedStatement psWarehouse =
            cnx.prepareStatement(insertProductIntoWarehouseQuery)) {
          psWarehouse.setObject(1, addedProductId);
          psWarehouse.setObject(2, UUID.fromString(bodegaId));
          int inserted = psWarehouse.executeUpdate();
          if (inserted < 1) {
            System.out.println("SQL | Error inserting product on warehouse, rolling back");
            cnx.rollback();
            return Optional.empty();
          }
        }

        cnx.commit();
        System.out.println("SQL | Product created with id: " + addedProductId);
        return Optional.of(addedProductId);
      }
    } catch (SQLException ex) {
      System.err.println("SQL | Error adding product or linking to warehouse: " + ex.getMessage());
      if (cnx != null) {
        try {
          cnx.rollback();
          System.err.println("SQL | Transaction rolled back due to error.");
        } catch (SQLException rbEx) {
          System.err.println("SQL | Error during rollback: " + rbEx.getMessage());
        }
      }
      throw ex;
    } finally {
      if (cnx != null) {
        try {
          cnx.setAutoCommit(true);
          cnx.close();
        } catch (SQLException closeEx) {
          System.err.println("SQL | Error closing connection: " + closeEx.getMessage());
        }
      }
    }
  }

  public boolean updateProductPriceById(String productId, Integer newPrice) throws SQLException {
    System.out.println("SQL | Executing update product... " + productId);

    String insertProductQuery =
        """
        UPDATE producto SET precio = ? where id = ?
        """;

    try (Connection cnx = dataSource.getConnection();
        PreparedStatement ps = cnx.prepareStatement(insertProductQuery)) {

      ps.setInt(1, newPrice);
      ps.setObject(2, UUID.fromString(productId));

      int updated = ps.executeUpdate();
      return updated > 0;

    } catch (SQLException ex) {
      throw ex;
    }
  }
}
