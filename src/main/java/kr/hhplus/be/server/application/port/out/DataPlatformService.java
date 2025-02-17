package kr.hhplus.be.server.application.port.out;

import kr.hhplus.be.server.infra.platform.CompletedEvent;

public interface DataPlatformService {
    void sendPaymentData(CompletedEvent.PaymentCompletedEvent event);
}
