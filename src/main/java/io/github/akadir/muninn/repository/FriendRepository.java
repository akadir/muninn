package io.github.akadir.muninn.repository;

import io.github.akadir.muninn.model.Friend;
import io.github.akadir.muninn.model.projections.FriendChangeSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

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

    List<Friend> findAllByTwitterUserIdIn(List<Long> twitterUserIdList);

    @Query(value = "select f.id as friendId, f.username as username, f.twitter_user_id as twitterUserId," +
            " c.change_type as changeType, c.old_data as oldData, c.new_data as newData from change_set c " +
            "join friend f on f.id = c.friend_id " +
            "join user_friend uf on uf.friend_id = f.id " +
            "where uf.follower_id = ?1 order by f.id, c.change_type, c.created_at", nativeQuery = true)
    List<FriendChangeSet> findAllChangeSetForUserSinceLastNotifiedTime(long authenticatedUserId, Date lastNotifiedTime);
}
