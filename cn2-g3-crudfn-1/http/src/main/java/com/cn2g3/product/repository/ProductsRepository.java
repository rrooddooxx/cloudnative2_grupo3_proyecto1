package com.cn2g3.product.repository;

import com.cn2g3.infrastructure.DataSourceProvider;
import com.cn2g3.product.model.Product;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProductsRepository {
  private static final Logger log = LoggerFactory.getLogger(ProductsRepository.class);
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
}
