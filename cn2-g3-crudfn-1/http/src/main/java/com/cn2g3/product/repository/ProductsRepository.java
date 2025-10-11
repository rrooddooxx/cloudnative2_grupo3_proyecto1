package com.cn2g3.product.repository;

import com.cn2g3.infrastructure.DataSourceProvider;
import com.cn2g3.product.model.Bodega;
import com.cn2g3.product.model.Product;
import java.sql.*;
import java.util.*;
import java.util.UUID;
import javax.sql.DataSource;

public class ProductsRepository {

  private DataSource dataSourceProvider = null;

  private DataSource getDataSource() {
    if (this.dataSourceProvider == null) {
      this.dataSourceProvider = DataSourceProvider.get();
      return this.dataSourceProvider;
    }
    return this.dataSourceProvider;
  }

  public List<Product> getProducts() throws SQLException {
    return getProducts("", 50, 0);
  }

  public List<Product> getProducts(String search, int limit, int offset) throws SQLException {
    String base =
        """
        select p.id, p.marca, p.nombre, p.precio,
               c.nombre as categoria,
               b.nombre as nombre_bodega
          from producto p
          join categoria c on p.categoria_id = c.id
          join producto_bodega pb on pb.producto_id = p.id
          join bodega b on pb.bodega_id = b.id
        """;
    String where =
        (search != null && !search.isBlank())
            ? " where lower(p.nombre) like ? or lower(p.marca) like ? "
            : "";
    String sql = base + where + " order by p.id limit ? offset ?";

    var dataSource = getDataSource();

    List<Product> products = new ArrayList<>();
    try (Connection cn = dataSource.getConnection();
        PreparedStatement ps = cn.prepareStatement(sql)) {

      int i = 1;
      if (!where.isBlank()) {
        String term = "%" + search.toLowerCase() + "%";
        ps.setString(i++, term);
        ps.setString(i++, term);
      }
      ps.setInt(i++, limit);
      ps.setInt(i, offset);

      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          products.add(
              new Product(
                  rs.getObject("id", UUID.class),
                  rs.getString("marca"),
                  rs.getString("nombre"),
                  rs.getInt("precio"),
                  rs.getString("categoria"),
                  rs.getString("nombre_bodega")));
        }
      }
    }
    return products;
  }

  public List<Bodega> getWarehouses(String search, int limit, int offset) throws SQLException {
    String base =
        """
        select b.id as bodega_id, b.nombre,
               count(pb.producto_id) as cantidad_productos
          from bodega b
     left join producto_bodega pb on pb.bodega_id = b.id
     """;
    String where = (search != null && !search.isBlank()) ? " where lower(b.nombre) like ? " : "";
    String sql = base + where + " group by b.id, b.nombre order by b.nombre limit ? offset ?";

    var dataSource = getDataSource();
    List<Bodega> warehouses = new ArrayList<>();
    try (Connection cn = dataSource.getConnection();
        PreparedStatement ps = cn.prepareStatement(sql)) {

      int i = 1;
      if (!where.isBlank()) {
        ps.setString(i++, "%" + search.toLowerCase() + "%");
      }
      ps.setInt(i++, limit);
      ps.setInt(i, offset);

      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          warehouses.add(
              new Bodega(
                  rs.getObject("bodega_id", UUID.class),
                  rs.getString("nombre"),
                  rs.getInt("cantidad_productos")));
        }
      }
    }
    return warehouses;
  }

  public boolean deleteWarehouses(String warehouseId) throws SQLException {
    var dataSource = getDataSource();
    try (Connection cn = dataSource.getConnection()) {
      cn.setAutoCommit(false);
      int delIntersect = 0;
      int moved = 0;
      int delWh = 0;
      boolean isFound = false;
      try {
        UUID uuid = UUID.fromString(warehouseId);

        String sql0 =
            """
              SELECT COUNT(id) FROM bodega
              WHERE id = ?;
              """;

        try (PreparedStatement ps0 = cn.prepareStatement(sql0)) {
          ps0.setObject(1, uuid);
          try (ResultSet rs = ps0.executeQuery()) {
            if (!rs.next() || rs.getInt(1) == 0) {
              cn.rollback();
              return false;
            }
          }
        } catch (Exception ex) {
          System.out.println("SQL 0: " + ex.getMessage());
        }

        String sql1 =
            """
          DELETE FROM producto_bodega
          WHERE bodega_id = ?
            AND producto_id IN (
              SELECT producto_id
              FROM producto_bodega
              WHERE bodega_id = (SELECT id FROM bodega WHERE nombre = 'CD Valparaíso')
              INTERSECT
              SELECT producto_id
              FROM producto_bodega
              WHERE bodega_id = ?
            )
          """;
        try (PreparedStatement ps1 = cn.prepareStatement(sql1)) {
          ps1.setObject(1, uuid);
          ps1.setObject(2, uuid);
          delIntersect = ps1.executeUpdate();
        } catch (Exception ex) {
          System.out.println("SQL 1: " + ex.getMessage());
        }

        // 2) Move remaining products to Valparaíso
        String sql2 =
            """
          UPDATE producto_bodega
             SET bodega_id = (SELECT id FROM bodega WHERE nombre = 'CD Valparaíso')
           WHERE bodega_id = ?
          """;
        try (PreparedStatement ps2 = cn.prepareStatement(sql2)) {
          ps2.setObject(1, uuid);
          moved = ps2.executeUpdate();
          // optionally validate moved
        } catch (Exception ex) {
          System.out.println("SQL 2: " + ex.getMessage());
        }

        // 3) Delete the warehouse
        String sql3 = "DELETE FROM bodega WHERE id = ?";
        try (PreparedStatement ps3 = cn.prepareStatement(sql3)) {
          ps3.setObject(1, uuid);
          delWh = ps3.executeUpdate();
          // ensure exactly one row deleted, else throw
        } catch (Exception ex) {
          System.out.println("SQL 3: " + ex.getMessage());
        }

        cn.commit();
        int sum = (delIntersect + moved + delWh);
        return sum > 0;
      } catch (Exception e) {
        cn.rollback();
        throw e;
      } finally {
        cn.setAutoCommit(true);
      }
    }
  }
}
