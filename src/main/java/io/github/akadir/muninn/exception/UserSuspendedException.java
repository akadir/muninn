package io.github.akadir.muninn.exception;

import io.github.akadir.muninn.enumeration.TwitterAccountStatus;
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

    public TwitterAccountStatus getAccountStatus() {
        return TwitterAccountStatus.SUSPENDED;
    }
}
