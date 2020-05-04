package io.github.akadir.muninn.repository;

import io.github.akadir.muninn.model.Friend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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
}
