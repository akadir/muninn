package io.github.akadir.muninn.operation;

import io.github.akadir.muninn.enumeration.TelegramOption;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * @author akadir
 * Date: 2.05.2020
 * Time: 23:04
 */
public interface Operation {
    TelegramOption getOption();

    SendMessage generateMessage(Update update);
}
