package kr.hhplus.be.server.infra.platform.publisher;

import kr.hhplus.be.server.infra.platform.CompletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventPublisher {
    private final ApplicationEventPublisher eventPublisher;

    public void publishPaymentCompleted(CompletedEvent.PaymentCompletedEvent event){
        log.info("결제 완료 이벤트 발행 - paymentId: {}", event.paymentId());
        eventPublisher.publishEvent(event);
    }
}
