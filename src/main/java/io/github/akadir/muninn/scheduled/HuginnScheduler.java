package io.github.akadir.muninn.scheduled;

import io.github.akadir.muninn.TelegramBot;
import io.github.akadir.muninn.enumeration.TelegramBotStatus;
import io.github.akadir.muninn.model.AuthenticatedUser;
import io.github.akadir.muninn.scheduled.task.Huginn;
import io.github.akadir.muninn.service.AuthenticatedUserService;
import io.github.akadir.muninn.service.FriendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

/**
 * @author akadir
 * Date: 21.05.2020
 * Time: 22:29
 */
@Component
public class HuginnScheduler extends TaskScheduler {

    @Autowired
    public HuginnScheduler(AuthenticatedUserService authenticatedUserService, FriendService friendService, TelegramBot telegramBot) {
        super(authenticatedUserService, friendService, telegramBot);
    }

    @Transactional
    @Scheduled(fixedDelay = 1000 * 60 * 60 * 3, initialDelay = 1000 * 30)
    public void notifyUsers() throws InterruptedException {
        List<AuthenticatedUser> userList = authenticatedUserService.getUsersToNotify();
        logger.info("Found {} users to notify", userList.size());
        List<Huginn> threads = new ArrayList<>();

        for (AuthenticatedUser user : userList) {
            if (user.getBotStatus() == TelegramBotStatus.ACTIVE.getCode()) {
                Huginn huginn = new Huginn(user, authenticatedUserService, friendService, telegramBot);
                huginn.start();
                threads.add(huginn);
            }
        }

        for (Huginn huginn : threads) {
            huginn.join();
        }
    }
}
