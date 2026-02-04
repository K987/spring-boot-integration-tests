package com.examples.application.user;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class TransactionalTestUserService {

    private final UserRepository repository;

    private final TransactionalTestUserService self;

    public TransactionalTestUserService(UserRepository repository, TransactionalTestUserService self) {
        this.repository = repository;
        this.self = self;
    }

    void create(User user) {
        repository.save(user);
    }

    void longRunningOperation(User draft, boolean shouldFail) {
        // Create user draft and commit immediately
        draft.setUserStatus(0);
        repository.save(draft);

        // Do some other processing here that may fail
        try {
            self.failingLongRunningOperation(draft, shouldFail);
        } catch (Exception e) {
            // post process failure... keep draft
        }
    }

    @Transactional
    void failingLongRunningOperation(User draft, boolean shouldFail) {
        // Some processing done here, may or may not commit
        draft.setUserStatus(100);
        repository.save(draft);

        if (shouldFail) {
            // But then fail
            throw new RuntimeException("Simulated failure during long running operation");
        }
    }

    User fetchUserByUserName(String username) {
        return repository.findUserByUsername(username);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    User createWithCommit(User user) {
        return repository.save(user);
    }

    void createForRollback(User user) {
        try {
            self.doCreateWithRollback(user);
        } catch (Exception e) {
            // Swallow exception to allow test to continue
        }
    }

    @Transactional
    User doCreateWithRollback(User user) {
        repository.save(user);
        throw new RuntimeException("Forcing rollback for testing purposes");
    }

    @Transactional(propagation = Propagation.NEVER)
    void neverWithTransaction() {
        // This method should never run within a transaction
    }

    @Transactional(propagation = Propagation.MANDATORY)
    void withMandatoryTransaction() {
        // This method should throw an exception if no transaction exists
    }
}
