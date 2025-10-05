package com.cn2g3.crudfn2;

import com.azure.core.credential.AzureKeyCredential;
import com.azure.core.util.BinaryData;
import com.azure.messaging.eventgrid.EventGridEvent;
import com.azure.messaging.eventgrid.EventGridPublisherClient;
import com.azure.messaging.eventgrid.EventGridPublisherClientBuilder;
import com.cn2g3.crudfn2.product.model.NewProductDto;
import com.cn2g3.crudfn2.product.model.UpdateProductProductPriceDto;
import java.util.Map;

public class ProductEventProducer {

  private ProductEventProducer() {}

  public static void produceProductAddedEvent(NewProductDto dto) {
    var credentials = getProductAddedCredentials();
    EventGridPublisherClient<EventGridEvent> eventGridEventClient =
        new EventGridPublisherClientBuilder()
            .endpoint(credentials.get(ProductEventProperties.ENDPOINT))
            .credential(new AzureKeyCredential(credentials.get(ProductEventProperties.KEY)))
            .buildEventGridEventPublisherClient();

    EventGridEvent event =
        new EventGridEvent(
            "/EventGridEvents/example/source",
            "Product.AddProductEvent",
            BinaryData.fromObject(dto),
            "1.0");

    eventGridEventClient.sendEvent(event);
  }

  public static void produceProductUpdatedEvent(UpdateProductProductPriceDto dto) {
    var credentials = getProductUpdatedCredentials();
    EventGridPublisherClient<EventGridEvent> eventGridEventClient =
        new EventGridPublisherClientBuilder()
            .endpoint(credentials.get(ProductEventProperties.ENDPOINT))
            .credential(new AzureKeyCredential(credentials.get(ProductEventProperties.KEY)))
            .buildEventGridEventPublisherClient();

    EventGridEvent event =
        new EventGridEvent(
            "/EventGridEvents/example/source",
            "Product.UpdateProductEvent",
            BinaryData.fromObject(dto),
            "1.0");

    eventGridEventClient.sendEvent(event);
  }

  private static Map<ProductEventProperties, String> getProductAddedCredentials() {
    var eventGridTopicEndpoint =
        "https://productoagregado.eastus2-1.eventgrid.azure.net/api/events";
    var eventGridTopicKey =
        "5UdIvmNlNOeLbqvyWkgqVOg2Ej452bE5c6gmR5CVdIZTONfeqE4vJQQJ99BJACHYHv6XJ3w3AAABAZEGr2Fm";

    return Map.of(
        ProductEventProperties.ENDPOINT,
        eventGridTopicEndpoint,
        ProductEventProperties.KEY,
        eventGridTopicKey);
  }

  private static Map<ProductEventProperties, String> getProductUpdatedCredentials() {
    var eventGridTopicEndpoint =
        "https://productoactualizado.eastus2-1.eventgrid.azure.net/api/events";
    var eventGridTopicKey =
        "3VfP52kg95r8LJds1lY1TGJ2dGi2H0CdJlHBs4lzZ3MIukmw8gINJQQJ99BJACHYHv6XJ3w3AAABAZEGWOLi";

    return Map.of(
        ProductEventProperties.ENDPOINT,
        eventGridTopicEndpoint,
        ProductEventProperties.KEY,
        eventGridTopicKey);
  }
}
