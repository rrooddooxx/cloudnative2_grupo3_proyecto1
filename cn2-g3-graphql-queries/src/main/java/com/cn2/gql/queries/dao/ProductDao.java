package com.cn2.gql.queries.dao;

import com.cn2.gql.queries.db.Db;
import com.cn2.gql.queries.model.Product;

import java.sql.*;
import java.util.*;

public class ProductDao {

  public List<Product> list(String search, int limit, int offset) throws SQLException {
    String base = "SELECT id, nombre, precio FROM producto";
    String where = (search != null && !search.isBlank()) ? " WHERE LOWER(nombre) LIKE ?" : "";
    String sql = base + where + " ORDER BY id LIMIT ? OFFSET ?";

    try (PreparedStatement ps = Db.conn().prepareStatement(sql)) {
      int i = 1;
      if (!where.isBlank()) {
        ps.setString(i++, "%" + search.toLowerCase() + "%");
      }
      ps.setInt(i++, limit);
      ps.setInt(i, offset);

      List<Product> out = new ArrayList<>();
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          Product p = new Product();
          p.id = rs.getObject("id").toString();
          p.nombre = rs.getString("nombre");
          p.precio = rs.getDouble("precio");
          out.add(p);
        }
      }
      return out;
    }
  }

  public Product getById(String id) throws SQLException {
    String sql = "SELECT id, nombre, precio FROM producto WHERE id = ?";
    try (PreparedStatement ps = Db.conn().prepareStatement(sql)) {
      ps.setObject(1, java.util.UUID.fromString(id));
      try (ResultSet rs = ps.executeQuery()) {
        if (!rs.next()) return null;
        Product p = new Product();
        p.id = rs.getObject("id").toString();
        p.nombre = rs.getString("nombre");
        p.precio = rs.getDouble("precio");
        return p;
      }
    }
  }
}
