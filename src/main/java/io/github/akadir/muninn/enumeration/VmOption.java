package io.github.akadir.muninn.enumeration;

/**
 * @author akadir
 * Date: 3.05.2020
 * Time: 23:55
 */
public enum VmOption {
    TELEGRAM_BOT_NAME("telegramBotName"),
    TELEGRAM_TOKEN("telegramToken"),
    TWITTER_CONSUMER_KEY("twitterConsumerKey"),
    TWITTER_CONSUMER_SECRET("twitterConsumerSecret"),
    RECHECK_PERIOD("reCheckPeriod"),
    DATA_SOURCE_URL("DATA_SOURCE_URL"),
    DATA_SOURCE_USERNAME("DATA_SOURCE_USERNAME"),
    DATA_SOURCE_PASSWORD("DATA_SOURCE_PASSWORD");

    VmOption(String key) {
        this.key = key;
    }

    private final String key;

    public String getKey() {
        return key;
    }
}
