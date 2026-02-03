package com.examples.application.user.cases;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public abstract class BeforeEachTest extends AbstractUserRepositoryTest {

    private static final String ALICE_DOE = "alice_doe";

    // Testing BeforeEach behavior
    @BeforeEach
    void insertAliceDoe() {
        log.info("Executing BeforeEach -- will run before each test method");
        createAndPersist(ALICE_DOE);
    }

    @Test
    void verifyAliceDoeExistsButNotCommitted() {
        verifyUserExists(ALICE_DOE);
        executeInNewTransaction(() -> verifyUserDoesNotExists(ALICE_DOE));
    }

    // Clean up after each test is not needed as the transaction will be rolled back
}
