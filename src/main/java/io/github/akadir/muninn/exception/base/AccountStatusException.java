package io.github.akadir.muninn.exception.base;

import io.github.akadir.muninn.enumeration.TwitterAccountState;

/**
 * @author akadir
 * Date: 10.05.2020
 * Time: 16:31
 */
public abstract class AccountStatusException extends RuntimeException {
    public abstract TwitterAccountState getAccountStatus();
}
