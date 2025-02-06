package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.application.product.response.ProductResult;
import kr.hhplus.be.server.domain.product.dto.ProductInfo;
import kr.hhplus.be.server.support.constant.ErrorCode;
import kr.hhplus.be.server.support.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductCacheRepository productCacheRepository;

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
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND,productId));

        return ProductInfo.ProductDetail.from(product);
    }

    /**
     * 상품 상세 조회
     */
    @Transactional
    public ProductInfo.ProductDetail getProductByIdWithLock(Long productId){
        Product product =  productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND,productId));

        return ProductInfo.ProductDetail.from(product);
    }

    @Transactional
    public void decreaseProductStock(Long productId, int quantity){
        Product product = productRepository.findByIdWithLock(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND,productId));

        product.decreaseStock(quantity);

        productRepository.save(product);

    }

    @Transactional
    public List<ProductInfo.ProductPopularList> getPopularProducts() {
        List<ProductInfo.ProductPopularQueryDto> queryResults = productRepository.findPopularProducts();


        // 인기상품이 존재하지 않는 경우 빈 리스트 반환하도록 수정
        if(queryResults.isEmpty()){
            log.info("인기 상품이 존재하지 않습니다.");
            return Collections.emptyList();
        }
//        if(queryResults.isEmpty()){
//            throw new BusinessException(ErrorCode.PRODUCT_NOT_FOUND);
//        }

        return IntStream.range(0, queryResults.size())
                .mapToObj(i -> ProductInfo.ProductPopularList.from(queryResults.get(i), i + 1))
                .collect(Collectors.toList());
    }

    public List<ProductResult.ProductPopularResult> getPopularProductsByRedis(){
        // 1. 캐시에서 조회
        List<ProductResult.ProductPopularResult> cachedProducts = productCacheRepository.getPopularProducts();

        if(!cachedProducts.isEmpty()){
            return cachedProducts;
        }

        // 2. 데이터베이스 조회
        // DB에서 조회, 빈 리스트여도 캐시에 저장
        List<ProductResult.ProductPopularResult> products =
                getPopularProducts().stream()
                        .map(ProductResult.ProductPopularResult::from)
                        .collect(Collectors.toList());

        // 캐시 미스 방지 -> 빈 리스트도 캐시에 저장되도록
        productCacheRepository.savePopularProducts(products);

        return products;

    }
}
