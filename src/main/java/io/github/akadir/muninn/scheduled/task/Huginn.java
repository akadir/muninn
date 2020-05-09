package io.github.akadir.muninn.scheduled.task;

import io.github.akadir.muninn.TelegramBot;
import io.github.akadir.muninn.model.AuthenticatedUser;
import io.github.akadir.muninn.model.Change;
import io.github.akadir.muninn.model.projections.FriendChangeSet;
import io.github.akadir.muninn.service.AuthenticatedUserService;
import io.github.akadir.muninn.service.FriendService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.github.akadir.muninn.helper.Constants.TELEGRAM_MESSAGE_CHARACTER_LIMIT;

/**
 * @author akadir
 * Date: 7.05.2020
 * Time: 20:10
 */
public class Huginn extends Thread {
    private final Logger logger = LoggerFactory.getLogger(Huginn.class);

    private AuthenticatedUser user;
    private final AuthenticatedUserService authenticatedUserService;
    private final FriendService friendService;
    private final TelegramBot telegramBot;

    public Huginn(AuthenticatedUser user, AuthenticatedUserService authenticatedUserService,
                  FriendService friendService, TelegramBot telegramBot) {
        this.user = user;
        this.authenticatedUserService = authenticatedUserService;
        this.friendService = friendService;
        this.telegramBot = telegramBot;
    }

    @Override
    public void run() {
        super.run();
        notifyUser();
    }

    private void notifyUser() {
        List<FriendChangeSet> changeSetList = friendService
                .fetchAllChangeSetForUserSinceLastNotifiedTime(user.getId(), user.getLastNotifiedTime());

        if (changeSetList.isEmpty()) {
            return;
        }

        List<String> messages = new ArrayList<>();

        Map<String, List<FriendChangeSet>> changeSetMap = changeSetList.stream()
                .collect(Collectors.groupingBy(FriendChangeSet::getUsername, LinkedHashMap::new, Collectors.toList()));

        for (Map.Entry<String, List<FriendChangeSet>> entry : changeSetMap.entrySet()) {
            String username = entry.getKey();
            List<FriendChangeSet> changes = entry.getValue();


            Map<Integer, Change> uniqueChanges = new LinkedHashMap<>();


            for (FriendChangeSet friendChangeSet : changes) {
                if (uniqueChanges.containsKey(friendChangeSet.getChangeType())) {
                    Change change = uniqueChanges.get(friendChangeSet.getChangeType());
                    change.setNewData(friendChangeSet.getNewData());
                } else {
                    Change change = Change.from(friendChangeSet);
                    uniqueChanges.put(friendChangeSet.getChangeType(), change);
                }
            }


            generateMessage(username, uniqueChanges, messages);
        }

        logger.info("Generated message count: {}", messages.size());

        if (!messages.isEmpty()) {
            for (String message : messages) {
                SendMessage telegramMessage = new SendMessage()
                        .setChatId(user.getTelegramChatId())
                        .enableHtml(true)
                        .disableWebPagePreview()
                        .setText(message);

                telegramBot.notify(telegramMessage);
                logger.info("Message send: {}", message);
            }

            user = authenticatedUserService.updateUserNotifiedTime(user);
        }
    }

    private void generateMessage(String username, Map<Integer, Change> uniqueChanges, List<String> messages) {
        StringBuilder mb = new StringBuilder("\n\n<a href=\"https://twitter.com/")
                .append(username).append("\"><b>").append(username).append("</b></a> has changed: \n");

        for (Map.Entry<Integer, Change> entry : uniqueChanges.entrySet()) {
            mb.append("\n\n").append(entry.getValue());
        }

        mb.append("\n\n--------");

        String message = mb.toString();

        if (messages.isEmpty()) {
            messages.add(message);
        } else {
            String lastMessage = messages.get(messages.size() - 1);

            if (lastMessage.length() + message.length() > TELEGRAM_MESSAGE_CHARACTER_LIMIT) {
                messages.add(message);
            } else {
                lastMessage = lastMessage + message;
                messages.set(messages.size() - 1, lastMessage);
            }
        }
    }
}
