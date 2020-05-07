package io.github.akadir.muninn.repository;

import io.github.akadir.muninn.model.UserFriend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author akadir
 * Date: 6.05.2020
 * Time: 00:40
 */
public interface UserFriendRepository extends JpaRepository<UserFriend, Long> {
    List<UserFriend> findAllByFollowerId(Long followerId);

    @Modifying
    @Transactional
    @Query(value = "delete from user_friend where follower_id = ?1 and friend_id in ?2",
            nativeQuery = true)
    void unfollow(Long followerId, List<Long> friendId);

    @Query(value = "select user_friend where follower_id = ?1 and friend_id in ?2",
            nativeQuery = true)
    List<UserFriend> findAllByFollowerIdAndFriendId(Long followerId, Long friendId);
}
