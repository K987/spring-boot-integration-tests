package com.examples.application.user;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jdbc.test.autoconfigure.DataJdbcTest;
import org.springframework.transaction.IllegalTransactionStateException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJdbcTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PropagationTest {

    private static final String COMMIT = "u_commit";
    private static final String ROLLBACK = "u_rollback";

    @Autowired
    private TransactionalTestUserService userService;

    @Test
    @Order(1)
    void createInNewTransaction() {
        User user = UserUtil.createUser(COMMIT);
        userService.createWithCommit(user);

        user = userService.fetchUserByUserName(COMMIT);
        assertThat(user).isNotNull();
        assertThat(user.getId()).isNotNull();
    }

    @Test
    @Order(2)
    void createInSameTransaction() {
        User user = UserUtil.createUser(ROLLBACK);
        userService.create(user);

        user = userService.fetchUserByUserName(ROLLBACK);
        assertThat(user).isNotNull();
        assertThat(user.getId()).isNotNull();
    }

    @Test
    @Order(3)
    void verifyCommit() {
        User user = userService.fetchUserByUserName(ROLLBACK);
        assertThat(user).isNull();

        user = userService.fetchUserByUserName(COMMIT);
        assertThat(user).isNotNull();
    }

    @Test
    void propagationNever() {
        assertThatThrownBy(() -> userService.neverWithTransaction())
                .isInstanceOf(IllegalTransactionStateException.class);
    }

    @Test
    void propagationSupportFalseNegative() {
        assertThatNoException().isThrownBy(() -> userService.withMandatoryTransaction());
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void propagationSupport() {
        assertThatThrownBy(() -> userService.withMandatoryTransaction())
                .isInstanceOf(IllegalTransactionStateException.class);
    }

}