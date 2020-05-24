package io.github.akadir.muninn.checker.validity;

import io.github.akadir.muninn.TelegramBot;
import io.github.akadir.muninn.model.AuthenticatedUser;
import twitter4j.User;

/**
 * @author akadir
 * Date: 24.05.2020
 * Time: 01:02
 */
public interface AccountValidator {
    boolean validate(AuthenticatedUser user, User twitterUser, TelegramBot telegramBot);
}
