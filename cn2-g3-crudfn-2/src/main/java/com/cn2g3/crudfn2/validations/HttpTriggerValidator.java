package com.cn2g3.crudfn2.validations;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import java.util.Optional;

public class HttpTriggerValidator {

  private HttpTriggerValidator() {}

  public static HttpResponseMessage.Builder validate(
      HttpRequestMessage<Optional<String>> request, ExecutionContext context, String action) {
    context
        .getLogger()
        .info("Add or Update Product | Starting request... Action: %s.".formatted(action));

    if (!action.equals("add") && !action.equals("update")) {
      context.getLogger().severe("Add or Update Product | Invalid Action: %s".formatted(action));
      return request.createResponseBuilder(HttpStatus.BAD_REQUEST);
    }

    if (request.getBody().isEmpty()) {
      context
          .getLogger()
          .severe(
              "Add or Update Product | ERROR! Action: (%s) with no request body, Aborting..."
                  .formatted(action));
      return request.createResponseBuilder(HttpStatus.BAD_REQUEST);
    }
    return null;
  }
}
