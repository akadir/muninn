package io.github.akadir.muninn.enumeration;

/**
 * @author akadir
 * Date: 5.05.2020
 * Time: 23:37
 */
public enum TwitterAccountState {
    ACTIVE(1), DEACTIVATED(50), SUSPENDED(63), UNKNOWN(-1);

    private final int code;

    TwitterAccountState(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static TwitterAccountState of(int code) {
        for (TwitterAccountState status : TwitterAccountState.values()) {
            if (status.getCode() == code) {
                return status;
            }
        }

        return UNKNOWN;
    }
}
