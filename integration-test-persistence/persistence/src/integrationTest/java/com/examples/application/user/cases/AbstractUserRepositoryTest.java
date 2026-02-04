package com.examples.application.user.cases;

import com.examples.application.user.User;
import com.examples.application.user.UserRepository;
import org.instancio.Instancio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.data.jdbc.test.autoconfigure.DataJdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.instancio.Select.field;

@DataJdbcTest
abstract class AbstractUserRepositoryTest {

    protected static final Logger log = LoggerFactory.getLogger(AbstractUserRepositoryTest.class);

    protected static final String ALICE_DOE = "alice_doe";

    @Autowired
    UserRepository userRepository;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    TransactionTemplate transactionTemplate;

    @Value("${spring.flyway.placeholders.usersTableName}")
    String tableName;

    protected void executeInNewTransaction(Runnable verification) {
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        transactionTemplate.executeWithoutResult(status -> {
            log.info("Executing in new transaction: ");
            verification.run();
        });
    }

    protected void verifyUserExists(String username) {
        log.info("Verifying {} was inserted...", username);
        assertThat(userRepository.findUserByUsername(username)).isNotNull();
    }

    protected void verifyUserDoesNotExists(String username) {
        log.info("Verifying {} not exists...", username);
        assertThat(userRepository.findUserByUsername(username)).isNull();
    }

    protected void createAndPersist(String username) {
        log.info("Inserting {}...", username);
        User user = Instancio
                .of(User.class)
                .ignore(field("id"))
                .set(field("username"), username)
                .create();
        new SimpleJdbcInsert(jdbcTemplate).withTableName(tableName).usingGeneratedKeyColumns("id").execute(new BeanPropertySqlParameterSource(user));
    }

    protected void delete(String username) {
        log.info("Deleting {}...", username);
        int deletedRows = JdbcTestUtils.deleteFromTableWhere(jdbcTemplate, tableName, "username = ?", username);
        assertThat(deletedRows).isEqualTo(1);
    }
}
