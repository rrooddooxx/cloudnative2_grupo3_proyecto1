package com.cn2g3.product.repository;

import com.cn2g3.infrastructure.DataSourceProvider;
import com.cn2g3.product.model.Bodega;
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
select p.id, p.marca, p.nombre nombre, p.precio, c.nombre categoria,
       b.nombre nombre_bodega
    from producto p
 join categoria c
on p.categoria_id = c.id
join producto_bodega pb
on pb.producto_id =  p.id
join bodega b
on pb.bodega_id = b.id
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
                rs.getString("categoria"),
                rs.getString("nombre_bodega"));
        products.add(product);
      }
    } catch (SQLException ex) {
      throw ex;
    }

    return products;
  }

  public List<Bodega> getWarehouses() throws SQLException {
    String getWarehousesQuery =
        """
            select pb.bodega_id, b.nombre, count(pb.producto_id) cantidad_productos from producto_bodega pb
            join bodega b
            on pb.bodega_id = b.id
            join producto p
            on pb.producto_id = p.id
            group by pb.bodega_id, b.nombre
            """;

    List<Bodega> warehouses = new ArrayList<>();

    try (Connection cnx = dataSource.getConnection();
        PreparedStatement ps = cnx.prepareStatement(getWarehousesQuery);
        ResultSet rs = ps.executeQuery()) {

      while (rs.next()) {
        Bodega warehouse =
            new Bodega(
                rs.getObject("bodega_id", UUID.class),
                rs.getString("nombre"),
                rs.getInt("cantidad_productos"));
        warehouses.add(warehouse);
      }
    } catch (SQLException ex) {
      throw ex;
    }

    return warehouses;
  }
}
