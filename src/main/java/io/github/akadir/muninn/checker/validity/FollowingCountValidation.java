package io.github.akadir.muninn.checker.validity;

import io.github.akadir.muninn.TelegramBot;
import io.github.akadir.muninn.config.ConfigParams;
import io.github.akadir.muninn.enumeration.MuninnMessage;
import io.github.akadir.muninn.model.AuthenticatedUser;
import io.github.akadir.muninn.service.AuthenticatedUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import twitter4j.User;

import java.util.Locale;

/**
 * @author akadir
 * Date: 24.05.2020
 * Time: 01:12
 */
@Component
public class FollowingCountValidation implements AccountValidator {
    private final Logger logger = LoggerFactory.getLogger(FollowingCountValidation.class);

    private final AuthenticatedUserService authenticatedUserService;
    private final MessageSource messageSource;

    @Autowired
    public FollowingCountValidation(AuthenticatedUserService authenticatedUserService, MessageSource messageSource) {
        this.authenticatedUserService = authenticatedUserService;
        this.messageSource = messageSource;
    }

    @Override
    public boolean validate(AuthenticatedUser user, User twitterUser, TelegramBot telegramBot) {
        if (twitterUser.getFriendsCount() > ConfigParams.FOLLOWING_COUNT_LIMIT) {
            String message = messageSource.getMessage(MuninnMessage.FOLLOWING_LIMIT_REACHED.name(),
                    new Object[]{twitterUser.getFriendsCount(), ConfigParams.FOLLOWING_COUNT_LIMIT},
                    Locale.getDefault());

            user = authenticatedUserService.setUserNotActive(user);

            SendMessage telegramMessage = new SendMessage()
                    .setChatId(user.getTelegramChatId())
                    .enableHtml(true)
                    .disableNotification()
                    .disableWebPagePreview()
                    .setText(message);

            telegramBot.notify(telegramMessage);

            logger.info("User: {} following {} accounts which is more than our limit: {}. Bot deactivated for this user.",
                    user.getId(), twitterUser.getFriendsCount(), ConfigParams.FOLLOWING_COUNT_LIMIT);

            return false;
        }

        return true;
    }
}
