package io.github.akadir.muninn.model;

import io.github.akadir.muninn.enumeration.TwitterAccountStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import twitter4j.User;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author akadir
 * Date: 4.05.2020
 * Time: 22:04
 */
@Data
@Entity
@Table(name = "friend")
@EqualsAndHashCode(callSuper = true)
public class Friend extends BaseModel {
    @Column(name = "twitter_user_id")
    private Long twitterUserId;
    @Column(name = "username")
    private String username;
    @Column(name = "name")
    private String name;
    @Column(name = "bio")
    private String bio;
    @Column(name = "profile_pic_url")
    private String profilePicUrl;
    @Column(name = "is_account_active")
    private int isAccountActive;
    @Column(name = "last_checked")
    private Date lastChecked;

    public static Friend from(User user) {
        Friend friend = new Friend();

        friend.twitterUserId = user.getId();
        friend.username = user.getScreenName();
        friend.name = user.getName();
        friend.bio = user.getDescription();
        friend.profilePicUrl = user.get400x400ProfileImageURLHttps();
        friend.isAccountActive = TwitterAccountStatus.ACTIVE.getCode();
        friend.lastChecked = new Date();

        return friend;
    }

}
