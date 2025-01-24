package com.devkbil.mtssbj.common.Listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TransactionEventListener {

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleTransactionCommit(Object event) {
        // 트랜잭션 성공 시 호출
        log.info("Transaction successfully committed!" + event);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void handleTransactionRollback(Object event) {
        // 트랜잭션 실패(롤백) 시 호출
        log.info("Transaction rolled back! Details: " + event);
    }
}
