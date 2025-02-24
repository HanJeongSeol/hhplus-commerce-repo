package kr.hhplus.be.server.domain.payment;

import jakarta.persistence.*;
import kr.hhplus.be.server.support.constant.PaymentEventStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name="payment_outbox_event")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentOutboxEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String eventType;

    @Lob
    private String payload;

    @Enumerated(EnumType.STRING)
    private PaymentEventStatus status;

    private LocalDateTime createdAt;

    public static PaymentOutboxEvent create(String eventType, String payload) {
        return PaymentOutboxEvent.builder()
                .eventType(eventType)
                .payload(payload)
                .status(PaymentEventStatus.UNSENT)
                .createdAt(LocalDateTime.now())
                .build();
    }
    public void markAsSent() {
        this.status = PaymentEventStatus.SENT;
    }
}
