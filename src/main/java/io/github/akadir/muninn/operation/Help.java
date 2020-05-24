package io.github.akadir.muninn.operation;

import io.github.akadir.muninn.TelegramBot;
import io.github.akadir.muninn.enumeration.TelegramOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Locale;

/**
 * @author akadir
 * Date: 2.05.2020
 * Time: 23:44
 */
@Component("help")
public class Help implements Operation {
    private final Logger logger = LoggerFactory.getLogger(Help.class);

    private final MessageSource messageSource;
    private final TelegramBot telegramBot;

    @Autowired
    public Help(MessageSource messageSource, TelegramBot telegramBot) {
        this.messageSource = messageSource;
        this.telegramBot = telegramBot;
    }

    @Override
    public TelegramOption getOption() {
        return TelegramOption.HELP;
    }

    @Override
    public void handle(Update update) {
        StringBuilder stringBuilder = new StringBuilder();

        for (TelegramOption option : TelegramOption.values()) {
            stringBuilder.append(option.getOption())
                    .append(messageSource.getMessage(option.getOption(), new Object[0], Locale.getDefault()));
        }

        SendMessage message = new SendMessage()
                .setChatId(update.getMessage().getChatId())
                .disableWebPagePreview()
                .setText(stringBuilder.toString());

        logger.info("Message for update: {} generated as follows: {} ", update.getUpdateId(), message);

        telegramBot.notify(message);
    }
}
