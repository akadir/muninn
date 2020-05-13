package io.github.akadir.muninn.repository;

import io.github.akadir.muninn.model.Friend;
import io.github.akadir.muninn.model.projections.FriendChangeSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author akadir
 * Date: 6.05.2020
 * Time: 00:44
 */
public interface FriendRepository extends JpaRepository<Friend, Long> {
    @Query("select f from Friend f " +
            "join UserFriend uf on f.id = uf.friend.id " +
            "join AuthenticatedUser au on au.id = uf.follower.id where au.id = ?1")
    List<Friend> findUserFriends(Long userId);

    @Modifying
    @Transactional
    @Query(value = "update friend f set thread_availability = 1, thread_id = ?2, check_start_time = now()" +
            "from user_friend as uf " +
            "where f.id = uf.friend_id and uf.follower_id = ?1 and (f.thread_availability is null or f.thread_availability = 0)", nativeQuery = true)
    void signFriendsToAvailableForFetch(Long userId, String threadId);

    @Query(value = "select * from friend where thread_id = ?1", nativeQuery = true)
    List<Friend> findUserFriendsToCheck(String threadId);

    List<Friend> findAllByTwitterUserIdIn(List<Long> twitterUserIdList);

    @Query(value = "select f.id as friendId, f.username as username, f.twitter_user_id as twitterUserId," +
            " c.change_type as changeType, c.old_data as oldData, c.new_data as newData from change_set c " +
            "join friend f on f.id = c.friend_id " +
            "join user_friend uf on uf.friend_id = f.id " +
            "where uf.follower_id = ?1 and c.created_at > ?2 order by f.id, c.change_type, c.created_at", nativeQuery = true)
    List<FriendChangeSet> findAllChangeSetForUserSinceLastNotifiedTime(long authenticatedUserId, Date lastNotifiedTime);

    Optional<Friend> findByTwitterUserId(Long twitterUserId);
}
