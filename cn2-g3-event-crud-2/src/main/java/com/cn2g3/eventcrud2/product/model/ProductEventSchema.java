package com.cn2g3.eventcrud2.product.model;

import java.util.Date;
import java.util.UUID;

public record ProductEventSchema(
    String topic,
    String subject,
    String eventType,
    Date eventTime,
    String id,
    String dataVersion,
    String metadataVersion,
    UUID data) {}
