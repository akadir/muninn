package io.github.akadir.muninn.enumeration;

/**
 * @author akadir
 * Date: 10.05.2020
 * Time: 16:04
 */
public enum TwitterError {
    USER_NOT_FOUND(50), USER_SUSPENDED(63), ME_SUSPENDED(64), RATE_LIMIT_EXCEEDED(88),
    INVALID_OR_EXPIRED_TOKEN(89), OVER_CAPACITY(130);

    private final int code;

    TwitterError(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
