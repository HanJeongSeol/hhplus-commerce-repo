package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.config.redis.annotation.RedissonLock;
import kr.hhplus.be.server.domain.product.dto.ProductInfo;
import kr.hhplus.be.server.support.constant.ErrorCode;
import kr.hhplus.be.server.support.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final RedissonClient redissonClient;
    private static final String PRODUCT_STOCK_LOCK_PREFIX = "product:stock:lock:";
    private static final long WAIT_TIME = 10L;
    private static final long LEASE_TIME = 5L;

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
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_LOCK_ACQUISITION_FAILED,productId));

        product.decreaseStock(quantity);

        productRepository.save(product);

    }
    @Transactional
    public void decreaseProductStockNotConcurrency(Long productId, int quantity){
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_LOCK_ACQUISITION_FAILED,productId));

        product.decreaseStock(quantity);

        productRepository.save(product);

    }
    @Transactional
    public void decreaseProductStockRedis(Long productId, int quantity) {

        RLock lock = redissonClient.getLock(PRODUCT_STOCK_LOCK_PREFIX + productId);

        try {
            log.info("스레드 {}: 락 '{}' 시도 중...", Thread.currentThread().getName(), lock);

            if (!lock.tryLock(WAIT_TIME, LEASE_TIME, TimeUnit.SECONDS)) {
                log.warn("스레드 {}: 락 '{}' 획득 실패", Thread.currentThread().getName(), lock);
                throw new BusinessException(ErrorCode.PRODUCT_LOCK_ACQUISITION_FAILED, productId);
            }
            log.info("스레드 {}: 락 '{}' 획득 성공", Thread.currentThread().getName(), lock);

            // 트랜잭션 내에서 상품 조회
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND, productId));

            // 재고 감소
            product.decreaseStock(quantity);

            // 변경사항 즉시 DB에 반영
            productRepository.save(product);

        } catch (InterruptedException e) {
            log.error("스레드 {}: 락 획득 중 인터럽트 발생", Thread.currentThread().getName(), e);
            Thread.currentThread().interrupt();
            throw new BusinessException(ErrorCode.PRODUCT_LOCK_ACQUISITION_FAILED, productId);
        } finally {
            // 락 해제
            log.info("스레드 {}: 락 '{}' 해제", Thread.currentThread().getName(), lock);
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

//    @Transactional
    @RedissonLock(value = "#productId")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void decreaseProductStockRedisByAnnotation(Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_LOCK_ACQUISITION_FAILED,productId));
        log.info(">>> 재고 차감 로직 실행 productId={}, quantity={}", productId, quantity);
        product.decreaseStock(quantity);

        productRepository.save(product);
    }
    // 예외 발생 시 보상 트랜잭션 재고 복원을 위한 작업
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void restoreStockNewTx(Long productId, int quantity){
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

        // 재고 복원 (원복)
        product.increaseStock(quantity);

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
