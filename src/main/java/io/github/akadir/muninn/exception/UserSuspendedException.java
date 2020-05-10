package io.github.akadir.muninn.exception;

import io.github.akadir.muninn.enumeration.TwitterAccountState;
import io.github.akadir.muninn.exception.base.AccountStatusException;

/**
 * @author akadir
 * Date: 5.05.2020
 * Time: 23:45
 */
public class UserSuspendedException extends AccountStatusException {

    public UserSuspendedException() {
        super();
    }

    public TwitterAccountState getAccountStatus() {
        return TwitterAccountState.SUSPENDED;
    }
}
