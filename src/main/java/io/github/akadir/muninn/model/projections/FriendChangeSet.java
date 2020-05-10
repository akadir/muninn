package io.github.akadir.muninn.model.projections;

import org.springframework.beans.factory.annotation.Value;

import java.util.List;

/**
 * @author akadir
 * Date: 7.05.2020
 * Time: 22:14
 */
public interface FriendChangeSet {
    @Value("#{target.friendId}")
    Long getId();

    @Value("#{target.username}")
    String getUsername();

    @Value("#{target.twitterUserId}")
    String getTwitterUserId();

    int getChangeType();

    String getOldData();

    String getNewData();
}
