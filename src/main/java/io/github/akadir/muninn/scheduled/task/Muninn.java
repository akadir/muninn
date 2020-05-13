package io.github.akadir.muninn.scheduled.task;

import io.github.akadir.muninn.TelegramBot;
import io.github.akadir.muninn.bot.TwitterBot;
import io.github.akadir.muninn.checker.UpdateChecker;
import io.github.akadir.muninn.config.ConfigParams;
import io.github.akadir.muninn.enumeration.TelegramBotStatus;
import io.github.akadir.muninn.enumeration.TwitterAccountState;
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
import org.apache.commons.lang3.ArrayUtils;
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
    private List<Friend> unfollowedFriendList;


    public Muninn(AuthenticatedUser user, FriendService friendService, ChangeSetService changeSetService,
                  Set<UpdateChecker> updateCheckerSet, TelegramBot telegramBot) {
        this.user = user;
        this.friendService = friendService;
        this.changeSetService = changeSetService;
        this.updateCheckerSet = updateCheckerSet;
        this.telegramBot = telegramBot;
        this.unfollowedFriendList = new ArrayList<>();
    }

    @Override
    public void run() {
        try {
            Twitter twitter = TwitterBot.getTwitter(user.getTwitterToken(), user.getTwitterTokenSecret());
            logger.info("Start to checking friend updates for user: {}", user.getTwitterUserId());
            List<Friend> friends = friendService.findUserFriendsToCheck(user.getId());
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
        Map<Long, Friend> friendIdFriendMap = friends.stream()
                .collect(Collectors.toMap(Friend::getTwitterUserId, Function.identity()));

        long cursor = -1;
        Set<Long> friendIdList = new HashSet<>();
        IDs friendIds;

        do {
            friendIds = TwitterBot.getFriendIDs(twitter, user, cursor);
            friendIdList.addAll(Arrays.asList(ArrayUtils.toObject(friendIds.getIDs())));
        } while ((cursor = friendIds.getNextCursor()) != 0);

        List<ChangeSet> changeSets = new ArrayList<>(checkForFriendUpdates(twitter, friendIdList, friendIdFriendMap));

        changeSetService.saveAll(user, changeSets);
    }

    private List<ChangeSet> checkForFriendUpdates(Twitter twitter, Set<Long> friendIds, Map<Long, Friend> userFriendsIDs) {
        List<ChangeSet> changeSets = new ArrayList<>();
        List<Friend> friendList = new ArrayList<>();
        List<UserFriend> newFollowings = new ArrayList<>();

        changeSets.addAll(checkCurrentFollowings(twitter, friendIds, userFriendsIDs, friendList));
        changeSets.addAll(checkNewFollowings(twitter, friendIds, userFriendsIDs, friendList, newFollowings));

        friendList.forEach(friend -> friend.setThreadAvailability(0));

        friendService.saveAllFriends(user, friendList);
        friendService.saveNewFollowings(user, newFollowings);

        return changeSets;
    }

    private List<ChangeSet> checkCurrentFollowings(Twitter twitter, Set<Long> friendIds, Map<Long, Friend> userFriendsIDs,
                                                   List<Friend> friendList) {
        List<ChangeSet> changeSets = new ArrayList<>();

        for (Map.Entry<Long, Friend> entry : userFriendsIDs.entrySet()) {
            Long friendId = entry.getKey();
            Friend friend = entry.getValue();
            logger.info("Check friend: {}", friend.getUsername());
            long hoursSinceLastCheck = DateTimeHelper.getTimeDifferenceInHoursSince(friend.getLastChecked());

            if (hoursSinceLastCheck < ConfigParams.RECHECK_PERIOD) {
                logger.info("User: {} checked {} hours ago. Will not be checked again for {} for now.",
                        friend.getUsername(), hoursSinceLastCheck, user.getTwitterUserId());
                continue;
            }

            try {
                User twitterFriend = TwitterBot.showUser(twitter, user, friendId);

                if (friendIds.contains(friendId)) {
                    changeSets.addAll(checkUpdates(friend, twitterFriend));
                    friendList.add(friend);
                } else {
                    logger.info("User: {} unfollowed {}", user.getTwitterUserId(), twitterFriend.getScreenName());
                    unfollowedFriendList.add(friend);
                }
            } catch (AccountStatusException e) {
                logger.error("User with id {} deactivated account or suspended", friendId);

                if (friend.getAccountState() != e.getAccountStatus().getCode()) {
                    changeSets.add(ChangeSet.changeStatus(friend, TwitterAccountState.of(friend.getAccountState()), e.getAccountStatus()));
                    friend.setLastChecked(new Date());
                    friend.setAccountState(e.getAccountStatus().getCode());
                    friendList.add(friend);
                }
            }
        }

        if (!unfollowedFriendList.isEmpty()) {
            changeSets = checkUnfollowedUsersStatuses(twitter, unfollowedFriendList);
        }

        return changeSets;
    }

    private List<ChangeSet> checkNewFollowings(Twitter twitter, Set<Long> friendIdsFetchedFromTwitter,
                                               Map<Long, Friend> userFriendsIDs, List<Friend> friendList, List<UserFriend> newFollowings) {
        List<ChangeSet> changeSets = new ArrayList<>();

        for (Long friendId : friendIdsFetchedFromTwitter) {
            if (!userFriendsIDs.containsKey(friendId)) {
                try {
                    User twitterFriend = TwitterBot.showUser(twitter, user, friendId);
                    logger.info("Found new following. User: {} followed {}", user.getTwitterUserId(), twitterFriend.getScreenName());

                    Optional<Friend> optionalFriend = friendService.findByTwitterUserId(friendId);

                    Friend f;
                    if (optionalFriend.isPresent()) {
                        f = optionalFriend.get();
                        changeSets.addAll(checkUpdates(f, twitterFriend));
                        logger.info("new followed friend already exist in db.");
                    } else {
                        f = Friend.from(twitterFriend);
                        logger.info("new followed friend not exist in our db");
                    }

                    UserFriend userFriend = UserFriend.from(user, f);
                    newFollowings.add(userFriend);
                    friendList.add(f);
                } catch (AccountStatusException e) {
                    logger.error("Exception occurred while fetching user details: {}", friendId, e);
                }
            }
        }

        return changeSets;
    }

    private List<ChangeSet> checkUpdates(Friend f, User u) {
        List<ChangeSet> listOfChanges = new ArrayList<>();

        for (UpdateChecker updateChecker : updateCheckerSet) {
            updateChecker.checkUpdate(f, u).ifPresent(listOfChanges::add);
        }

        f.setLastChecked(new Date());
        return listOfChanges;
    }

    public List<ChangeSet> checkUnfollowedUsersStatuses(Twitter twitter, List<Friend> unfollowedFriendList) {
        List<ChangeSet> changeSets = new ArrayList<>();

        for (Friend f : unfollowedFriendList) {
            try {
                TwitterBot.showUser(twitter, user, f.getTwitterUserId());
            } catch (AccountStatusException e) {
                logger.error("User with id {} deactivated account or suspended", f);

                if (f.getAccountState() != e.getAccountStatus().getCode()) {
                    changeSets.add(ChangeSet.changeStatus(f, TwitterAccountState.of(f.getAccountState()), e.getAccountStatus()));
                    f.setLastChecked(new Date());
                    f.setAccountState(e.getAccountStatus().getCode());
                }
            }
        }

        return changeSets;
    }

    public AuthenticatedUser getUser() {
        return user;
    }

    public List<Friend> getUnfollowedFriendList() {
        return unfollowedFriendList;
    }
}
