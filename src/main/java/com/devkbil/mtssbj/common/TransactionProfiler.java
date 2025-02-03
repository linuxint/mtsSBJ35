package com.devkbil.mtssbj.common;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Component
@Aspect
@Slf4j
public class TransactionProfiler {

    @Before("@annotation(org.springframework.transaction.annotation.Transactional)")
    public void registerTransactionSynchronization() {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {

            @Override
            public void afterCommit() {
                log.info("After commit (global)");
            }

            @Override
            public void beforeCommit(boolean readOnly) {
                log.info("Before commit (global). ReadOnly: {}", readOnly);
            }

            @Override
            public void afterCompletion(int status) {
                log.info("After completion (global). Status: {}", status == TransactionSynchronization.STATUS_COMMITTED ? "COMMITTED" : "ROLLED_BACK");
            }

            @Override
            public void beforeCompletion() {
                log.info("Before completion (global).");
            }

            // 필요 시 추가적으로 구현할 수 있는 메서드들:
            @Override
            public void suspend() {
                log.info("Transaction suspended (global).");
            }

            @Override
            public void resume() {
                log.info("Transaction resumed (global).");
            }
        });
    }
}