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
        "spring.datasource.url=jdbc:tc:postgresql:18-alpine:///db",
        "spring.datasource.username=test",
        "spring.datasource.password=test",
        "spring.flyway.locations=classpath:postgres",
        "spring.flyway.placeholders.usersTableName=t_users",
})
class PostgresUserRepositoryTest {

    @Nested
    class PostgresBeforeAndAfterTransactionTest extends BeforeAndAfterTransactionTest {
    }

    @Nested
    class PostgresBeforeAndAfterAllTest extends BeforeAndAfterAllTest {
    }

    @Nested
    class PostgresBeforeEachTest extends BeforeEachTest {
    }

    @Nested
    class PostgresCommitTest extends CommitTest {
    }

    @Nested
    class PostgresTestMethodTest extends TestMethodTest {
    }

    @Nested
    class PosgtresNoTransactionTest extends NoTransactionTest {
    }
}
