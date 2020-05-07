package io.github.akadir.muninn.model;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author akadir
 * Date: 4.05.2020
 * Time: 21:58
 */
@Entity
@Table(name = "user_friend")
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

    public AuthenticatedUser getFollower() {
        return follower;
    }

    public void setFollower(AuthenticatedUser follower) {
        this.follower = follower;
    }

    public Friend getFriend() {
        return friend;
    }

    public void setFriend(Friend friend) {
        this.friend = friend;
    }

}
