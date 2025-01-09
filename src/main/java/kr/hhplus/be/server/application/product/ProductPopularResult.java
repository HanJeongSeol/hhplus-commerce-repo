package kr.hhplus.be.server.application.product;

import kr.hhplus.be.server.interfaces.dto.product.ProductPolularItemResponse;
import kr.hhplus.be.server.interfaces.dto.product.ProductPopularResponse;

import java.time.LocalDateTime;
import java.util.List;

public record ProductPopularResult(
        LocalDateTime standardDate,
        List<ProductPopularDetailCommand> products
) {
    public ProductPopularResponse toResponse() {
        List<ProductPolularItemResponse> items = products.stream()
                .map(detail -> new ProductPolularItemResponse(
                        detail.rank(),
                        detail.product().getProductId(),
                        detail.product().getName(),
                        detail.salesCount(),
                        detail.product().getPrice(),
                        detail.product().getStock(),
                        detail.product().getStatus()
                ))
                .toList();

        return new ProductPopularResponse(standardDate, items);
    }
}

