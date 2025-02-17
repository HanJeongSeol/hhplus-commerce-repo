package kr.hhplus.be.server.infra.platform;

import kr.hhplus.be.server.application.port.out.DataPlatformService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DataPlatformServiceImpl implements DataPlatformService {
    @Override
    public void sendPaymentData(CompletedEvent.PaymentCompletedEvent event) {
        log.info("데이터 플랫폼 전송 확인 - Payment Id: {}", event.paymentId());
    }
}
