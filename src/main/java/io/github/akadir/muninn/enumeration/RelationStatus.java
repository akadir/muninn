package io.github.akadir.muninn.enumeration;

/**
 * @author akadir
 * Date: 6.05.2020
 * Time: 18:42
 */
public enum RelationStatus {
    ACTIVE(1), PASSIVE(0);

    private final int code;

    RelationStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
