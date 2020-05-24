package io.github.akadir.muninn.service;

import io.github.akadir.muninn.config.ConfigParams;
import io.github.akadir.muninn.model.AuthenticatedUser;
import io.github.akadir.muninn.model.Friend;
import io.github.akadir.muninn.model.UserFriend;
import io.github.akadir.muninn.model.projections.FriendChangeSet;
import io.github.akadir.muninn.repository.FriendRepository;
import io.github.akadir.muninn.repository.UserFriendRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author akadir
 * Date: 4.05.2020
 * Time: 21:52
 */
@Service
public class FriendService {
    private final Logger logger = LoggerFactory.getLogger(FriendService.class);

    private final UserFriendRepository userFriendRepository;
    private final FriendRepository friendRepository;

    @Autowired
    public FriendService(UserFriendRepository userFriendRepository, FriendRepository friendRepository) {
        this.userFriendRepository = userFriendRepository;
        this.friendRepository = friendRepository;
    }

    public List<Friend> findUserFriends(Long userId) {
        List<Friend> friends = friendRepository.findUserFriends(userId);
        logger.info("Found {} friends on db for user with id: {}", friends.size(), userId);
        return friends;
    }

    public List<Friend> findUserFriendsToCheck(Long userId) {
        UUID uuid = UUID.randomUUID();

        friendRepository.signFriendsToAvailableForFetch(userId, uuid.toString(), ConfigParams.RECHECK_PERIOD);
        logger.info("User: {} friends signed with id: {}", userId, uuid);

        List<Friend> friendsToCheck = friendRepository.findUserFriendsToCheck(uuid.toString());
        logger.info("Friend list with size {} fetched", friendsToCheck.size());

        return friendsToCheck;
    }

    public void saveAllFriends(AuthenticatedUser user, List<Friend> friends) {
        if (friends.isEmpty()) {
            return;
        }

        friends = friendRepository.saveAll(friends);

        logger.info("{} friends saved or updated for user with id: {}", friends.size(), user.getId());
    }

    public void saveNewFollowings(AuthenticatedUser user, List<UserFriend> newFollowings) {
        if (newFollowings.isEmpty()) {
            return;
        }

        newFollowings = userFriendRepository.saveAll(newFollowings);
        logger.info("{} new followings saved for user: {} ", newFollowings.size(), user.getId());
    }

    public void saveNewFollowing(AuthenticatedUser user, UserFriend newFollowing) {
        userFriendRepository.save(newFollowing);
        logger.info("New followings saved for user: {} ", user.getId());
    }

    public void unfollowFriends(AuthenticatedUser user, List<Long> unfollowedIdList) {
        userFriendRepository.unfollow(user.getId(), unfollowedIdList);
        logger.info("User {} unfollowed {} accounts", user.getId(), unfollowedIdList.size());
    }

    public List<FriendChangeSet> fetchAllChangeSetForUserSinceLastNotifiedTime(Long authenticatedUserId, Date lastNotifiedTime) {
        return friendRepository.findAllChangeSetForUserSinceLastNotifiedTime(authenticatedUserId, lastNotifiedTime);
    }

    public Optional<Friend> findByTwitterUserId(Long twitterUserId) {
        return friendRepository.findByTwitterUserId(twitterUserId);
    }

    public synchronized Friend saveFriend(Friend friend) {
        Optional<Friend> optionalFriend = friendRepository.findByTwitterUserId(friend.getTwitterUserId());

        return optionalFriend.orElseGet(() -> friendRepository.save(friend));
    }
}
