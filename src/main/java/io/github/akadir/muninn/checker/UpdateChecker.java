package io.github.akadir.muninn.checker;

import io.github.akadir.muninn.model.ChangeSet;
import io.github.akadir.muninn.model.Friend;
import twitter4j.User;

import java.util.Optional;

/**
 * @author akadir
 * Date: 10.05.2020
 * Time: 00:35
 */
public interface UpdateChecker {
    Optional<ChangeSet> checkUpdate(Friend friend, User user);
}
