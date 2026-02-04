package com.examples.application.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jdbc.test.autoconfigure.DataJdbcTest;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJdbcTest
class LongRunningOperationTest {

    public static final String ACTIVE_USER = "activeUser";
    public static final String DRAFT_USER = "draftUser";
    public static final String FALSE_POSITIVE = "falsePositive";

    @Autowired
    TransactionalTestUserService userService;

    @Autowired
    TransactionTemplate transactionTemplate;

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void finalizeUserSuccessfully() {
        User user = UserUtil.createUser(ACTIVE_USER);
        userService.longRunningOperation(user, false);

        user = userService.fetchUserByUserName(ACTIVE_USER);
        assertThat(user).isNotNull();
        assertThat(user.getUserStatus()).isEqualTo(100);
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void keepUserAsDraft() {
        User user = UserUtil.createUser(DRAFT_USER);
        userService.longRunningOperation(user, true);

        user = userService.fetchUserByUserName(DRAFT_USER);
        assertThat(user).isNotNull();
        assertThat(user.getUserStatus()).isEqualTo(0);
    }

    @Test
    void falseResult() {
        User user = UserUtil.createUser(FALSE_POSITIVE);
        userService.longRunningOperation(user, true);

        // seems to be committed with success - should be committed as draft
        user = userService.fetchUserByUserName(FALSE_POSITIVE);
        assertThat(user).isNotNull();
        assertThat(user.getUserStatus()).isEqualTo(100);

        // in fact, it was never committed - should be committed as draft
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        transactionTemplate.executeWithoutResult(status -> {
            User freshUser = userService.fetchUserByUserName(FALSE_POSITIVE);
            assertThat(freshUser).isNull();
        });
    }
}