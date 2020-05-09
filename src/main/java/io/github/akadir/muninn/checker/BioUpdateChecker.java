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
 * Time: 00:36
 */
@Component
public class BioUpdateChecker implements UpdateChecker {
    private final Logger logger = LoggerFactory.getLogger(BioUpdateChecker.class);

    @Override
    public Optional<ChangeSet> checkUpdate(Friend friend, User user) {
        Optional<ChangeSet> optionalChangeSet = Optional.empty();

        if (!user.getDescription().equals(friend.getBio())) {
            logger.info("User: {} has changed bio from: {} ||| to: {}", user.getScreenName(), friend.getBio(), user.getDescription());
            ChangeSet changeSet = ChangeSet.change(friend, friend.getBio(), user.getDescription(), ChangeType.BIO);
            friend.setBio(user.getDescription());
            optionalChangeSet = Optional.of(changeSet);
        }

        return optionalChangeSet;
    }
}
