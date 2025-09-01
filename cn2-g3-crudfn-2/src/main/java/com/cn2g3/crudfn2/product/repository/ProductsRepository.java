package com.cn2g3.crudfn2.product.repository;

import com.cn2g3.crudfn2.infrastructure.DataSourceProvider;
import com.cn2g3.crudfn2.product.model.Product;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.sql.DataSource;

public class ProductsRepository {
  DataSource dataSource = DataSourceProvider.get();

  public List<Product> getProducts() throws SQLException {
    String getProductsQuery =
"""
select p.id, p.marca, p.nombre nombre, p.precio, c.nombre categoria from producto p
join categoria c
on p.categoria_id = c.id
        """;

    List<Product> products = new ArrayList<>();

    try (Connection cnx = dataSource.getConnection();
        PreparedStatement ps = cnx.prepareStatement(getProductsQuery);
        ResultSet rs = ps.executeQuery()) {

      while (rs.next()) {
        Product product =
            new Product(
                rs.getObject("id", UUID.class),
                rs.getString("marca"),
                rs.getString("nombre"),
                rs.getInt("precio"),
                rs.getString("categoria"));
        products.add(product);
      }
    } catch (SQLException ex) {
      throw ex;
    }

    return products;
  }

  public Optional<UUID> addProduct(Product newProduct) throws SQLException {
    System.out.println("SQL | Executing add product... " + newProduct);

    String insertProductQuery =
        """
           insert into producto (id, precio, nombre, marca, categoria_id) values
             (?, ?, ?, ?, ?)
        """;

    UUID addedProductId = null;

    try (Connection cnx = dataSource.getConnection();
        PreparedStatement ps =
            cnx.prepareStatement(insertProductQuery, Statement.RETURN_GENERATED_KEYS)) {

      ps.setObject(1, UUID.randomUUID());
      ps.setInt(2, newProduct.precio());
      ps.setString(3, newProduct.nombreProducto());
      ps.setString(4, newProduct.marca());
      ps.setObject(5, UUID.fromString(newProduct.categoria()));

      int rows = ps.executeUpdate();
      System.out.println("rows: %s".formatted(rows));
      if (rows > 0) {
        try (ResultSet rs = ps.getGeneratedKeys()) {
          if (rs.next()) {
            System.out.println("hay next");
            addedProductId = rs.getObject(1, UUID.class);
          }
        }
        System.out.println("SQL | Product created with id: " + addedProductId);
        return Optional.ofNullable(addedProductId);
      }
      return Optional.empty();
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
