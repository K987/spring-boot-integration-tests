package com.examples.application.user.cases;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public abstract class TestMethodTest extends AbstractUserRepositoryTest {

    private static final String JAKE_DOE = "jake_doe";

    @Test
    @Order(1)
    void insertJohnDoeExistsButNotCommitted() {
        createAndPersist(JAKE_DOE);
        verifyUserExists(JAKE_DOE);
        executeInNewTransaction(() -> verifyUserDoesNotExists(JAKE_DOE));
    }

    @Test
    @Order(2)
    void verifyJohnDoeIsRolledBack() {
        verifyUserDoesNotExists(JAKE_DOE);
    }
}
