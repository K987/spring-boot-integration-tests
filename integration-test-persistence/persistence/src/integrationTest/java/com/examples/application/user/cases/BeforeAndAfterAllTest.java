package com.examples.application.user.cases;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

//To be able to run class setup and teardown methods as non-static
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BeforeAndAfterAllTest extends AbstractUserRepositoryTest {

    private static final String JANE_DOE = "jane_doe";

    @BeforeAll
    void insertJaneDoe() {
        log.info("Executing BeforeAll -- will run only once");
        createAndPersist(JANE_DOE);
    }

    @Test
    void verifyJaneDoeCommitted() {
        verifyUserExists(JANE_DOE);
        executeInNewTransaction(() -> verifyUserExists(JANE_DOE));

    }

    @AfterAll
    void deleteJaneDoe() {
        log.info("Executing AfterAll -- will run only once");
        delete(JANE_DOE);
    }
}
