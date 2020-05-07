package io.github.akadir.muninn.enumeration;

/**
 * @author akadir
 * Date: 5.05.2020
 * Time: 23:37
 */
public enum TwitterAccountStatus {
    ACTIVE("1"), DEACTIVATED("50"), SUSPENDED("63");

    private final String code;

    TwitterAccountStatus(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
