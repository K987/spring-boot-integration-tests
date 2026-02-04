package com.examples.application.user;


import org.instancio.Instancio;

import static org.assertj.core.api.Assumptions.assumeThat;
import static org.instancio.Select.field;

final class UserUtil {

    static User
    createUser(String username) {
        User user = Instancio
                .of(User.class)
                .ignore(field("id"))
                .set(field("username"), username)
                .create();
        assumeThat(user.getId()).isNull();
        return user;
    }

    private UserUtil() {
    }
}
