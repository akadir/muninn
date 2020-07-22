package io.github.akadir.muninn.config;

import io.github.akadir.muninn.enumeration.VmOption;
import io.github.akadir.muninn.exception.PropertyNotLoadedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;

/**
 * @author akadir
 * Date: 9.05.2020
 * Time: 22:49
 */
public class ConfigParams {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigParams.class);

    private static final String PROPERTIES_FILE = "muninn.properties";

    public static final String TELEGRAM_BOT_NAME;
    public static final String TELEGRAM_TOKEN;
    public static final String TWITTER_CONSUMER_KEY;
    public static final String TWITTER_CONSUMER_SECRET;
    public static final int RECHECK_PERIOD;
    public static final String DATA_SOURCE_URL;
    public static final String DATA_SOURCE_USERNAME;
    public static final String DATA_SOURCE_PASSWORD;
    public static final int FOLLOWING_COUNT_LIMIT;

    static {
        Properties properties = new Properties();

        File propertyFile = new File(PROPERTIES_FILE);

        try (InputStream input = new FileInputStream(propertyFile)) {
            properties.load(input);

            TELEGRAM_BOT_NAME = properties.getProperty(VmOption.TELEGRAM_BOT_NAME.getKey());
            TELEGRAM_TOKEN = properties.getProperty(VmOption.TELEGRAM_TOKEN.getKey());
            TWITTER_CONSUMER_KEY = properties.getProperty(VmOption.TWITTER_CONSUMER_KEY.getKey());
            TWITTER_CONSUMER_SECRET = properties.getProperty(VmOption.TWITTER_CONSUMER_SECRET.getKey());
            RECHECK_PERIOD = Integer.parseInt(properties.getProperty(VmOption.RECHECK_PERIOD.getKey(), "1"));
            DATA_SOURCE_URL = properties.getProperty(VmOption.DATA_SOURCE_URL.getKey());
            DATA_SOURCE_USERNAME = properties.getProperty(VmOption.DATA_SOURCE_USERNAME.getKey());
            DATA_SOURCE_PASSWORD = properties.getProperty(VmOption.DATA_SOURCE_PASSWORD.getKey());
            FOLLOWING_COUNT_LIMIT = Integer.parseInt(properties.getProperty(VmOption.FOLLOWING_COUNT_LIMIT.getKey(), "0"));

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("{}: {}", VmOption.TELEGRAM_BOT_NAME, TELEGRAM_BOT_NAME);
                LOGGER.debug("{}: {}", VmOption.TELEGRAM_TOKEN, replaceChars(TELEGRAM_TOKEN));
                LOGGER.debug("{}: {}", VmOption.TWITTER_CONSUMER_KEY, replaceChars(TWITTER_CONSUMER_KEY));
                LOGGER.debug("{}: {}", VmOption.TWITTER_CONSUMER_SECRET, replaceChars(TWITTER_CONSUMER_SECRET));
                LOGGER.debug("{}: {}", VmOption.RECHECK_PERIOD, RECHECK_PERIOD);
                LOGGER.debug("{}: {}", VmOption.DATA_SOURCE_URL, DATA_SOURCE_URL);
                LOGGER.debug("{}: {}", VmOption.DATA_SOURCE_USERNAME, replaceChars(DATA_SOURCE_USERNAME));
                LOGGER.debug("{}: {}", VmOption.DATA_SOURCE_PASSWORD, replaceChars(DATA_SOURCE_PASSWORD));
                LOGGER.debug("{}: {}", VmOption.FOLLOWING_COUNT_LIMIT, FOLLOWING_COUNT_LIMIT);
            }
        } catch (IOException e) {
            LOGGER.error("error occurred while getting properties from file: " + PROPERTIES_FILE, e);
            throw new PropertyNotLoadedException(PROPERTIES_FILE);
        }
    }

    private ConfigParams() {
    }

    private static String replaceChars(String token) {
        if (token == null) {
            return null;
        }

        char[] tokenChars = token.toCharArray();
        token.getChars(0, token.length() / 3, tokenChars, 0);
        Arrays.fill(tokenChars, token.length() / 3, tokenChars.length, '*');
        return (new String(tokenChars));
    }
}
