package com.examples.application.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class LongRunningOperationTest {

    public static final String ACTIVE_USER = "activeUser";
    public static final String DRAFT_USER = "draftUser";
    public static final String FALSE_POSITIVE = "falsePositive";

    @Autowired
    TransactionalTestUserService userService;

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
    }
}