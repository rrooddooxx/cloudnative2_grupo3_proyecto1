package com.cn2.gql.queries.dao;

import com.cn2.gql.queries.db.Db;
import com.cn2.gql.queries.model.Bodega;

import java.sql.*;
import java.util.*;

public class BodegaDao {

  public List<Bodega> list(int limit, int offset) throws SQLException {
    String sql = "SELECT id, nombre FROM bodega ORDER BY id LIMIT ? OFFSET ?";
    try (PreparedStatement ps = Db.conn().prepareStatement(sql)) {
      ps.setInt(1, limit);
      ps.setInt(2, offset);

      List<Bodega> out = new ArrayList<>();
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          Bodega b = new Bodega();
          b.id = rs.getObject("id").toString();
          b.nombre = rs.getString("nombre");
          out.add(b);
        }
      }
      return out;
    }
  }

  public Bodega getById(String id) throws SQLException {
    String sql = "SELECT id, nombre FROM bodega WHERE id = ?";
    try (PreparedStatement ps = Db.conn().prepareStatement(sql)) {
      ps.setObject(1, java.util.UUID.fromString(id));
      try (ResultSet rs = ps.executeQuery()) {
        if (!rs.next()) return null;
        Bodega b = new Bodega();
        b.id = rs.getObject("id").toString();
        b.nombre = rs.getString("nombre");
        return b;
      }
    }
  }
}
