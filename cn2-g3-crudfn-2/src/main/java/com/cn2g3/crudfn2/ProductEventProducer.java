package com.cn2g3.crudfn2;

import com.azure.core.credential.AzureKeyCredential;
import com.azure.core.util.BinaryData;
import com.azure.messaging.eventgrid.EventGridEvent;
import com.azure.messaging.eventgrid.EventGridPublisherClient;
import com.azure.messaging.eventgrid.EventGridPublisherClientBuilder;
import com.cn2g3.crudfn2.product.model.NewProductDto;
import com.cn2g3.crudfn2.product.model.UpdateProductProductPriceDto;
import java.io.IOException;
import java.util.Map;

public class ProductEventProducer {

  private ProductEventProducer() {}

  public static void produceProductAddedEvent(NewProductDto dto) throws IOException {
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
    System.out.println("EVENTO ENVIADO!! %s".formatted(event.toJsonString()));
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
    return Map.of(
        ProductEventProperties.ENDPOINT,
        System.getenv("TopicProductAddedEndpoint"),
        ProductEventProperties.KEY,
        System.getenv("TopicProductAddedKey"));
  }

  private static Map<ProductEventProperties, String> getProductUpdatedCredentials() {
    return Map.of(
        ProductEventProperties.ENDPOINT,
        System.getenv("TopicProductUpdatedEndpoint"),
        ProductEventProperties.KEY,
        System.getenv("TopicProductUpdatedKey"));
  }
}
