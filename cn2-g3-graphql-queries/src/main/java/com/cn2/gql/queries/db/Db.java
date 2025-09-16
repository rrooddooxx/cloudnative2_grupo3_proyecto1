package com.cn2.gql.queries.db;

import java.sql.*;
import java.util.Properties;

public class Db {
  private static Connection cached;

  public static Connection conn() throws SQLException {
    if (cached != null && !cached.isClosed()) return cached;
    String url  = getenv("DB_URL");
    String user = getenv("DB_USER");
    String pass = getenv("DB_PASSWORD");
    Properties props = new Properties();
    props.setProperty("user", user);
    props.setProperty("password", pass);
    cached = DriverManager.getConnection(url, props);
    return cached;
  }

  private static String getenv(String k) {
    String v = System.getenv(k);
    if (v == null || v.isBlank()) v = System.getProperty(k);
    if (v == null || v.isBlank()) throw new IllegalStateException("Falta variable: " + k);
    return v;
  }
}
