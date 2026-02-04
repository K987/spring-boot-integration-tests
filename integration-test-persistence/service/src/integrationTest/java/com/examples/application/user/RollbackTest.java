package com.examples.application.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jdbc.test.autoconfigure.DataJdbcTest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJdbcTest
class RollbackTest {

    private static final String USERNAME = "rollback";

    @Autowired
    private TransactionalTestUserService userService;

    @Test
    void testDoesNotRollback() {

        createUserForRollback();

        // user found despite rollback
        User fetchedUser = userService.fetchUserByUserName(USERNAME);
        assertThat(fetchedUser).isNotNull();
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void testRollsBack() {

        createUserForRollback();

        // user not found due to rollback
        User fetchedUser = userService.fetchUserByUserName(USERNAME);
        assertThat(fetchedUser).isNull();
    }

    private void createUserForRollback() {
        User user = UserUtil.createUser(USERNAME);
        userService.createForRollback(user);
    }
}

