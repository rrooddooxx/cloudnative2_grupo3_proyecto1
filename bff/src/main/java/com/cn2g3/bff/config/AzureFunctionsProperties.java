package com.cn2g3.bff.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "azure-functions")
@Data
public class AzureFunctionsProperties {
  private String fn1Host;
  private String fn2Host;
  private String fn3Host;
}
