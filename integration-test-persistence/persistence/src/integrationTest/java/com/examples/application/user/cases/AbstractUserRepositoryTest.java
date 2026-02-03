package com.examples.application.user.cases;

import com.examples.application.user.User;
import com.examples.application.user.UserRepository;
import org.instancio.Instancio;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.instancio.Select.field;

@DataJpaTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
abstract class AbstractUserRepositoryTest {

    protected static final Logger log = LoggerFactory.getLogger(AbstractUserRepositoryTest.class);

    @Autowired
    UserRepository userRepository;

    @Autowired
    TestEntityManager testEntityManager;

    @Autowired
    TransactionTemplate transactionTemplate;

    protected void createAndPersist(String username) {
        User user = Instancio.of(User.class)
                .set(field("id"), null)
                .set(field("username"), username)
                .create();
        log.info("Inserting {}...", username);
        testEntityManager.persistAndFlush(user);
    }

    protected void executeInNewTransaction(Runnable verification) {
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        transactionTemplate.executeWithoutResult(status -> {
            log.info("Executing in new transaction: ");
            verification.run();
        });
    }

    protected void verifyUserExists(String username) {
        log.info("Verifying {} was inserted...", username);
        assertThat(userRepository.findUserByUsername(username)).isNotNull();
    }

    protected void verifyUserDoesNotExists(String username) {
        log.info("Verifying {} not exists...", username);
        assertThat(userRepository.findUserByUsername(username)).isNull();
    }

    protected void delete(String username) {
        log.info("Deleting {}...", username);
        testEntityManager.remove(userRepository.findUserByUsername(username));
    }
}