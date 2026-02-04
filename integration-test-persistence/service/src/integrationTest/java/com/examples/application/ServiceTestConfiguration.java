package com.examples.application;

import com.examples.application.user.UserRepository;
import com.examples.application.user.TransactionalTestUserService;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;

@SpringBootConfiguration
@EnableAutoConfiguration
public class ServiceTestConfiguration {

    @Bean
    TransactionalTestUserService userServiceForTransactionalTests(
            UserRepository userRepository,
            @Lazy TransactionalTestUserService userService)
    {
        return new TransactionalTestUserService(userRepository, userService);
    }
}
