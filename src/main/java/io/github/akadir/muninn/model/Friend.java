package io.github.akadir.muninn.model;

import io.github.akadir.muninn.enumeration.TwitterAccountStatus;
import twitter4j.User;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

/**
 * @author akadir
 * Date: 4.05.2020
 * Time: 22:04
 */
@Entity
@Table(name = "friend")
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
        friend.isAccountActive = Integer.parseInt(TwitterAccountStatus.ACTIVE.getCode());
        friend.lastChecked = new Date();

        return friend;
    }

    public Long getTwitterUserId() {
        return twitterUserId;
    }

    public void setTwitterUserId(Long twitterUserId) {
        this.twitterUserId = twitterUserId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }

    public int getIsAccountActive() {
        return isAccountActive;
    }

    public void setIsAccountActive(int isAccountActive) {
        this.isAccountActive = isAccountActive;
    }

    public Date getLastChecked() {
        return lastChecked;
    }

    public void setLastChecked(Date lastChecked) {
        this.lastChecked = lastChecked;
    }

}
