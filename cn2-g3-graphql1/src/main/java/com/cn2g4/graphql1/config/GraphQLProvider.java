package com.cn2g4.graphql1.config;

import com.cn2g4.graphql1.product.repository.ProductsRepository;
import com.cn2g4.graphql1.resolver.MutationResolver;
import com.cn2g4.graphql1.resolver.ProductsQueryResolver;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

public class GraphQLProvider {

    private static final GraphQL graphQL;

    static {
        GraphQL tempGraphQL = null;
        try {
            ProductsRepository productsRepository = new ProductsRepository();
            ProductsQueryResolver queryResolver = new ProductsQueryResolver(productsRepository);
            MutationResolver mutationResolver = new MutationResolver(productsRepository);

            String schema = loadSchema("schema.graphqls");
            TypeDefinitionRegistry typeRegistry = new SchemaParser().parse(schema);

            RuntimeWiring runtimeWiring = RuntimeWiring.newRuntimeWiring()
                    .type("Query", builder -> builder.dataFetcher("products", queryResolver.productsDataFetcher()))
                    .type("Mutation", builder -> builder
                            .dataFetcher("addProduct", mutationResolver.addProductDataFetcher())
                            .dataFetcher("updateProductPrice", mutationResolver.updateProductPriceDataFetcher()))
                    .build();

            GraphQLSchema graphQLSchema = new SchemaGenerator().makeExecutableSchema(typeRegistry, runtimeWiring);
            tempGraphQL = GraphQL.newGraphQL(graphQLSchema).build();
        } catch (Throwable t) {
            System.err.println("FATAL: Failed to initialize GraphQL instance.");
            t.printStackTrace(System.err);
        }
        graphQL = tempGraphQL;
    }

    public static GraphQL get() {
        if (graphQL == null) {
            throw new IllegalStateException("GraphQL instance could not be initialized. Check logs for startup errors.");
        }
        return graphQL;
    }

    private static String loadSchema(String name) {
        try (InputStream inputStream = GraphQLProvider.class.getClassLoader().getResourceAsStream(name);
             Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            char[] buffer = new char[4096];
            StringBuilder stringBuilder = new StringBuilder();
            int numRead;
            while ((numRead = reader.read(buffer, 0, buffer.length)) != -1) {
                stringBuilder.append(buffer, 0, numRead);
            }
            return stringBuilder.toString();
        } catch (IOException | NullPointerException e) {
            throw new IllegalStateException("Cannot load schema from classpath: " + name, e);
        }
    }
}
