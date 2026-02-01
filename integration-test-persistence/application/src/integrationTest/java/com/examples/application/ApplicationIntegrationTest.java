package com.examples.application;

import org.flywaydb.test.FlywayTestExecutionListener;
import org.flywaydb.test.annotation.FlywayTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;

@SpringBootTest
@TestExecutionListeners(listeners = {FlywayTestExecutionListener.class}, mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS)
class ApplicationIntegrationTest {

    @Autowired
    DataSource ds;
    private JdbcClient jdbcClient;

    @BeforeEach
    void setUp() {
        jdbcClient = JdbcClient.create(ds);
    }

    @FlywayTest(locationsForMigrate = {"classpath:config/migration"},  invokeCleanDB = false)
    @Test
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    void test2() {
        int tUsers = JdbcTestUtils.countRowsInTable(jdbcClient, "t_users");
        System.out.println("2 Number of users in t_users table: " + tUsers);
    }

    @FlywayTest(locationsForMigrate = {"classpath:config/migration"}, invokeCleanDB = false)
    @Test
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    void test1() {
        int tUsers = JdbcTestUtils.countRowsInTable(jdbcClient, "t_users");
        System.out.println("1 Number of users in t_users table: " + tUsers);
    }
}

