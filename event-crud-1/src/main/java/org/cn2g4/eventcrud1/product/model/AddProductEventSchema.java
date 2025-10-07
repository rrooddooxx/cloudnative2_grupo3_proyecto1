package org.cn2g4.eventcrud1.product.model;

import java.util.Date;

public record AddProductEventSchema(
    String topic,
    String subject,
    String eventType,
    Date eventTime,
    String id,
    String dataVersion,
    String metadataVersion,
    NewProductDto data) {}
