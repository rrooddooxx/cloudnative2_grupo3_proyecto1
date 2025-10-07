package org.cn2g3.crudfn3;

import com.azure.core.credential.AzureKeyCredential;
import com.azure.core.util.BinaryData;
import com.azure.messaging.eventgrid.EventGridEvent;
import com.azure.messaging.eventgrid.EventGridPublisherClient;
import com.azure.messaging.eventgrid.EventGridPublisherClientBuilder;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public class ProductEventProducer {

  private ProductEventProducer() {}

  public static void produceProductDeletedEvent(String id) throws IOException {
    var credentials = getProductDeletedCredentials();
    EventGridPublisherClient<EventGridEvent> eventGridEventClient =
        new EventGridPublisherClientBuilder()
            .endpoint(credentials.get(ProductEventProperties.ENDPOINT))
            .credential(new AzureKeyCredential(credentials.get(ProductEventProperties.KEY)))
            .buildEventGridEventPublisherClient();

    EventGridEvent event =
        new EventGridEvent(
            "/EventGridEvents/example/source",
            "Product.DeleteProductEvent",
            BinaryData.fromObject(UUID.fromString(id)),
            "1.0");

    eventGridEventClient.sendEvent(event);
    System.out.println("EVENTO ENVIADO!! %s".formatted(event.toJsonString()));
  }

  private static Map<ProductEventProperties, String> getProductDeletedCredentials() {
    return Map.of(
        ProductEventProperties.ENDPOINT,
        System.getenv("TopicProductDeletedEndpoint"),
        ProductEventProperties.KEY,
        System.getenv("TopicProductDeletedKey"));
  }
}
