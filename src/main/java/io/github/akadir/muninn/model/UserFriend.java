package io.github.akadir.muninn.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author akadir
 * Date: 4.05.2020
 * Time: 21:58
 */
@Data
@Entity
@Table(name = "user_friend")
@EqualsAndHashCode(callSuper = true)
public class UserFriend extends BaseModel {
    @ManyToOne
    @JoinColumn(name = "follower_id")
    private AuthenticatedUser follower;
    @ManyToOne
    @JoinColumn(name = "friend_id")
    private Friend friend;

    public static UserFriend from(AuthenticatedUser user, Friend friend) {
        UserFriend userFriend = new UserFriend();

        userFriend.setFollower(user);
        userFriend.setFriend(friend);

        return userFriend;
    }

}
