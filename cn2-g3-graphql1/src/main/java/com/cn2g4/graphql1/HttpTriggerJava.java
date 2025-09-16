package com.cn2g4.graphql1;

import com.cn2g4.graphql1.config.GraphQLProvider;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import java.util.Map;
import java.util.Optional;

public class HttpTriggerJava {

  private static final GraphQL graphQL = GraphQLProvider.get();
  private static final ObjectMapper objectMapper = new ObjectMapper();

  @FunctionName("graphql")
  public HttpResponseMessage run(
      @HttpTrigger(
              name = "req",
              methods = {HttpMethod.POST},
              authLevel = AuthorizationLevel.ANONYMOUS,
              route = "graphql")
          HttpRequestMessage<Optional<String>> request,
      final ExecutionContext context) {
    context.getLogger().info("Processing GraphQL request.");

    String requestBody = request.getBody().orElse(null);
    if (requestBody == null || requestBody.isEmpty()) {
      return request
          .createResponseBuilder(HttpStatus.BAD_REQUEST)
          .body("Request body is empty. Please provide a GraphQL query.")
          .build();
    }

    try {
      Map<String, Object> bodyMap = objectMapper.readValue(requestBody, new TypeReference<>() {});
      String query = (String) bodyMap.get("query");
      Map<String, Object> variables = (Map<String, Object>) bodyMap.get("variables");

      ExecutionInput.Builder executionInputBuilder =
          ExecutionInput.newExecutionInput().query(query);
      if (variables != null) {
        executionInputBuilder.variables(variables);
      }
      ExecutionInput executionInput = executionInputBuilder.build();

      ExecutionResult executionResult = graphQL.execute(executionInput);

      return request
          .createResponseBuilder(HttpStatus.OK)
          .header("Content-Type", "application/json")
          .body(objectMapper.writeValueAsString(executionResult.toSpecification()))
          .build();

    } catch (Exception ex) {
      context.getLogger().severe("Error processing GraphQL request: " + ex.getMessage());
      return request
          .createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Error executing GraphQL query. Check logs for details.")
          .build();
    }
  }
}
