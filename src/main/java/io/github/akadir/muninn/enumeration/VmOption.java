package io.github.akadir.muninn.enumeration;

/**
 * @author akadir
 * Date: 3.05.2020
 * Time: 23:55
 */
public enum VmOption {
    TELEGRAM_BOT_NAME("telegram.bot.name"),
    TELEGRAM_TOKEN("telegram.token"),
    TWITTER_CONSUMER_KEY("twitter.consume.key"),
    TWITTER_CONSUMER_SECRET("twitter.consumer.secret"),
    RECHECK_PERIOD("muninn.recheck.period"),
    DATA_SOURCE_URL("data.source.url"),
    DATA_SOURCE_USERNAME("data.source.username"),
    DATA_SOURCE_PASSWORD("data.source.password");

    VmOption(String key) {
        this.key = key;
    }

    private final String key;

    public String getKey() {
        return key;
    }
}
