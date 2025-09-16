package com.cn2g4.graphql1.resolver;

import com.cn2g4.graphql1.model.input.InputNewProduct;
import com.cn2g4.graphql1.model.input.InputUpdateProductPrice;
import com.cn2g4.graphql1.model.response.AddProductResponse;
import com.cn2g4.graphql1.model.response.UpdateProductResponse;
import com.cn2g4.graphql1.product.model.Product;
import com.cn2g4.graphql1.product.repository.ProductsRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.schema.DataFetcher;

import java.util.Optional;
import java.util.UUID;

public class MutationResolver {

    private final ProductsRepository productsRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public MutationResolver(ProductsRepository productsRepository) {
        this.productsRepository = productsRepository;
    }

    public DataFetcher<AddProductResponse> addProductDataFetcher() {
        return environment -> {
            try {
                InputNewProduct input = objectMapper.convertValue(environment.getArgument("product"), InputNewProduct.class);
                Product newProduct = new Product(null, input.marca(), input.nombreProducto(), input.precio(), input.categoria(), null);

                Optional<UUID> addedProductId = productsRepository.addProduct(newProduct, input.bodegaId());

                if (addedProductId.isPresent()) {
                    return new AddProductResponse(addedProductId.get().toString(), null);
                } else {
                    return new AddProductResponse(null, "No ID for added product. Check database");
                }
            } catch (Exception ex) {
                return new AddProductResponse(null, "Error adding product: " + ex.getMessage());
            }
        };
    }

    public DataFetcher<UpdateProductResponse> updateProductPriceDataFetcher() {
        return environment -> {
            try {
                InputUpdateProductPrice input = objectMapper.convertValue(environment.getArgument("input"), InputUpdateProductPrice.class);
                boolean wasUpdated = productsRepository.updateProductPriceById(input.id(), input.precio());
                return new UpdateProductResponse(wasUpdated, wasUpdated ? null : "Product not found or update failed");
            } catch (Exception ex) {
                return new UpdateProductResponse(false, "Error updating product: " + ex.getMessage());
            }
        };
    }
}
