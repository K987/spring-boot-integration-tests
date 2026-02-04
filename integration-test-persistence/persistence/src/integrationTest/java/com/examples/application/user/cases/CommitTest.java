package com.examples.application.user.cases;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.test.annotation.Commit;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public abstract class CommitTest extends AbstractUserRepositoryTest {

    private static final String JOHN_DOE = "john_doe";

    @Test
    @Order(1)
    @Commit //same as @Rollback(false), but more explicit about the intent
    void insertJohnDoeWithCommit() {
        createAndPersist(JOHN_DOE);
        verifyUserExists(JOHN_DOE);
        executeInNewTransaction(() -> verifyUserDoesNotExists(JOHN_DOE));
    }

    @Test
    @Order(2)
    void verifyJohnDoeIsCommitted() {
        verifyUserExists(JOHN_DOE);
        executeInNewTransaction(() -> verifyUserExists(JOHN_DOE));
        //cleanup, will be committed
        executeInNewTransaction(() -> delete(JOHN_DOE));
    }
}
