package io.github.akadir.muninn.config;

import io.github.akadir.muninn.enumeration.VmOption;

/**
 * @author akadir
 * Date: 9.05.2020
 * Time: 22:49
 */
public class ConfigParams {
    public static final String TELEGRAM_BOT_NAME;
    public static final String TELEGRAM_TOKEN;
    public static final String TWITTER_CONSUMER_KEY;
    public static final String TWITTER_CONSUMER_SECRET;
    public static final int RECHECK_PERIOD;

    static {
        TELEGRAM_BOT_NAME = System.getProperty(VmOption.TELEGRAM_BOT_NAME.getKey());
        TELEGRAM_TOKEN = System.getProperty(VmOption.TELEGRAM_TOKEN.getKey());
        TWITTER_CONSUMER_KEY = System.getProperty(VmOption.TWITTER_CONSUMER_KEY.getKey());
        TWITTER_CONSUMER_SECRET = System.getProperty(VmOption.TWITTER_CONSUMER_SECRET.getKey());
        RECHECK_PERIOD = Integer.parseInt(System.getProperty(VmOption.RECHECK_PERIOD.getKey()));
    }

    private ConfigParams() {
    }
}
