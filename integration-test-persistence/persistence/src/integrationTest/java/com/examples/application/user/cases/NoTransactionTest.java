package com.examples.application.user.cases;

import com.examples.application.user.User;
import org.instancio.Instancio;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static org.instancio.Select.field;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional(propagation = Propagation.NEVER)
public abstract class NoTransactionTest extends AbstractUserRepositoryTest {

    private static final String MIKE_DOE = "mike_doe";

    @Test
    @Order(1)
    void eachOperationHasItsOwnTrx() {
        User user = Instancio
                .of(User.class)
                .ignore(field("id"))
                .set(field("username"), MIKE_DOE)
                .create();
        log.info("Inserting {}...", MIKE_DOE);
        userRepository.save(user);
        verifyUserExists(MIKE_DOE);
        executeInNewTransaction(() -> verifyUserExists(MIKE_DOE));
    }

    @Test
    @Order(2)
    void verifyJohnDoeIsCommitted() {
        verifyUserExists(MIKE_DOE);
    }
}
