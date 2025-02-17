package kr.hhplus.be.server.infra.platform.listener;

import kr.hhplus.be.server.application.port.out.DataPlatformService;
import kr.hhplus.be.server.infra.platform.CompletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventListener {
    private final DataPlatformService dataPlatformService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePaymentCompletedEvent(CompletedEvent.PaymentCompletedEvent event){
        try{
            log.info("결제 완료 이벤트 수신 확인 - paymentId: {}, orderId: {}", event.paymentId(), event.orderId());

            dataPlatformService.sendPaymentData(event);
        } catch (Exception e){
            log.error("결제 데이터 처리 중 오류 발생", e);
        }
    }
}
