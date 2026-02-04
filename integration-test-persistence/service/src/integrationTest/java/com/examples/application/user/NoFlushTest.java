package com.examples.application.user;

import org.hibernate.exception.DataException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@ExtendWith(OutputCaptureExtension.class)
class NoFlushTest {

    @Autowired
    TransactionalTestUserService userService;

    @Autowired
    TestEntityManager testEntityManager;

    // Allowed length for username is 255 characters
    String veryLongUsername = "A".repeat(500);

    @Test
    void falseNegativeWithoutFlush() {
        User user = UserUtil.createUser(veryLongUsername);
        userService.create(user);

        // No exception is thrown here because the flush has not occurred yet
    }

    @Test
    void failsWithFlush() {
        User user = UserUtil.createUser(veryLongUsername);
        userService.create(user);

        assertThatThrownBy(() -> testEntityManager.flush())
                .isInstanceOf(DataException.class);
    }

    @Test
    void postPersistNotCalledWithoutFlush(CapturedOutput logOutput) {
        User user = UserUtil.createUser("validUsername");
        userService.create(user);

        assertThat(logOutput.getOut()).doesNotContain("PostPersist called for User: validUsername");
    }

    @Test
    void postPersistCalledWhenFlush(CapturedOutput logOutput) {
        User user = UserUtil.createUser("validUsername");
        userService.create(user);

        testEntityManager.flush();

        assertThat(logOutput.getOut()).contains("PostPersist called for User: validUsername");
    }
}
