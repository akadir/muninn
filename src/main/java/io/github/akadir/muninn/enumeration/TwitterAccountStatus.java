package io.github.akadir.muninn.enumeration;

/**
 * @author akadir
 * Date: 5.05.2020
 * Time: 23:37
 */
public enum TwitterAccountStatus {
    ACTIVE(1), DEACTIVATED(50), SUSPENDED(63), UNKNOWN(-1);

    private final int code;

    TwitterAccountStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static TwitterAccountStatus of(int code) {
        for (TwitterAccountStatus status : TwitterAccountStatus.values()) {
            if (status.getCode() == code) {
                return status;
            }
        }

        return UNKNOWN;
    }
}
