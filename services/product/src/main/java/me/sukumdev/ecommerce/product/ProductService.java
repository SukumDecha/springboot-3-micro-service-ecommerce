package me.sukumdev.ecommerce.product;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.sukumdev.ecommerce.exception.ProductPurchaseException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository repository;
    private final ProductMapper mapper;

    public Integer createProduct(@Valid ProductRequest productRequest) {
        var product = mapper.toProduct(productRequest);

        return repository.save(product).getId();
    }

    public List<ProductPurchaseResponse> purchaseProducts(List<ProductPurchaseRequest> requests) {
        var productIds = requests
                .stream()
                .map(ProductPurchaseRequest::productId)
                .toList();

        var storedProducts = repository.findAllByIdInOrderById(productIds);
        if(productIds.size() != storedProducts.size()) {
            throw new ProductPurchaseException("One or more products not found");
        }
        var storedRequest = requests.stream().sorted(Comparator.comparing(
                ProductPurchaseRequest::productId
        )).toList();
        var purchasedProducts = new ArrayList<ProductPurchaseResponse>();
        for(int i = 0; i < storedRequest.size(); i++) {
            var product = storedProducts.get(i);
            var productRequest = storedRequest.get(i);
            if(product.getAvailableQuantity() < productRequest.quantity()) {
                throw new ProductPurchaseException("Insufficient quantity for the product with ID:: " + product.getId());
            }
            var newQuantity = product.getAvailableQuantity() - productRequest.quantity();
            product.setAvailableQuantity(newQuantity);
            repository.save(product);

            purchasedProducts.add(mapper.toProductPurchaseResponse(product, productRequest.quantity()));
        }
        return purchasedProducts;
    }

    public ProductResponse findById(Integer productId) {
        return repository.findById(productId)
                .map(mapper::toProductResponse)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with the ID:: " + productId));
    }

    public List<ProductResponse> findAll() {
        return repository.findAll().stream().map(
                mapper::toProductResponse
        ).collect(Collectors.toList());
    }

}
