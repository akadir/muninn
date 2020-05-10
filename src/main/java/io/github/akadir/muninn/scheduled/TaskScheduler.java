package io.github.akadir.muninn.scheduled;

import io.github.akadir.muninn.TelegramBot;
import io.github.akadir.muninn.checker.UpdateChecker;
import io.github.akadir.muninn.enumeration.TelegramBotStatus;
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
import java.util.Set;

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
    private final Set<UpdateChecker> updateCheckers;

    @Autowired
    public TaskScheduler(AuthenticatedUserService authenticatedUserService, FriendService friendService,
                         ChangeSetService changeSetService, TelegramBot telegramBot, Set<UpdateChecker> updateCheckers) {
        this.authenticatedUserService = authenticatedUserService;
        this.friendService = friendService;
        this.changeSetService = changeSetService;
        this.telegramBot = telegramBot;
        this.updateCheckers = updateCheckers;
    }

    @Transactional
    @Scheduled(fixedDelay = 1000 * 60 * 30, initialDelay = 1000 * 3)
    public void checkFriends() throws InterruptedException {
        List<AuthenticatedUser> userList = authenticatedUserService.getUsersToCheck();
        List<Muninn> threads = new ArrayList<>();
        if (!userList.isEmpty()) {
            logger.info("Found {} users to check", userList.size());
            for (AuthenticatedUser user : userList) {
                Muninn muninn = new Muninn(user, friendService, changeSetService, updateCheckers, telegramBot);
                muninn.start();
                threads.add(muninn);
            }

            for (Muninn m : threads) {
                m.join();
                AuthenticatedUser user = m.getUser();
                user = authenticatedUserService.updateLastCheckedTime(user);
                if (user.getBotStatus() == TelegramBotStatus.ACTIVE.getCode()) {
                    Huginn huginn = new Huginn(user, authenticatedUserService, friendService, telegramBot,
                            m.getUnfollowedFriendList());
                    huginn.start();
                }
            }
        } else {
            logger.info("No user found.");
        }
    }
}
