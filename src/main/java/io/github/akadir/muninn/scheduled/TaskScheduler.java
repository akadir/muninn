package io.github.akadir.muninn.scheduled;

import io.github.akadir.muninn.TelegramBot;
import io.github.akadir.muninn.service.AuthenticatedUserService;
import io.github.akadir.muninn.service.FriendService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author akadir
 * Date: 6.05.2020
 * Time: 20:18
 */
public abstract class TaskScheduler {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    protected final AuthenticatedUserService authenticatedUserService;
    protected final FriendService friendService;
    protected final TelegramBot telegramBot;

    public TaskScheduler(AuthenticatedUserService authenticatedUserService, FriendService friendService, TelegramBot telegramBot) {
        this.authenticatedUserService = authenticatedUserService;
        this.friendService = friendService;
        this.telegramBot = telegramBot;
    }
}
