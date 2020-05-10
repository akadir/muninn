package io.github.akadir.muninn.scheduled.task;

import io.github.akadir.muninn.TelegramBot;
import io.github.akadir.muninn.bot.TwitterBot;
import io.github.akadir.muninn.checker.UpdateChecker;
import io.github.akadir.muninn.config.ConfigParams;
import io.github.akadir.muninn.enumeration.TelegramBotStatus;
import io.github.akadir.muninn.enumeration.TwitterAccountStatus;
import io.github.akadir.muninn.exception.AccountSuspendedException;
import io.github.akadir.muninn.exception.TokenExpiredException;
import io.github.akadir.muninn.exception.base.AccountStatusException;
import io.github.akadir.muninn.helper.DateTimeHelper;
import io.github.akadir.muninn.model.AuthenticatedUser;
import io.github.akadir.muninn.model.ChangeSet;
import io.github.akadir.muninn.model.Friend;
import io.github.akadir.muninn.model.UserFriend;
import io.github.akadir.muninn.service.ChangeSetService;
import io.github.akadir.muninn.service.FriendService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import twitter4j.IDs;
import twitter4j.Twitter;
import twitter4j.User;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author akadir
 * Date: 6.05.2020
 * Time: 18:08
 */
public class Muninn extends Thread {
    private final Logger logger = LoggerFactory.getLogger(Muninn.class);

    private final AuthenticatedUser user;
    private final FriendService friendService;
    private final ChangeSetService changeSetService;
    private final Set<UpdateChecker> updateCheckerSet;
    private final TelegramBot telegramBot;


    public Muninn(AuthenticatedUser user, FriendService friendService, ChangeSetService changeSetService,
                  Set<UpdateChecker> updateCheckerSet, TelegramBot telegramBot) {
        this.user = user;
        this.friendService = friendService;
        this.changeSetService = changeSetService;
        this.updateCheckerSet = updateCheckerSet;
        this.telegramBot = telegramBot;
    }

    @Override
    public void run() {
        try {
            Twitter twitter = TwitterBot.getTwitter(user.getTwitterToken(), user.getTwitterTokenSecret());
            logger.info("Start to checking friend updates for user: {}", user.getTwitterUserId());
            List<Friend> friends = friendService.findUserFriends(user.getId());
            super.setName("muninn for: " + user.getTwitterUserId());

            checkUserFriends(twitter, friends);
            logger.info("Finish to checking friend updates for user: {}", user.getTwitterUserId());
        } catch (AccountSuspendedException | TokenExpiredException e) {
            logger.error("User token expired: {}", user.getId());

            user.setBotStatus(TelegramBotStatus.NOT_ACTIVE.getCode());

            String message = "Your twitter account logged out from <b>Muninn</b> bot.\n\n You should to login again to use <b>Muninn</b>.";

            SendMessage telegramMessage = new SendMessage()
                    .setChatId(user.getTelegramChatId())
                    .enableHtml(true)
                    .disableWebPagePreview()
                    .setText(message);

            telegramBot.notify(telegramMessage);
            logger.info("Message send: {}", message);
        }
    }

    private void checkUserFriends(Twitter twitter, List<Friend> friends) {
        Set<Long> currentFollowingSet = new HashSet<>();
        Map<Long, Friend> friendIdFriendMap = friends.stream()
                .collect(Collectors.toMap(Friend::getTwitterUserId, Function.identity()));

        long cursor = -1;
        IDs friendIds;

        do {
            friendIds = TwitterBot.getFriendIDs(twitter, user, cursor);
            checkForFriendUpdates(twitter, friendIds.getIDs(), friendIdFriendMap, currentFollowingSet);
        } while ((cursor = friendIds.getNextCursor()) != 0);

        checkUnfollows(currentFollowingSet, friendIdFriendMap);
    }

    private void checkUnfollows(Set<Long> currentFollowingSet, Map<Long, Friend> oldFollowingMap) {
        List<Long> unfollowedIdList = new ArrayList<>();

        for (Map.Entry<Long, Friend> entry : oldFollowingMap.entrySet()) {
            if (!currentFollowingSet.contains(entry.getKey())) {
                unfollowedIdList.add(entry.getValue().getId());
            }
        }

        if (!unfollowedIdList.isEmpty()) {
            friendService.unfollowFriends(user, unfollowedIdList);
        }
    }

    private void checkForFriendUpdates(Twitter twitter, long[] friendIds, Map<Long, Friend> userFriendsIDs,
                                       Set<Long> currentFriendIdSet) {
        List<ChangeSet> changeSets = new ArrayList<>();
        List<Friend> friendList = new ArrayList<>();
        List<UserFriend> newFollowings = new ArrayList<>();

        for (long friendId : friendIds) {
            try {
                User twitterFriend = TwitterBot.showUser(twitter, user, friendId);
                Friend f;
                logger.info("Check friend: {}", twitterFriend.getScreenName());

                if (userFriendsIDs.containsKey(friendId)) {
                    f = userFriendsIDs.get(friendId);
                    long hoursSinceLastCheck = DateTimeHelper.getTimeDifferenceInHoursSince(f.getLastChecked());

                    if (hoursSinceLastCheck < ConfigParams.RECHECK_PERIOD) {
                        logger.info("User: {} checked {} hours ago. Will not be checked again for {} for now.",
                                f.getUsername(), hoursSinceLastCheck, user.getTwitterUserId());
                        currentFriendIdSet.add(friendId);
                        continue;
                    }

                    changeSets.addAll(checkUpdates(f, twitterFriend));
                } else {
                    logger.info("Found new following. User: {} followed {}", user.getTwitterUserId(), twitterFriend.getScreenName());
                    Optional<Friend> optionalFriend = friendService.findByTwitterUserId(friendId);

                    if (optionalFriend.isPresent()) {
                        f = optionalFriend.get();
                        changeSets.addAll(checkUpdates(f, twitterFriend));
                        logger.info("new followed friend already exist in database.");
                    } else {
                        f = Friend.from(twitterFriend);
                        logger.info("new followed friend not exist in database.");
                    }

                    UserFriend userFriend = UserFriend.from(user, f);
                    newFollowings.add(userFriend);
                    userFriendsIDs.put(friendId, f);
                }

                friendList.add(f);
                currentFriendIdSet.add(friendId);
                userFriendsIDs.put(friendId, f);
            } catch (AccountStatusException e) {
                logger.error("User with id {} deactivated account or suspended", friendId);

                if (userFriendsIDs.containsKey(friendId)) {
                    Friend f = userFriendsIDs.get(friendId);
                    changeSets.add(ChangeSet.changeStatus(f, TwitterAccountStatus.of(f.getIsAccountActive()), e.getAccountStatus()));
                    f.setLastChecked(new Date());
                    f.setIsAccountActive(e.getAccountStatus().getCode());
                    friendList.add(f);
                }
            }
        }

        friendService.saveAllFriends(user, friendList);
        friendService.saveNewFollowings(user, newFollowings);
        changeSetService.saveAll(user, changeSets);
    }

    private List<ChangeSet> checkUpdates(Friend f, User u) {
        List<ChangeSet> listOfChanges = new ArrayList<>();

        for (UpdateChecker updateChecker : updateCheckerSet) {
            updateChecker.checkUpdate(f, u).ifPresent(listOfChanges::add);
        }

        f.setLastChecked(new Date());
        return listOfChanges;
    }

    public AuthenticatedUser getUser() {
        return user;
    }
}
