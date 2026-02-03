package com.examples.application.user.cases;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.test.context.transaction.BeforeTransaction;

public abstract class BeforeAndAfterTransactionTest extends AbstractUserRepositoryTest {

    private static final String BOB_DOE = "bob_doe";

    @BeforeTransaction
    void insertBobDoeBeforeTransaction() {
        log.info("Executing BeforeTransaction -- will run before each test method");
        /*
            TestEntityManager operations must run inside a transaction.
            The @BeforeTransaction method itself is not transactional,
            so we need to create a transaction here.
        */
        executeInNewTransaction(() -> createAndPersist(BOB_DOE));
    }

    @Test
    void verifyBobDoeIsCommitted() {
        verifyUserExists(BOB_DOE);
        executeInNewTransaction(() -> verifyUserExists(BOB_DOE));
    }

    @AfterTransaction
    void deleteBobDoeAfterTransaction() {
        log.info("Executing AfterTransaction -- will run after each test method");
        /*
            TestEntityManager operations must run inside a transaction.
            The @AfterTransaction method itself is not transactional,
            so we need to create a transaction here.
        */
        executeInNewTransaction(() -> delete(BOB_DOE));
    }
}
