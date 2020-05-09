package io.github.akadir.muninn.scheduled.task;

import com.kadir.twitterbots.ratelimithandler.handler.RateLimitHandler;
import com.kadir.twitterbots.ratelimithandler.process.ApiProcessType;
import io.github.akadir.muninn.bot.TwitterBot;
import io.github.akadir.muninn.enumeration.ChangeType;
import io.github.akadir.muninn.enumeration.TwitterAccountStatus;
import io.github.akadir.muninn.exception.AccountSuspendedException;
import io.github.akadir.muninn.helper.DateTimeHelper;
import io.github.akadir.muninn.model.*;
import io.github.akadir.muninn.service.ChangeSetService;
import io.github.akadir.muninn.service.FriendService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.IDs;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;
import twitter4j.auth.AccessToken;

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

    public Muninn(AuthenticatedUser user, FriendService friendService, ChangeSetService changeSetService) {
        this.user = user;
        this.friendService = friendService;
        this.changeSetService = changeSetService;
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        Twitter twitter = getTwitter();
        logger.info("Start to checking friend updates for user: {}", user.getTwitterUserId());
        List<Friend> friends = friendService.findUserFriends(user.getId());
        super.setName("muninn for: " + user.getTwitterUserId());

        checkUserFriends(twitter, friends);
        logger.info("Finish to checking friend updates for user: {}", user.getTwitterUserId());
    }

    private void checkUserFriends(Twitter twitter, List<Friend> friends) {
        Set<Long> currentFollowingSet = new HashSet<>();
        Map<Long, Friend> friendIdFriendMap = friends.stream()
                .collect(Collectors.toMap(Friend::getTwitterUserId, Function.identity()));

        Optional<IDs> friendIds;
        long cursor = -1;

        do {
            friendIds = getFriendIDs(twitter, cursor);
            checkForFriendUpdates(twitter, friendIds.orElse(new EmptyIDs()).getIDs(), friendIdFriendMap, currentFollowingSet);
        } while ((cursor = friendIds.orElse(new EmptyIDs()).getNextCursor()) != 0);

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
                User twitterFriend = twitter.showUser(friendId);
                Friend f;
                logger.info("Check friend: {}", twitterFriend.getScreenName());
                if (userFriendsIDs.containsKey(friendId)) {
                    f = userFriendsIDs.get(friendId);
                    changeSets.addAll(checkUpdates(f, twitterFriend));
                    f.setLastChecked(new Date());
                } else {
                    f = Friend.from(twitterFriend);
                    userFriendsIDs.put(friendId, f);
                    logger.info("Found new following. User: {} followed {}", user.getTwitterUserId(), twitterFriend.getScreenName());
                    UserFriend userFriend = UserFriend.from(user, f);
                    newFollowings.add(userFriend);
                }

                friendList.add(f);
                currentFriendIdSet.add(friendId);
                userFriendsIDs.put(friendId, f);
                RateLimitHandler.handle(twitter.getId(), twitterFriend.getRateLimitStatus(), ApiProcessType.SHOW_USER);
            } catch (TwitterException e) {
                logger.error("Error while getting user information: {}", friendId, e);

                if (e.getStatusCode() == 64) {
                    throw new AccountSuspendedException();
                }

                if (userFriendsIDs.containsKey(friendId) && (e.getStatusCode() == 50 || e.getStatusCode() == 63)) {
                    Friend f = userFriendsIDs.get(friendId);
                    ChangeSet deactivateAccount = ChangeSet.changeStatus(f, TwitterAccountStatus.ACTIVE, TwitterAccountStatus.DEACTIVATED);
                    changeSets.add(deactivateAccount);
                    f.setLastChecked(new Date());
                    friendList.add(f);
                    userFriendsIDs.put(friendId, f);
                }
            }
        }

        friendService.saveAllFriends(user, friendList);
        friendService.saveNewFollowings(user, newFollowings);
        changeSetService.saveAll(user, changeSets);
    }

    private Twitter getTwitter() {
        Twitter twitter = TwitterBot.getTwitter();
        twitter.setOAuthAccessToken(new AccessToken(user.getTwitterToken(), user.getTwitterTokenSecret()));
        return twitter;
    }

    private Optional<IDs> getFriendIDs(Twitter twitter, long cursor) {
        IDs iDs = null;
        try {
            iDs = twitter.getFriendsIDs(user.getTwitterUserId(), cursor);
        } catch (TwitterException e) {
            logger.error("Error while fetching user friends: ", e);
            if (e.getStatusCode() == 64) {
                throw new AccountSuspendedException();
            }
        }
        return Optional.ofNullable(iDs);
    }

    private List<ChangeSet> checkUpdates(Friend f, User u) {
        List<ChangeSet> listOfChanges = new ArrayList<>();

        long hoursSinceLastCheck = DateTimeHelper.getTimeDifferenceInHoursSince(f.getLastChecked());

        if (hoursSinceLastCheck < 20) {
            logger.info("User: {} checked {} hours ago. Will not be checked again for {} for now.", f.getUsername(), hoursSinceLastCheck, user.getTwitterUserId());
            return listOfChanges;
        }

        if (!u.getDescription().equals(f.getBio())) {
            logger.info("User: {} has changed bio from: {} ||| to: {}", u.getScreenName(), f.getBio(), u.getDescription());
            ChangeSet changeSet = ChangeSet.change(f, f.getBio(), u.getDescription(), ChangeType.BIO);
            f.setBio(u.getDescription());
            listOfChanges.add(changeSet);
        }

        if (!u.getName().equals(f.getName())) {
            logger.info("User: {} has changed name from: {} ||| to: {}", u.getScreenName(), f.getName(), u.getName());
            ChangeSet changeSet = ChangeSet.change(f, f.getName(), u.getName(), ChangeType.NAME);
            f.setName(u.getName());
            listOfChanges.add(changeSet);
        }

        if (!u.getScreenName().equals(f.getUsername())) {
            logger.info("User has changed screen name from: {} ||| to: {}", f.getUsername(), u.getScreenName());
            ChangeSet changeSet = ChangeSet.change(f, f.getUsername(), u.getScreenName(), ChangeType.USERNAME);
            f.setUsername(u.getScreenName());
            listOfChanges.add(changeSet);
        }

        if (!u.get400x400ProfileImageURLHttps().equals(f.getProfilePicUrl())) {
            logger.info("User: {} has changed profile pic from: {} ||| to: {}", u.getScreenName(), f.getProfilePicUrl(), u.get400x400ProfileImageURLHttps());
            ChangeSet changeSet = ChangeSet.change(f, f.getProfilePicUrl(), u.get400x400ProfileImageURLHttps(), ChangeType.PP);
            f.setProfilePicUrl(u.get400x400ProfileImageURLHttps());
            listOfChanges.add(changeSet);
        }


        return listOfChanges;
    }

    public AuthenticatedUser getUser() {
        return user;
    }
}
