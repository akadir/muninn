package io.github.akadir.muninn.operation;

import io.github.akadir.muninn.enumeration.MuninnMessage;
import io.github.akadir.muninn.enumeration.TelegramBotStatus;
import io.github.akadir.muninn.enumeration.TelegramOption;
import io.github.akadir.muninn.model.AuthenticatedUser;
import io.github.akadir.muninn.service.AuthenticatedUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Locale;
import java.util.Optional;

/**
 * @author akadir
 * Date: 2.05.2020
 * Time: 23:06
 */
@Component("stop")
public class Stop implements Operation {
    private final Logger logger = LoggerFactory.getLogger(Stop.class);
    private final AuthenticatedUserService authenticatedUserService;
    private final MessageSource messageSource;

    @Autowired
    public Stop(AuthenticatedUserService authenticatedUserService, MessageSource messageSource) {
        this.authenticatedUserService = authenticatedUserService;
        this.messageSource = messageSource;
    }

    @Override
    public TelegramOption getOption() {
        return TelegramOption.LOGOUT;
    }

    @Override
    public SendMessage generateMessage(Update update) {
        String messageContent = null;
        try {
            Integer userId = update.getMessage().getFrom().getId();
            Optional<AuthenticatedUser> optionalAuthenticatedUser = authenticatedUserService.findActiveByTelegramUserId(userId);

            if (optionalAuthenticatedUser.isPresent()) {
                AuthenticatedUser authenticatedUser = optionalAuthenticatedUser.get();
                logger.info("User found: {}", authenticatedUser);

                if (authenticatedUser.getBotStatus() == TelegramBotStatus.ACTIVE.getCode()) {
                    authenticatedUserService.setUserNotActive(authenticatedUser);
                    logger.info("User updated as not active: {}", authenticatedUser);
                    messageContent = messageSource.getMessage(MuninnMessage.BOT_DEACTIVATED.name(), new Object[0],
                            Locale.forLanguageTag(update.getMessage().getFrom().getLanguageCode()));
                }
            } else {
                logger.info("User not found with userId: {}", userId);
                messageContent = messageSource.getMessage(MuninnMessage.ACCOUNT_NOT_FOUND.name(), new Object[0],
                        Locale.forLanguageTag(update.getMessage().getFrom().getLanguageCode()));
            }
        } catch (Exception e) {
            logger.error("An error occurred while logging out: ", e);
            messageContent = messageSource.getMessage(MuninnMessage.ERROR.name(),
                    new Object[]{TelegramOption.HELP.getOption()}, Locale.getDefault());
        }

        return new SendMessage()
                .setChatId(update.getMessage().getChatId())
                .disableWebPagePreview()
                .enableHtml(true)
                .setText(messageContent);
    }
}
