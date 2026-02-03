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
        "spring.datasource.url=jdbc:tc:mysql:8.0.36:///db",
        "spring.datasource.username=test",
        "spring.datasource.password=test",
        "spring.flyway.locations=classpath:mysql",
})
class MySqlUserRepositoryTest {

    @Nested
    class MySqlBeforeAndAfterTransactionTest extends BeforeAndAfterTransactionTest {
    }

    @Nested
    class MySqlBeforeAndAfterAllTest extends BeforeAndAfterAllTest {
    }

    @Nested
    class MySqlBeforeEachTest extends BeforeEachTest {
    }

    @Nested
    class MySqlCommitTest extends CommitTest {
    }

    @Nested
    class MySqlTestMethodTest extends TestMethodTest {
    }

    @Nested
    class MySqlNoTransactionTest extends NoTransactionTest {
    }
}