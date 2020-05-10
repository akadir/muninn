package io.github.akadir.muninn.enumeration;

/**
 * @author akadir
 * Date: 5.05.2020
 * Time: 23:27
 */
public enum ChangeType {
    BIO(1), ACCOUNT_STATUS(2), PROFILE_PIC(3), NAME(4), USERNAME(5), UNKNOWN(-1);

    private final int code;

    ChangeType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static ChangeType of(int code) {
        for (ChangeType changeType : ChangeType.values()) {
            if (changeType.getCode() == code) {
                return changeType;
            }
        }

        return UNKNOWN;
    }
}
