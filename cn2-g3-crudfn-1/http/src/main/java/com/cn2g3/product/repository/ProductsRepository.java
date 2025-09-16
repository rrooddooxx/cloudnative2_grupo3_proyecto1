package com.cn2g3.product.repository;

import com.cn2g3.product.model.Product;
import com.cn2g3.product.model.Bodega;

import java.sql.*;
import java.util.*;
import java.util.UUID;
import java.util.Properties;

/**
 * Repository JDBC directo a Postgres.
 * Variables requeridas: DB_URL, DB_USER, DB_PASSWORD
 */
public class ProductsRepository {

  /* ===========================
     Productos
     =========================== */

  public List<Product> getProducts() throws SQLException {
    return getProducts("", 50, 0);
  }

  public List<Product> getProducts(String search, int limit, int offset) throws SQLException {
    String base = """
        select p.id, p.marca, p.nombre, p.precio,
               c.nombre as categoria,
               b.nombre as nombre_bodega
          from producto p
          join categoria c on p.categoria_id = c.id
          join producto_bodega pb on pb.producto_id = p.id
          join bodega b on pb.bodega_id = b.id
        """;
    String where = (search != null && !search.isBlank())
        ? " where lower(p.nombre) like ? or lower(p.marca) like ? "
        : "";
    String sql = base + where + " order by p.id limit ? offset ?";

    List<Product> products = new ArrayList<>();
    try (Connection cn = conn();
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
          products.add(new Product(
              rs.getObject("id", UUID.class),
              rs.getString("marca"),
              rs.getString("nombre"),
              rs.getInt("precio"),
              rs.getString("categoria"),
              rs.getString("nombre_bodega")
          ));
        }
      }
    }
    return products;
  }

  /* ===========================
     Bodegas
     =========================== */

  public List<Bodega> getWarehouses() throws SQLException {
    return getWarehouses("", 50, 0);
  }

  public List<Bodega> getWarehouses(String search, int limit, int offset) throws SQLException {
    String base = """
        select b.id as bodega_id, b.nombre,
               count(pb.producto_id) as cantidad_productos
          from bodega b
     left join producto_bodega pb on pb.bodega_id = b.id
        """;
    String where = (search != null && !search.isBlank())
        ? " where lower(b.nombre) like ? "
        : "";
    String sql = base + where + " group by b.id, b.nombre order by b.nombre limit ? offset ?";

    List<Bodega> warehouses = new ArrayList<>();
    try (Connection cn = conn();
         PreparedStatement ps = cn.prepareStatement(sql)) {

      int i = 1;
      if (!where.isBlank()) {
        ps.setString(i++, "%" + search.toLowerCase() + "%");
      }
      ps.setInt(i++, limit);
      ps.setInt(i, offset);

      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          warehouses.add(new Bodega(
              rs.getObject("bodega_id", UUID.class),
              rs.getString("nombre"),
              rs.getInt("cantidad_productos")
          ));
        }
      }
    }
    return warehouses;
  }

  /* ===========================
     Connection helper
     =========================== */
  private Connection conn() throws SQLException {
    String url  = getenv("DB_URL");
    String user = getenv("DB_USER");
    String pass = getenv("DB_PASSWORD");
    Properties props = new Properties();
    props.setProperty("user", user);
    props.setProperty("password", pass);
    return DriverManager.getConnection(url, props);
  }

  private static String getenv(String k) {
    String v = System.getenv(k);
    if (v == null || v.isBlank()) v = System.getProperty(k);
    if (v == null || v.isBlank()) throw new IllegalStateException("Falta variable: " + k);
    return v;
  }
}
