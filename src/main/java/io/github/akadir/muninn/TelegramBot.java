package io.github.akadir.muninn;

import io.github.akadir.muninn.config.ConfigParams;
import io.github.akadir.muninn.exception.InvalidCommandException;
import io.github.akadir.muninn.operation.Operation;
import io.github.akadir.muninn.operation.OperationFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

/**
 * @author akadir
 * Date: 2.05.2020
 * Time: 22:08
 */
@Component
public class TelegramBot extends TelegramLongPollingBot {
    private final Logger logger = LoggerFactory.getLogger(TelegramBot.class);

    private final OperationFactory operationFactory;

    @Autowired
    public TelegramBot(OperationFactory operationFactory) {
        this.operationFactory = operationFactory;
    }

    static {
        Logger logger = LoggerFactory.getLogger("main");
        logger.info("Init telegram bot context");
        ApiContextInitializer.init();
    }

    public static void main(String[] args) throws TelegramApiRequestException {
        AnnotationConfigApplicationContext appContext = new AnnotationConfigApplicationContext();
        Logger logger = LoggerFactory.getLogger("main");
        logger.info("Init application");

        appContext.scan("io.github.akadir.muninn");
        appContext.refresh();

        TelegramBot telegramBot = (TelegramBot) appContext.getBean("telegramBot");
        TelegramBotsApi botsApi = new TelegramBotsApi();
        logger.info("Register our bot");
        botsApi.registerBot(telegramBot);

        logger.info("Register shutdown hook");
        appContext.registerShutdownHook();
    }

    @Override
    public void onUpdateReceived(Update update) {
        logger.info("Update received from: {}", update);

        if (update.hasMessage()) {
            try {
                Operation operation = operationFactory.getOperation(update.getMessage().getText());
                SendMessage message = operation.generateMessage(update);
                execute(message);
            } catch (TelegramApiRequestException e) {
                logger.error("TelegramApiRequestException: {}", e.getApiResponse(), e);
            } catch (TelegramApiException e) {
                logger.error("Telegram sending message error: ", e);
            } catch (InvalidCommandException e) {
                logger.error("Error occurred: ", e);
            }
        }
    }

    public void notify(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiRequestException e) {
            logger.error("TelegramApiRequestException: {}", e.getApiResponse(), e);
        } catch (TelegramApiException e) {
            logger.error("Telegram sending message error: ", e);
        } catch (InvalidCommandException e) {
            logger.error("Error occurred: ", e);
        }
    }

    @Override
    public String getBotUsername() {
        return ConfigParams.TELEGRAM_BOT_NAME;
    }

    @Override
    public String getBotToken() {
        return ConfigParams.TELEGRAM_TOKEN;
    }
}