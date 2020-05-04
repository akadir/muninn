package io.github.akadir.muninn.repository;

import io.github.akadir.muninn.model.UserFriend;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author akadir
 * Date: 6.05.2020
 * Time: 00:40
 */
public interface UserFriendRepository extends JpaRepository<UserFriend, Long> {
    List<UserFriend> findAllByFollowerId(Long followerId);
}
