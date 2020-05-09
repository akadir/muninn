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
 * Time: 00:51
 */
@Component
public class ProfilePicUpdateChecker implements UpdateChecker {
    private final Logger logger = LoggerFactory.getLogger(ProfilePicUpdateChecker.class);

    @Override
    public Optional<ChangeSet> checkUpdate(Friend friend, User user) {
        Optional<ChangeSet> optionalChangeSet = Optional.empty();

        if (!user.get400x400ProfileImageURLHttps().equals(friend.getProfilePicUrl())) {
            logger.info("User: {} has changed profile pic from: {} ||| to: {}", user.getScreenName(),
                    friend.getProfilePicUrl(), user.get400x400ProfileImageURLHttps());
            ChangeSet changeSet = ChangeSet.change(friend, friend.getProfilePicUrl(),
                    user.get400x400ProfileImageURLHttps(), ChangeType.PP);
            friend.setProfilePicUrl(user.get400x400ProfileImageURLHttps());
            optionalChangeSet = Optional.of(changeSet);
        }

        return optionalChangeSet;
    }
}
