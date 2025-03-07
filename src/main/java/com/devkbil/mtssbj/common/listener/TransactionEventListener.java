package com.devkbil.mtssbj.common.listener;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import lombok.extern.slf4j.Slf4j;

/**
 * 트랜잭션 커밋 및 롤백 단계와 관련된 이벤트를 처리하는 Listener 클래스입니다.
 * 이 클래스는 트랜잭션 이벤트를 감지하고 트랜잭션 단계에 따라 특정 로직을 실행합니다.
 * 다양한 트랜잭션 라이프사이클 단계에서의 동작을 정의하기 위해 {@link TransactionalEventListener} 어노테이션을 사용합니다.
 *
 * 이 클래스의 주요 목적은 성공적인 커밋이나 롤백과 같은 트랜잭션 결과를 로깅하는 것입니다.
 *
 * 사용된 어노테이션:
 * 1. {@link Component} - 이 클래스를 Spring에서 관리되는 컴포넌트로 표시하여 DI(의존성 주입)에 사용합니다.
 * 2. {@link Slf4j} - 클래스에 로깅 기능을 제공합니다.
 * 3. {@link TransactionalEventListener} - 특정 트랜잭션 관련 이벤트를 감지할 수 있도록 활성화합니다.
 *
 * 메소드:
 * 1. handleTransactionCommit - 트랜잭션이 성공적으로 커밋된 후 트리거됩니다.
 * 2. handleTransactionRollback - 트랜잭션이 롤백된 후 트리거됩니다.
 */
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
