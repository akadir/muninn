package io.github.akadir.muninn.enumeration;

/**
 * @author akadir
 * Date: 5.05.2020
 * Time: 23:27
 */
public enum ChangeType {
    BIO(1), ACCOUNT_STATUS(2), PP(3), NAME(4), USERNAME(5);

    private final int code;

    ChangeType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
