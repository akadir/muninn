package io.github.akadir.muninn.enumeration;

/**
 * @author akadir
 * Date: 4.05.2020
 * Time: 01:39
 */
public enum TelegramBotStatus {
    ACTIVE(1), AUTHENTICATING(2), NOT_ACTIVE(3);

    TelegramBotStatus(int code) {
        this.code = code;
    }

    private final int code;

    public int getCode() {
        return code;
    }
}
