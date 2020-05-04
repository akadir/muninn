package io.github.akadir.muninn.enumeration;

/**
 * @author akadir
 * Date: 2.05.2020
 * Time: 22:09
 */
public enum TelegramOption {
    LOGIN("/start"),
    LOGOUT("/stop"),
    HELP("/help");

    TelegramOption(String option) {
        this.option = option;
    }

    private final String option;

    public String getOption() {
        return option;
    }
}
