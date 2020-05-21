package io.github.akadir.muninn.enumeration;

/**
 * @author akadir
 * Date: 21.05.2020
 * Time: 19:14
 */
public enum ThreadAvailability {
    AVAILABLE(0), NOT_AVAILABLE(1);

    private final int code;

    ThreadAvailability(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
