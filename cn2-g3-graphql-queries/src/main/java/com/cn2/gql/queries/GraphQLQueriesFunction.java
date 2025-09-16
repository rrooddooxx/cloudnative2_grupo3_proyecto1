package com.cn2.gql.queries;

import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.*;

import com.cn2.gql.queries.dao.ProductDao;
import com.cn2.gql.queries.dao.BodegaDao;

import java.util.*;
import java.util.stream.Collectors;

public class GraphQLQueriesFunction {
  private static final ObjectMapper MAPPER = new ObjectMapper();
  private static GraphQL graphQL;

  private static synchronized GraphQL graphQL() {
    if (graphQL != null) return graphQL;
    try {
      String sdl = new String(
        GraphQLQueriesFunction.class.getClassLoader()
          .getResourceAsStream("schema.graphqls").readAllBytes()
      );

      TypeDefinitionRegistry registry = new SchemaParser().parse(sdl);
      ProductDao productDao = new ProductDao();
      BodegaDao  bodegaDao  = new BodegaDao();

      RuntimeWiring wiring = RuntimeWiring.newRuntimeWiring()
        .type("Query", builder -> builder
          .dataFetcher("products", env -> {
            String search = env.getArgument("search");
            Integer limit = env.getArgumentOrDefault("limit", 50);
            Integer offset = env.getArgumentOrDefault("offset", 0);
            return productDao.list(search, limit, offset);
          })
          .dataFetcher("product", env -> {
            String id = env.getArgument("id");
            return productDao.getById(id);
          })
          .dataFetcher("bodegas", env -> {
            Integer limit = env.getArgumentOrDefault("limit", 50);
            Integer offset = env.getArgumentOrDefault("offset", 0);
            return bodegaDao.list(limit, offset);
          })
          .dataFetcher("bodega", env -> {
            String id = env.getArgument("id");
            return bodegaDao.getById(id);
          })
        )
        .build();

      GraphQLSchema schema = new SchemaGenerator().makeExecutableSchema(registry, wiring);
      graphQL = GraphQL.newGraphQL(schema).build();
      return graphQL;
    } catch (Exception e) {
      throw new RuntimeException("Error inicializando GraphQL", e);
    }
  }

  public static class GraphQLRequest {
    public String query;
    public Map<String,Object> variables;
    public String operationName;
  }

  @FunctionName("graphql-queries")
  public HttpResponseMessage run(
      @HttpTrigger(name = "req", methods = {HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS,
                   route = "graphql") HttpRequestMessage<Optional<String>> request,
      final ExecutionContext context) {

    try {
      String body = request.getBody().orElse("{}");
      GraphQLRequest gql = MAPPER.readValue(body, GraphQLRequest.class);
      if (gql.query == null || gql.query.isBlank()) {
        return json(request, 400, Map.of("error", "Falta 'query'"));
      }

      ExecutionResult result = graphQL().execute(
        ExecutionInput.newExecutionInput()
          .query(gql.query)
          .operationName(gql.operationName)
          .variables(gql.variables == null ? Map.of() : gql.variables)
          .build()
      );

      Map<String,Object> resp = new LinkedHashMap<>();
      if (result.getData() != null) resp.put("data", result.getData());
      if (!result.getErrors().isEmpty()) {
        resp.put("errors", result.getErrors().stream()
          .map(e -> Map.of("message", e.getMessage())).collect(Collectors.toList()));
        return json(request, 400, resp);
      }
      return json(request, 200, resp);

    } catch (Exception e) {
      context.getLogger().severe("GraphQL error: " + e);
      return json(request, 500, Map.of("error", "Error interno"));
    }
  }

  private HttpResponseMessage json(HttpRequestMessage<?> req, int status, Object body) {
    return req.createResponseBuilder(HttpStatus.valueOf(status))
      .header("Content-Type", "application/json")
      .header("Cache-Control", "no-store")
      .body(body).build();
  }
}
