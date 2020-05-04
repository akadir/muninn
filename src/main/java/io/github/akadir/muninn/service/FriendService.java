package io.github.akadir.muninn.service;

import com.kadir.twitterbots.ratelimithandler.handler.RateLimitHandler;
import com.kadir.twitterbots.ratelimithandler.process.ApiProcessType;
import io.github.akadir.muninn.bot.TwitterBot;
import io.github.akadir.muninn.enumeration.ChangeType;
import io.github.akadir.muninn.enumeration.TwitterAccountStatus;
import io.github.akadir.muninn.exception.AccountSuspendedException;
import io.github.akadir.muninn.model.AuthenticatedUser;
import io.github.akadir.muninn.model.ChangeSet;
import io.github.akadir.muninn.model.Friend;
import io.github.akadir.muninn.model.UserFriend;
import io.github.akadir.muninn.repository.ChangeSetRepository;
import io.github.akadir.muninn.repository.FriendRepository;
import io.github.akadir.muninn.repository.UserFriendRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import twitter4j.IDs;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;
import twitter4j.auth.AccessToken;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author akadir
 * Date: 4.05.2020
 * Time: 21:52
 */
@Service
public class FriendService {
    private final Logger logger = LoggerFactory.getLogger(FriendService.class);

    private final AuthenticatedUserService authenticatedUserService;
    private final ChangeSetRepository changeSetRepository;
    private final UserFriendRepository userFriendRepository;
    private final FriendRepository friendRepository;

    @Autowired
    public FriendService(AuthenticatedUserService authenticatedUserService, ChangeSetRepository changeSetRepository,
                         UserFriendRepository userFriendRepository, FriendRepository friendRepository) {
        this.authenticatedUserService = authenticatedUserService;
        this.changeSetRepository = changeSetRepository;
        this.userFriendRepository = userFriendRepository;
        this.friendRepository = friendRepository;
    }

    //@Scheduled(fixedRate = 1000)
    public void task() {
        logger.info("I am running in fixed rate");
    }

    @Transactional
    @Scheduled(fixedRate = 60000 * 60 * 12)
    public void checkFriends() {
        List<AuthenticatedUser> userList = authenticatedUserService.getUsersToCheck();

        if (!userList.isEmpty()) {
            logger.info("Found {} users to check", userList.size());
            for (AuthenticatedUser user : userList) {
                Twitter twitter = TwitterBot.getTwitter();
                twitter.setOAuthAccessToken(new AccessToken(user.getTwitterToken(), user.getTwitterTokenSecret()));
                try {
                    User auth = twitter.showUser(twitter.verifyCredentials().getId());
                    IDs iDs = twitter.getFriendsIDs(auth.getId(), -1);
                    checkFriendsList(user, iDs.getIDs(), twitter);
                    logger.info("User following: {}", auth.getFriendsCount());
                    RateLimitHandler.handle(twitter.getId(), iDs.getRateLimitStatus(), ApiProcessType.GET_FRIENDS_IDS);
                } catch (TwitterException exception) {
                    logger.error("Exception: ", exception);
                }
            }
        } else {
            logger.info("No user found.");
        }
    }

    private void checkFriendsList(AuthenticatedUser authenticatedUser, long[] friendIds, Twitter twitter) {
        List<Friend> friends = friendRepository.findUserFriends(authenticatedUser.getId());
        List<UserFriend> newFriendRelations = new ArrayList<>();
        Map<Long, Friend> userFriendsIDs = friends.stream().collect(Collectors.toMap(Friend::getTwitterUserId, Function.identity()));
        List<ChangeSet> changeSets = new ArrayList<>();

        for (Long friendId : friendIds) {
            try {
                User user = twitter.showUser(friendId);
                if (userFriendsIDs.containsKey(friendId)) {
                    Friend f = userFriendsIDs.get(friendId);
                    changeSets.addAll(checkUpdates(f, user));
                    f.setLastChecked(new Date());
                } else {
                    Friend f = Friend.from(user);
                    friends.add(f);
                    //TODO update const
                    UserFriend userFriend = new UserFriend();
                    userFriend.setFollower(authenticatedUser);
                    userFriend.setFriend(f);
                    //TODO fix enum
                    userFriend.setIsRelationActive(1);
                    userFriendsIDs.put(friendId, f);
                    newFriendRelations.add(userFriend);
                }
                RateLimitHandler.handle(twitter.getId(), user == null ? null : user.getRateLimitStatus(), ApiProcessType.SHOW_USER);
            } catch (TwitterException e) {
                logger.error("Error while getting user information: {}", friendId, e);

                if (e.getStatusCode() == 50 || e.getStatusCode() == 63) {
                    if (userFriendsIDs.containsKey(friendId)) {
                        Friend f = userFriendsIDs.get(friendId);
                        changeSets.addAll(checkUpdates(f, null));
                        f.setLastChecked(new Date());
                    }
                } else if (e.getStatusCode() == 64) {
                    throw new AccountSuspendedException();
                }
            }
        }

        if (!changeSets.isEmpty()) {
            changeSets = changeSetRepository.saveAll(changeSets);
            logger.info("ChangeSets saved: {} for user with id: {}", changeSets.size(), authenticatedUser.getTwitterUserId());
        } else {
            logger.info("No change found for user with id: {}", authenticatedUser.getId());
        }

        friends = friendRepository.saveAll(friends);
        logger.info("Friends saved: {}", friends.size());
        newFriendRelations = userFriendRepository.saveAll(newFriendRelations);
        logger.info("New relations saved: {}", newFriendRelations);
    }

    private List<ChangeSet> checkUpdates(Friend f, User u) {
        List<ChangeSet> listOfChanges = new ArrayList<>();

        if (u == null) {
            ChangeSet changeSet = ChangeSet.changeStatus(f, TwitterAccountStatus.ACTIVE, TwitterAccountStatus.DEACTIVATED);
            listOfChanges.add(changeSet);
        } else {
            if (!u.getDescription().equals(f.getBio())) {
                ChangeSet changeSet = ChangeSet.change(f, f.getBio(), u.getDescription(), ChangeType.BIO);
                listOfChanges.add(changeSet);
            }

            if (!u.getName().equals(f.getName())) {
                ChangeSet changeSet = ChangeSet.change(f, f.getName(), u.getName(), ChangeType.NAME);
                listOfChanges.add(changeSet);
            }

            if (!u.getScreenName().equals(f.getUsername())) {
                ChangeSet changeSet = ChangeSet.change(f, f.getUsername(), u.getScreenName(), ChangeType.USERNAME);
                listOfChanges.add(changeSet);
            }

            if (!u.getBiggerProfileImageURL().equals(f.getProfilePicUrl())) {
                ChangeSet changeSet = ChangeSet.change(f, f.getProfilePicUrl(), u.getBiggerProfileImageURL(), ChangeType.PP);
                listOfChanges.add(changeSet);
            }

        }

        return listOfChanges;
    }

}
