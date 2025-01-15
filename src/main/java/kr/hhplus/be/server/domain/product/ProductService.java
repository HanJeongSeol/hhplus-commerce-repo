package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.domain.product.dto.ProductInfo;
import kr.hhplus.be.server.support.constant.ErrorCode;
import kr.hhplus.be.server.support.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    @Transactional
    public List<Product> getAllProducts() {
        List<Product> products = productRepository.findAll();
        if(products.isEmpty()){
            throw new BusinessException(ErrorCode.PRODUCT_NOT_FOUND);
        }
        return products;
    }


    /**
     * 페이징 처리된 상품 목록
     */
    @Transactional
    public Page<ProductInfo.ProductDetail> getAllProductPage(int page, int size){
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Product> products = productRepository.findAll(pageable);
        if(products.isEmpty()){
            throw new BusinessException(ErrorCode.PRODUCT_NOT_FOUND);
        }

        return products.map(ProductInfo.ProductDetail::from);
    }

    @Transactional(readOnly = true)
    public ProductInfo.ProductDetail getProductById(Long productId){

        Product product =  productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

        return ProductInfo.ProductDetail.from(product);
    }

    /**
     * 상품 상세 조회
     */
    @Transactional
    public ProductInfo.ProductDetail getProductByIdWithLock(Long productId){
        Product product =  productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

        return ProductInfo.ProductDetail.from(product);
    }

    @Transactional
    public void decreaseProductStock(Long productId, int quantity){
        Product product = productRepository.findByIdWithLock(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

        product.decreaseStock(quantity);

        productRepository.save(product);

    }

    @Transactional
    public List<ProductInfo.ProductPopularList> getPopularProducts() {
        List<ProductInfo.ProductPopularQueryDto> queryResults = productRepository.findPopularProducts();

        if(queryResults.isEmpty()){
            throw new BusinessException(ErrorCode.PRODUCT_NOT_FOUND);
        }

        return IntStream.range(0, queryResults.size())
                .mapToObj(i -> ProductInfo.ProductPopularList.from(queryResults.get(i), i + 1))
                .collect(Collectors.toList());
    }
}
