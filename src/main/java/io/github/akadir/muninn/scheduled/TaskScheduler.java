package io.github.akadir.muninn.scheduled;

import io.github.akadir.muninn.TelegramBot;
import io.github.akadir.muninn.helper.DateTimeHelper;
import io.github.akadir.muninn.model.AuthenticatedUser;
import io.github.akadir.muninn.scheduled.task.Huginn;
import io.github.akadir.muninn.scheduled.task.Muninn;
import io.github.akadir.muninn.service.AuthenticatedUserService;
import io.github.akadir.muninn.service.ChangeSetService;
import io.github.akadir.muninn.service.FriendService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

/**
 * @author akadir
 * Date: 6.05.2020
 * Time: 20:18
 */
@Component
public class TaskScheduler {
    private final Logger logger = LoggerFactory.getLogger(TaskScheduler.class);

    private final AuthenticatedUserService authenticatedUserService;
    private final FriendService friendService;
    private final ChangeSetService changeSetService;
    private final TelegramBot telegramBot;

    @Autowired
    public TaskScheduler(AuthenticatedUserService authenticatedUserService, FriendService friendService,
                         ChangeSetService changeSetService, TelegramBot telegramBot) {
        this.authenticatedUserService = authenticatedUserService;
        this.friendService = friendService;
        this.changeSetService = changeSetService;
        this.telegramBot = telegramBot;
    }

    @Transactional
    @Scheduled(fixedDelay = 1000 * 60 * 30, initialDelay = 1000 * 10)
    public void checkFriends() throws InterruptedException {
        List<AuthenticatedUser> userList = authenticatedUserService.getUsersToCheck();
        List<Muninn> threads = new ArrayList<>();
        if (!userList.isEmpty()) {
            logger.info("Found {} users to check", userList.size());
            for (AuthenticatedUser user : userList) {
                long sinceLastChecked = DateTimeHelper.getTimeDifferenceInHoursSince(user.getLastCheckedTime());
                if (sinceLastChecked > 6) {
                    Muninn muninn = new Muninn(user, friendService, changeSetService);
                    muninn.start();
                    threads.add(muninn);
                }
            }

            for (Muninn m : threads) {
                m.join();
                authenticatedUserService.updateLastCheckedTime(m.getUser());
                Huginn huginn = new Huginn(m.getUser(), authenticatedUserService, friendService, telegramBot);
                huginn.start();
            }
        } else {
            logger.info("No user found.");
        }
    }
}
