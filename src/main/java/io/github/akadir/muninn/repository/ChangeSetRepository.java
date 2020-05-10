package io.github.akadir.muninn.repository;

import io.github.akadir.muninn.model.ChangeSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

/**
 * @author akadir
 * Date: 6.05.2020
 * Time: 00:07
 */
public interface ChangeSetRepository extends JpaRepository<ChangeSet, Long> {
    @Query(value = "select c from ChangeSet c " +
            "join UserFriend f on c.friendId = f.friend.id " +
            "where f.follower.id = ?1 and c.createdAt > ?2 order by c.friendId, c.changeType, c.createdAt")
    List<ChangeSet> findAllChangeSetForUserSinceLastNotifiedTime(long authenticatedUserId, Date lastNotifiedTime);
}
