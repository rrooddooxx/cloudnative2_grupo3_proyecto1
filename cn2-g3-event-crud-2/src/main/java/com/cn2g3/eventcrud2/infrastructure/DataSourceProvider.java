package com.cn2g3.eventcrud2.infrastructure;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;

public final class DataSourceProvider {
  private static volatile HikariDataSource hikariDataSource;

  private DataSourceProvider() {}

  public static DataSource get() {
    HikariDataSource localDataSource = hikariDataSource;
    if (localDataSource == null) {
      synchronized (DataSourceProvider.class) {
        localDataSource = hikariDataSource;
        if (localDataSource == null) {
          HikariConfig hikariConfig = new HikariConfig();
          String host = System.getenv("PostgresSupabaseCnxHost");
          String port = System.getenv("PostgresSupabaseCnxPort");
          String user = System.getenv("PostgresSupabaseCnxUser");
          String pass = System.getenv("PostgresSupabaseCnxPass");
          String cnxUrl =
              String.format("jdbc:postgresql://%s:%s/postgres?sslmode=require", host, port);

          hikariConfig.setJdbcUrl(cnxUrl);
          hikariConfig.setUsername(user);
          hikariConfig.setPassword(pass);
          hikariConfig.setDriverClassName("org.postgresql.Driver");

          hikariConfig.setMaximumPoolSize(
              Integer.parseInt(System.getenv().getOrDefault("DB_POOL_MAX", "5")));
          hikariConfig.setMinimumIdle(
              Integer.parseInt(System.getenv().getOrDefault("DB_POOL_MIN_IDLE", "0")));
          hikariConfig.setConnectionTimeout(10_000);
          hikariConfig.setIdleTimeout(60_000);
          hikariConfig.setMaxLifetime(600_000);
          hikariConfig.setAutoCommit(true);

          hikariDataSource = localDataSource = new HikariDataSource(hikariConfig);
        }
      }
    }
    return localDataSource;
  }
}
