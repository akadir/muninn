package io.github.akadir.muninn.scheduled;

import io.github.akadir.muninn.TelegramBot;
import io.github.akadir.muninn.checker.update.UpdateChecker;
import io.github.akadir.muninn.checker.validity.AccountValidator;
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
 * Date: 21.05.2020
 * Time: 22:16
 */
@Component
public class MessengerScheduler {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final AuthenticatedUserService authenticatedUserService;
    private final FriendService friendService;
    private final TelegramBot telegramBot;
    private final ChangeSetService changeSetService;
    private final Set<UpdateChecker> updateCheckers;
    private final List<AccountValidator> validators;

    @Autowired
    public MessengerScheduler(AuthenticatedUserService authenticatedUserService, FriendService friendService,
                              ChangeSetService changeSetService, TelegramBot telegramBot, Set<UpdateChecker> updateCheckers,
                              List<AccountValidator> validators) {
        this.authenticatedUserService = authenticatedUserService;
        this.friendService = friendService;
        this.telegramBot = telegramBot;
        this.changeSetService = changeSetService;
        this.updateCheckers = updateCheckers;
        this.validators = validators;
    }

    @Transactional
    @Scheduled(fixedDelay = 60_000 * 60, initialDelay = 1000 * 10)
    public void checkFriends() throws InterruptedException {
        long start = System.currentTimeMillis();
        logger.info("Scheduled task started");

        List<AuthenticatedUser> userList = authenticatedUserService.getUsersToCheck();

        while (!userList.isEmpty()) {
            logger.info("Found {} users to check", userList.size());

            List<Muninn> muninns = runMuninn(userList);

            userList = runHuginn(muninns);

            for (AuthenticatedUser user : userList) {
                authenticatedUserService.updateUser(user);
                logger.info("User updated: twitter-id: {} db-id: {}", user.getTwitterUserId(), user.getId());
            }
        }

        long executionTime = System.currentTimeMillis() - start;

        logger.info("Scheduled task finished. duration in ms: {}", executionTime);
    }

    private List<Muninn> runMuninn(List<AuthenticatedUser> users) throws InterruptedException {
        List<Muninn> muninns = new ArrayList<>();

        for (AuthenticatedUser user : users) {
            Muninn muninn = new Muninn(user, friendService, changeSetService, updateCheckers, validators, telegramBot);
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
            Huginn huginn = new Huginn(m.getUser(), friendService, telegramBot, m.getUnfollows());
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
