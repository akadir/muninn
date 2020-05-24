package io.github.akadir.muninn.bot;

import com.kadir.twitterbots.ratelimithandler.handler.RateLimitHandler;
import com.kadir.twitterbots.ratelimithandler.process.ApiProcessType;
import io.github.akadir.muninn.config.ConfigParams;
import io.github.akadir.muninn.enumeration.TwitterError;
import io.github.akadir.muninn.exception.*;
import io.github.akadir.muninn.model.AuthenticatedUser;
import io.github.akadir.muninn.model.EmptyIDs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.*;
import twitter4j.auth.AccessToken;

import javax.validation.constraints.NotNull;
import java.util.Optional;

/**
 * @author akadir
 * Date: 4.05.2020
 * Time: 23:24
 */
public class TwitterBot {
    private static final Logger LOGGER = LoggerFactory.getLogger(TwitterBot.class);

    private TwitterBot() {
    }

    public static Twitter getTwitter() {
        TwitterFactory twitterFactory = new TwitterFactory();
        Twitter twitter = twitterFactory.getInstance();
        twitter.setOAuthConsumer(ConfigParams.TWITTER_CONSUMER_KEY, ConfigParams.TWITTER_CONSUMER_SECRET);

        return twitter;
    }

    public static Twitter getTwitter(String token, String tokenSecret) {
        TwitterFactory twitterFactory = new TwitterFactory();

        Twitter twitter = twitterFactory.getInstance();
        twitter.setOAuthConsumer(ConfigParams.TWITTER_CONSUMER_KEY, ConfigParams.TWITTER_CONSUMER_SECRET);
        twitter.setOAuthAccessToken(new AccessToken(token, tokenSecret));

        return twitter;
    }

    public static IDs getFriendIDs(Twitter twitter, @NotNull AuthenticatedUser user, long cursor) {
        IDs iDs;

        try {
            iDs = Optional.of(twitter.getFriendsIDs(user.getTwitterUserId(), cursor)).orElse(new EmptyIDs());
            RateLimitHandler.handle(user.getId(), iDs.getRateLimitStatus(), ApiProcessType.GET_FRIENDS_IDS);
        } catch (TwitterException e) {
            LOGGER.error("Error while fetching user friends: ", e);
            checkException(user.getId(), e, ApiProcessType.SHOW_USER);
            //TODO this call may cause stack overflow error
            iDs = getFriendIDs(twitter, user, cursor);
        }

        return iDs;
    }

    public static User showUser(Twitter twitter, @NotNull AuthenticatedUser authUser, long userId) {
        User user;

        try {
            user = twitter.showUser(userId);
            RateLimitHandler.handle(authUser.getTwitterUserId(), user.getRateLimitStatus(), ApiProcessType.SHOW_USER);
        } catch (TwitterException e) {
            LOGGER.error("Error while fetching user details with id: {}: ", userId, e);
            checkException(authUser.getId(), e, ApiProcessType.GET_FRIENDS_IDS);
            //TODO this call may cause stack overflow error
            user = showUser(twitter, authUser, userId);
        }

        return user;
    }

    private static void checkException(Long id, TwitterException e, ApiProcessType apiProcessType) {
        int errorCode = e.getErrorCode();

        if (errorCode == TwitterError.ME_SUSPENDED.getCode()) {
            throw new AccountSuspendedException();
        } else if (errorCode == TwitterError.INVALID_OR_EXPIRED_TOKEN.getCode()) {
            throw new TokenExpiredException();
        } else if (errorCode == TwitterError.USER_NOT_FOUND.getCode()) {
            throw new UserNotFoundException();
        } else if (errorCode == TwitterError.USER_SUSPENDED.getCode()) {
            throw new UserSuspendedException();
        } else if (errorCode == TwitterError.RATE_LIMIT_EXCEEDED.getCode()
                || errorCode == TwitterError.OVER_CAPACITY.getCode()) {
            LOGGER.warn("Rate limit hit. Wait little bit.");
            RateLimitHandler.handle(id, null, apiProcessType);
        } else {
            throw new ProxyTwitterException(e);
        }
    }
}
