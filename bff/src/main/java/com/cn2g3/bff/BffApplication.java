package com.cn2g3.bff;

import com.cn2g3.bff.config.AzureFunctionsProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({AzureFunctionsProperties.class})
public class BffApplication {

  public static void main(String[] args) {
    SpringApplication.run(BffApplication.class, args);
  }
}
