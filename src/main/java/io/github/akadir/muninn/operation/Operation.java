package io.github.akadir.muninn.operation;

import io.github.akadir.muninn.TelegramBot;
import io.github.akadir.muninn.enumeration.TelegramOption;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * @author akadir
 * Date: 2.05.2020
 * Time: 23:04
 */
public interface Operation {
    TelegramOption getOption();

    void handle(Update update, TelegramBot telegramBot);
}
