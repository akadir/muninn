package io.github.akadir.muninn.checker;

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
 * Time: 00:47
 */
@Component
public class NameUpdateChecker implements UpdateChecker {
    private final Logger logger = LoggerFactory.getLogger(NameUpdateChecker.class);

    @Override
    public Optional<ChangeSet> checkUpdate(Friend friend, User user) {
        Optional<ChangeSet> optionalChangeSet = Optional.empty();

        if (!user.getName().equals(friend.getName())) {
            logger.info("User: {} has changed name from: {} ||| to: {}", user.getScreenName(), friend.getName(), user.getName());
            ChangeSet changeSet = ChangeSet.change(friend, friend.getName(), user.getName(), ChangeType.NAME);
            friend.setName(user.getName());
            optionalChangeSet = Optional.of(changeSet);
        }

        return optionalChangeSet;
    }
}
