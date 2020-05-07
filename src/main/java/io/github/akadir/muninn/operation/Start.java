package io.github.akadir.muninn.operation;

import io.github.akadir.muninn.bot.TwitterBot;
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

    @Autowired
    public Start(MessageSource messageSource, AuthenticatedUserService authenticatedUserService) {
        this.messageSource = messageSource;
        this.authenticatedUserService = authenticatedUserService;
    }

    @Override
    public TelegramOption getOption() {
        return TelegramOption.LOGIN;
    }

    @Override
    public SendMessage generateMessage(Update update) {
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
                messageContent = authenticate(twitter, update);
            }
        } else {
            Optional<AuthenticatedUser> optionalAuthenticatingUser = authenticatedUserService.findUserBeingAuthenticated(userId);

            if (optionalAuthenticatingUser.isPresent()) {
                AuthenticatedUser beingAuthenticated = optionalAuthenticatingUser.get();

                if (notNullAndNotEmpty(beingAuthenticated.getTwitterRequestToken())
                        && notNullAndNotEmpty(beingAuthenticated.getTwitterRequestTokenSecret())) {
                    messageContent = getAccessTokens(twitter, command, beingAuthenticated);
                } else {
                    authenticatedUserService.deleteUser(beingAuthenticated);
                    messageContent = messageSource.getMessage(MuninnMessage.ERROR.name(),
                            new Object[]{TelegramOption.HELP.getOption()}, Locale.getDefault());
                }
            } else {
                throw new InvalidCommandException(command);
            }
        }

        message = new SendMessage()
                .setChatId(update.getMessage().getChatId())
                .enableMarkdownV2(true)
                .disableWebPagePreview()
                .setText(messageContent);

        logger.info("Message for command: {} generated as follows: {} ", command, message.getText());

        return message;
    }

    private String authenticate(Twitter twitter, Update update) {
        String message;
        try {
            int userId = update.getMessage().getFrom().getId();

            RequestToken requestToken = twitter.getOAuthRequestToken();

            AuthenticatedUser authenticatedUser = new AuthenticatedUser();

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

    private String getAccessTokens(Twitter twitter, String pin, AuthenticatedUser authenticatedUser) {
        String message;
        try {
            RequestToken requestToken = new RequestToken(authenticatedUser.getTwitterRequestToken(),
                    authenticatedUser.getTwitterRequestTokenSecret());

            AccessToken authAccessToken = twitter.getOAuthAccessToken(requestToken, pin);
            twitter.setOAuthAccessToken(new AccessToken(authAccessToken.getToken(), authAccessToken.getTokenSecret()));

            User auth = twitter.showUser(twitter.verifyCredentials().getId());
            logger.info("User authenticated: {}", auth);

            authenticatedUser.setBotStatus(TelegramBotStatus.ACTIVE.getCode());
            authenticatedUser.setTwitterUserId(auth.getId());
            authenticatedUser.setTwitterToken(authAccessToken.getToken());
            authenticatedUser.setTwitterTokenSecret(authAccessToken.getTokenSecret());
            authenticatedUser.setLastNotifiedTime(new Date());

            authenticatedUser = authenticatedUserService.saveAuthenticatedUser(authenticatedUser);
            logger.info("AuthenticatedUser updated: {}", authenticatedUser);

            message = messageSource.getMessage(MuninnMessage.BOT_ACTIVATED.name(), new Object[]{auth.getFriendsCount()},
                    Locale.getDefault());
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

    private boolean notNullAndNotEmpty(String text) {
        return text != null && text.trim().length() != 0;
    }
}
