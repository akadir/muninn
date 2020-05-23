package io.github.akadir.muninn.checker.update;

import io.github.akadir.muninn.enumeration.ChangeType;
import io.github.akadir.muninn.model.ChangeSet;
import io.github.akadir.muninn.model.Friend;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import twitter4j.User;

import java.util.Optional;

/**
 * @author akadir
 * Date: 10.05.2020
 * Time: 00:49
 */
@Component
public class ScreenNameUpdateChecker implements UpdateChecker {
    private final Logger logger = LoggerFactory.getLogger(ScreenNameUpdateChecker.class);

    @Override
    public Optional<ChangeSet> checkUpdate(Friend friend, User user) {
        Optional<ChangeSet> optionalChangeSet = Optional.empty();

        if (!user.getScreenName().equals(friend.getUsername())) {
            logger.info("User has changed screen name from: {} ||| to: {}", friend.getUsername(), user.getScreenName());
            ChangeSet changeSet = ChangeSet.change(friend, friend.getUsername(), user.getScreenName(), ChangeType.USERNAME);
            friend.setUsername(user.getScreenName());
            optionalChangeSet = Optional.of(changeSet);
        }

        return optionalChangeSet;
    }
}
