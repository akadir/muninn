package io.github.akadir.muninn.operation;

import io.github.akadir.muninn.TelegramBot;
import io.github.akadir.muninn.bot.TwitterBot;
import io.github.akadir.muninn.checker.validity.AccountValidator;
import io.github.akadir.muninn.enumeration.MuninnMessage;
import io.github.akadir.muninn.enumeration.TelegramBotStatus;
import io.github.akadir.muninn.enumeration.TelegramOption;
import io.github.akadir.muninn.exception.InvalidCommandException;
import io.github.akadir.muninn.model.AuthenticatedUser;
import io.github.akadir.muninn.service.AuthenticatedUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * @author akadir
 * Date: 2.05.2020
 * Time: 23:05
 */
@Component("start")
public class Start implements Operation {
    private final Logger logger = LoggerFactory.getLogger(Start.class);

    private final MessageSource messageSource;
    private final AuthenticatedUserService authenticatedUserService;
    private final List<AccountValidator> accountValidators;

    @Autowired
    public Start(MessageSource messageSource, AuthenticatedUserService authenticatedUserService, List<AccountValidator> accountValidators) {
        this.messageSource = messageSource;
        this.authenticatedUserService = authenticatedUserService;
        this.accountValidators = accountValidators;
    }

    @Override
    public TelegramOption getOption() {
        return TelegramOption.LOGIN;
    }

    @Override
    public void handle(Update update, TelegramBot telegramBot) {
        String messageContent;
        SendMessage message;
        String command = update.getMessage().getText();
        Twitter twitter = TwitterBot.getTwitter();

        int userId = update.getMessage().getFrom().getId();

        if (command.equals(TelegramOption.LOGIN.getOption())) {
            Optional<AuthenticatedUser> optionalAuthenticatedUser = authenticatedUserService.findActiveByTelegramUserId(userId);
            if (optionalAuthenticatedUser.isPresent()) {
                messageContent = messageSource.getMessage(MuninnMessage.BOT_ALREADY_ACTIVATED.name(),
                        new Object[0], Locale.getDefault());
            } else {
                Optional<AuthenticatedUser> optionalAuthenticatingUser = authenticatedUserService.findUserBeingAuthenticated(userId);
                messageContent = authenticate(twitter, update, optionalAuthenticatingUser.orElse(new AuthenticatedUser()));
            }
        } else {
            Optional<AuthenticatedUser> optionalAuthenticatingUser = authenticatedUserService.findUserBeingAuthenticated(userId);

            if (optionalAuthenticatingUser.isPresent()) {
                AuthenticatedUser beingAuthenticated = optionalAuthenticatingUser.get();

                if (notNullAndNotEmpty(beingAuthenticated.getTwitterRequestToken())
                        && notNullAndNotEmpty(beingAuthenticated.getTwitterRequestTokenSecret())) {
                    messageContent = getAccessTokens(twitter, command, beingAuthenticated, telegramBot);
                } else {
                    authenticatedUserService.deleteUser(beingAuthenticated);
                    messageContent = messageSource.getMessage(MuninnMessage.ERROR.name(),
                            new Object[]{TelegramOption.HELP.getOption()}, Locale.getDefault());
                }
            } else {
                throw new InvalidCommandException(command);
            }
        }

        if (!"".equals(messageContent.trim())) {
            message = new SendMessage()
                    .setChatId(update.getMessage().getChatId())
                    .enableHtml(true)
                    .disableWebPagePreview()
                    .setText(messageContent);

            logger.info("Message for command: {} generated as follows: {} ", command, message.getText());

            telegramBot.notify(message);
        } else {
            logger.warn("Generated message is empty: {}", messageContent);
        }
    }

    private String authenticate(Twitter twitter, Update update, AuthenticatedUser authenticatedUser) {
        String message;
        try {
            int userId = update.getMessage().getFrom().getId();

            RequestToken requestToken = twitter.getOAuthRequestToken();

            authenticatedUser.setBotStatus(TelegramBotStatus.AUTHENTICATING.getCode());
            authenticatedUser.setTelegramChatId(update.getMessage().getChatId());
            authenticatedUser.setTelegramUserId(userId);
            authenticatedUser.setTwitterRequestToken(requestToken.getToken());
            authenticatedUser.setTwitterRequestTokenSecret(requestToken.getTokenSecret());

            authenticatedUser = authenticatedUserService.saveAuthenticatedUser(authenticatedUser);
            logger.info("AuthenticatedUser saved: {}", authenticatedUser);

            message = messageSource.getMessage(MuninnMessage.ACTIVATE_BOT.name(), new Object[]{requestToken.getAuthorizationURL()},
                    Locale.forLanguageTag(update.getMessage().getFrom().getLanguageCode()));
        } catch (TwitterException e) {
            logger.error("An error occurred: ", e);
            message = messageSource.getMessage(MuninnMessage.ERROR.name(), new Object[0],
                    Locale.forLanguageTag(update.getMessage().getFrom().getLanguageCode()));
        }

        return message;
    }

    private String getAccessTokens(Twitter twitter, String pin, AuthenticatedUser authenticatedUser, TelegramBot telegramBot) {
        String message = "";
        try {
            RequestToken requestToken = new RequestToken(authenticatedUser.getTwitterRequestToken(),
                    authenticatedUser.getTwitterRequestTokenSecret());

            AccessToken authAccessToken = twitter.getOAuthAccessToken(requestToken, pin);
            twitter.setOAuthAccessToken(new AccessToken(authAccessToken.getToken(), authAccessToken.getTokenSecret()));

            User auth = twitter.showUser(twitter.verifyCredentials().getId());
            logger.info("User authenticated: {}", auth);

            authenticatedUser.setTwitterUserId(auth.getId());
            authenticatedUser.setTwitterToken(authAccessToken.getToken());
            authenticatedUser.setTwitterTokenSecret(authAccessToken.getTokenSecret());
            authenticatedUser.setLastNotifiedTime(new Date());

            if (validate(authenticatedUser, auth, telegramBot)) {
                authenticatedUser.setBotStatus(TelegramBotStatus.ACTIVE.getCode());
                message = messageSource.getMessage(MuninnMessage.BOT_ACTIVATED.name(), new Object[]{auth.getFriendsCount()},
                        Locale.getDefault());
                authenticatedUser = authenticatedUserService.saveAuthenticatedUser(authenticatedUser);
                logger.info("AuthenticatedUser updated: {}", authenticatedUser);
            } else {
                logger.error("User could not be validated: {}", auth.getScreenName());
            }
        } catch (TwitterException e) {
            logger.error("Error occurred: ", e);
            if (e.getStatusCode() == 401) {
                message = messageSource.getMessage(MuninnMessage.MISSING_OR_INCORRECT_PIN.name(),
                        new Object[]{TelegramOption.HELP.getOption()}, Locale.getDefault());
            } else {
                message = messageSource.getMessage(MuninnMessage.ERROR.name(), new Object[0], Locale.getDefault());
            }
            authenticatedUserService.deleteUser(authenticatedUser);
        }

        return message;
    }

    private boolean validate(AuthenticatedUser user, User twitterUser, TelegramBot telegramBot) {
        logger.info("Validate account: {}", twitterUser.getScreenName());

        for (AccountValidator validator : accountValidators) {
            if (!validator.validate(user, twitterUser, telegramBot)) {
                return false;
            }
        }

        return true;
    }

    private boolean notNullAndNotEmpty(String text) {
        return text != null && text.trim().length() != 0;
    }
}
