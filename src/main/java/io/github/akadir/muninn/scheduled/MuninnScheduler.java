package io.github.akadir.muninn.scheduled;

import io.github.akadir.muninn.TelegramBot;
import io.github.akadir.muninn.checker.UpdateChecker;
import io.github.akadir.muninn.model.AuthenticatedUser;
import io.github.akadir.muninn.scheduled.task.Huginn;
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
    @Scheduled(fixedDelay = 60_000 * 60 * 3, initialDelay = 1000 * 10)
    public void checkFriends() throws InterruptedException {
        long start = System.currentTimeMillis();
        logger.info("Scheduled task started");
        List<AuthenticatedUser> userList = authenticatedUserService.getActiveUsers();

        if (!userList.isEmpty()) {
            logger.info("Found {} users to check", userList.size());

            List<Muninn> muninns = runMuninn(userList);

            userList = runHuginn(muninns);

            for (AuthenticatedUser user : userList) {
                authenticatedUserService.updateUser(user);
                logger.info("User updated: twitter-id: {} db-id: {}", user.getTwitterUserId(), user.getId());
            }
        } else {
            logger.info("No user found.");
        }

        long executionTime = System.currentTimeMillis() - start;

        logger.info("Scheduled finished. duration: {}", executionTime);
    }

    private List<Muninn> runMuninn(List<AuthenticatedUser> users) throws InterruptedException {
        List<Muninn> muninns = new ArrayList<>();

        for (AuthenticatedUser user : users) {
            Muninn muninn = new Muninn(user, friendService, changeSetService, updateCheckers, telegramBot);
            muninn.start();
            logger.info("Muninn: {} started", muninn.getName());
            muninns.add(muninn);
        }

        for (Muninn m : muninns) {
            m.join();
            logger.info("Muninn: {} finished", m.getName());
        }

        return muninns;
    }

    private List<AuthenticatedUser> runHuginn(List<Muninn> muninns) throws InterruptedException {
        List<AuthenticatedUser> userList = new ArrayList<>();
        List<Huginn> huginns = new ArrayList<>();

        for (Muninn m : muninns) {
            Huginn huginn = new Huginn(m.getUser(), friendService, telegramBot);
            huginn.start();
            logger.info("Huginn: {} started", huginn.getName());
            huginns.add(huginn);
        }

        for (Huginn h : huginns) {
            h.join();
            logger.info("Huginn: {} finished", h.getName());
            userList.add(h.getUser());
        }

        return userList;
    }
}
