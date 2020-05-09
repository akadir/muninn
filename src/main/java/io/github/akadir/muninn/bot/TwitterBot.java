package io.github.akadir.muninn.bot;

import io.github.akadir.muninn.config.ConfigParams;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;

/**
 * @author akadir
 * Date: 4.05.2020
 * Time: 23:24
 */
public class TwitterBot {
    private TwitterBot() {
    }

    public static Twitter getTwitter() {
        TwitterFactory twitterFactory = new TwitterFactory();
        Twitter twitter = twitterFactory.getInstance();
        twitter.setOAuthConsumer(getConsumerKey(), getConsumerSecret());

        return twitter;
    }

    private static String getConsumerKey() {
        return ConfigParams.TWITTER_CONSUMER_KEY;
    }

    private static String getConsumerSecret() {
        return ConfigParams.TWITTER_CONSUMER_SECRET;
    }
}
