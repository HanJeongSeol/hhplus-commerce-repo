package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.infra.product.ProductJpaRepository;
import kr.hhplus.be.server.support.constant.ProductStatus;
import kr.hhplus.be.server.support.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;


@Slf4j
@SpringBootTest
@DisplayName("상품 서비스 동시성 테스트")
public class ProductConcurrencyTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductJpaRepository productJpaRepository;

    @Autowired
    private RedissonClient redissonClient;

    private Long productId;
    private final Integer CONCURRENT_COUNT = 900;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        // 기존 데이터 삭제
        productJpaRepository.deleteAll();

        // 테스트용 상품 생성
        Product product = Product.builder()
                .name("테스트 상품")
                .price(10000L)
                .stock(1000)
                .status(ProductStatus.ON_SALE)
                .build();
        product = productJpaRepository.save(product);
        productId = product.getProductId();
        log.info("테스트 상품 생성: {}", product);
    }
    @AfterEach
    public void tearDown() {
        // 테스트 후 데이터 정리
        productJpaRepository.deleteAll();
        log.info("테스트 데이터 삭제 완료");
    }

    /**
     * 동시성 테스트를 수행하는 헬퍼 메서드
     *
     * @param action 재고 감소 로직을 수행하는 Consumer
     * @throws InterruptedException 스레드 대기 중 인터럽트 발생 시
     */
    private void concurrentTest(Consumer<Void> action) throws InterruptedException {
        // 원래 재고 조회
        Integer originStock = productJpaRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."))
                .getStock();
        log.info("원래 재고: {}", originStock);

        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(CONCURRENT_COUNT);

        for (int i = 0; i < CONCURRENT_COUNT; i++) {
            executorService.submit(() -> {
                try {
                    action.accept(null);
                } catch (BusinessException e) {
                    log.error("BusinessException 발생: {}", e.getMessage());
                } catch (Exception e) {
                    log.error("예외 발생: ", e);
                } finally {
                    latch.countDown();
                }
            });
        }

        // 모든 스레드가 완료될 때까지 대기
        latch.await();
        executorService.shutdown();

        // 재고 최종 상태 조회
        Product updatedProduct = productJpaRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));
        log.info("최종 재고: {}", updatedProduct.getStock());

        // 재고 감소 검증
        assertEquals(originStock - CONCURRENT_COUNT, updatedProduct.getStock(),
                "재고가 예상과 다르게 감소했습니다.");
    }
    @Test
    @DisplayName("동시에 100명의 재고 감소: 분산락 사용 전 (동시성 이슈)")
    public void fdfdegbadDecreaseStockTest() throws Exception {
        concurrentTest((_no) -> productService.decreaseProductStockNotConcurrency(productId, 1));
    }

    @Test
    @DisplayName("동시에 100명의 재고 감소: Redis 분산락 사용")
    public void decreaseStockWithRedisLockTest() throws Exception {
        concurrentTest((_no) -> productService.decreaseProductStockRedis(productId, 1));
    }

    @Test
    @DisplayName("동시에 100명의 재고 감소: Redis 분산락 사용")
    public void decreaseStockWithRedisLockAnnotationTest() throws Exception {
        concurrentTest((_no) -> productService.decreaseProductStockRedisByAnnotation(productId, 1));
    }

}
