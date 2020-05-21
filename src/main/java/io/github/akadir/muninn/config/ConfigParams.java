package io.github.akadir.muninn.config;

import io.github.akadir.muninn.enumeration.VmOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * @author akadir
 * Date: 9.05.2020
 * Time: 22:49
 */
public class ConfigParams {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigParams.class);

    public static final String TELEGRAM_BOT_NAME;
    public static final String TELEGRAM_TOKEN;
    public static final String TWITTER_CONSUMER_KEY;
    public static final String TWITTER_CONSUMER_SECRET;
    public static final int RECHECK_PERIOD;
    public static final String DATA_SOURCE_URL;
    public static final String DATA_SOURCE_USERNAME;
    public static final String DATA_SOURCE_PASSWORD;

    static {
        TELEGRAM_BOT_NAME = System.getProperty(VmOption.TELEGRAM_BOT_NAME.getKey());
        TELEGRAM_TOKEN = System.getProperty(VmOption.TELEGRAM_TOKEN.getKey());
        TWITTER_CONSUMER_KEY = System.getProperty(VmOption.TWITTER_CONSUMER_KEY.getKey());
        TWITTER_CONSUMER_SECRET = System.getProperty(VmOption.TWITTER_CONSUMER_SECRET.getKey());
        RECHECK_PERIOD = Integer.parseInt(System.getProperty(VmOption.RECHECK_PERIOD.getKey()));
        DATA_SOURCE_URL = System.getProperty(VmOption.DATA_SOURCE_URL.getKey());
        DATA_SOURCE_USERNAME = System.getProperty(VmOption.DATA_SOURCE_USERNAME.getKey());
        DATA_SOURCE_PASSWORD = System.getProperty(VmOption.DATA_SOURCE_PASSWORD.getKey());

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("{}: {}", VmOption.TELEGRAM_BOT_NAME, TELEGRAM_BOT_NAME);
            LOGGER.debug("{}: {}", VmOption.TELEGRAM_TOKEN, replaceChars(TELEGRAM_TOKEN));
            LOGGER.debug("{}: {}", VmOption.TWITTER_CONSUMER_KEY, replaceChars(TWITTER_CONSUMER_KEY));
            LOGGER.debug("{}: {}", VmOption.TWITTER_CONSUMER_SECRET, replaceChars(TWITTER_CONSUMER_SECRET));
            LOGGER.debug("{}: {}", VmOption.RECHECK_PERIOD, RECHECK_PERIOD);
            LOGGER.debug("{}: {}", VmOption.DATA_SOURCE_URL, DATA_SOURCE_URL);
            LOGGER.debug("{}: {}", VmOption.DATA_SOURCE_USERNAME, replaceChars(DATA_SOURCE_USERNAME));
            LOGGER.debug("{}: {}", VmOption.DATA_SOURCE_PASSWORD, replaceChars(DATA_SOURCE_PASSWORD));
        }
    }

    private ConfigParams() {
    }

    private static String replaceChars(String token) {
        if (token == null) {
            return null;
        }

        char[] tokenChars = token.toCharArray();
        token.getChars(0, 3, tokenChars, 0);
        Arrays.fill(tokenChars, 3, tokenChars.length, '*');
        return (new String(tokenChars));
    }
}
