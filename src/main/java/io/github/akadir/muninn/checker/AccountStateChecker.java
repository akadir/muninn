package io.github.akadir.muninn.checker;

import io.github.akadir.muninn.enumeration.TwitterAccountState;
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
 * Time: 17:13
 */
@Component
public class AccountStateChecker implements UpdateChecker {
    private final Logger logger = LoggerFactory.getLogger(AccountStateChecker.class);

    @Override
    public Optional<ChangeSet> checkUpdate(Friend friend, User user) {
        Optional<ChangeSet> optionalChangeSet = Optional.empty();

        if (friend.getAccountState() != TwitterAccountState.ACTIVE.getCode()) {
            logger.info("User: {} activated account. It's state was: {}", user.getScreenName(), friend.getAccountState());
            ChangeSet changeSet = ChangeSet.changeStatus(friend, TwitterAccountState.of(friend.getAccountState()),
                    TwitterAccountState.ACTIVE);
            friend.setAccountState(TwitterAccountState.ACTIVE.getCode());
            optionalChangeSet = Optional.of(changeSet);
        }

        return optionalChangeSet;
    }
}
