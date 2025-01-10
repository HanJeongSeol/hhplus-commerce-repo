package kr.hhplus.be.server.application.product;

import kr.hhplus.be.server.domain.product.Product;

public record ProductPopularDetailCommand(
        int rank,
        Product product,
        int salesCount
) {
    public static ProductPopularDetailCommand of(Product product, int rank, int salesCount) {
        return new ProductPopularDetailCommand(rank, product, salesCount);
    }
}
