package io.github.akadir.muninn.scheduled;

import io.github.akadir.muninn.TelegramBot;
import io.github.akadir.muninn.checker.UpdateChecker;
import io.github.akadir.muninn.model.AuthenticatedUser;
import io.github.akadir.muninn.scheduled.task.Muninn;
import io.github.akadir.muninn.service.AuthenticatedUserService;
import io.github.akadir.muninn.service.ChangeSetService;
import io.github.akadir.muninn.service.FriendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author akadir
 * Date: 21.05.2020
 * Time: 22:16
 */
@Component
public class MuninnScheduler extends TaskScheduler {
    private final ChangeSetService changeSetService;
    private final Set<UpdateChecker> updateCheckers;

    @Autowired
    public MuninnScheduler(AuthenticatedUserService authenticatedUserService, FriendService friendService,
                           ChangeSetService changeSetService, TelegramBot telegramBot, Set<UpdateChecker> updateCheckers) {
        super(authenticatedUserService, friendService, telegramBot);
        this.changeSetService = changeSetService;
        this.updateCheckers = updateCheckers;
    }

    @Transactional
    @Scheduled(fixedDelay = 1000 * 60 * 30, initialDelay = 1000 * 10)
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
                authenticatedUserService.updateLastCheckedTime(user);
            }
        } else {
            logger.info("No user found.");
        }
    }
}
