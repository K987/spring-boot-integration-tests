package com.examples.application.user.providers;

import com.examples.application.user.cases.BeforeAndAfterAllTest;
import com.examples.application.user.cases.BeforeAndAfterTransactionTest;
import com.examples.application.user.cases.BeforeEachTest;
import com.examples.application.user.cases.CommitTest;
import com.examples.application.user.cases.NoTransactionTest;
import com.examples.application.user.cases.TestMethodTest;
import org.junit.jupiter.api.Nested;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = {
        "spring.flyway.locations=classpath:h2",
        "spring.flyway.placeholders.usersTableName=T_USERS",
})
class H2UserRepositoryTest {

    @Nested
    class H2BeforeAndAfterTransactionTest extends BeforeAndAfterTransactionTest {
    }

    @Nested
    class H2BeforeAndAfterAllTest extends BeforeAndAfterAllTest {
    }

    @Nested
    class H2BeforeEachTest extends BeforeEachTest {
    }

    @Nested
    class H2CommitTest extends CommitTest {
    }

    @Nested
    class H2TestMethodTest extends TestMethodTest {
    }

    @Nested
    class H2NoTransactionTest extends NoTransactionTest {
    }
}