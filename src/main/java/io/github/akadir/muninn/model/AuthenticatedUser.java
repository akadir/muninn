package io.github.akadir.muninn.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author akadir
 * Date: 3.05.2020
 * Time: 17:17
 */
@Entity
@Table(name = "authenticated_user")
public class AuthenticatedUser extends BaseModel {
    @Column(name = "twitter_user_id")
    private Long twitterUserId;
    @Column(name = "twitter_token")
    private String twitterToken;
    @Column(name = "twitter_token_secret")
    private String twitterTokenSecret;
    @Column(name = "twitter_request_token")
    private String twitterRequestToken;
    @Column(name = "twitter_request_token_secret")
    private String twitterRequestTokenSecret;
    @Column(name = "telegram_user_id")
    private Integer telegramUserId;
    @Column(name = "telegram_chat_id")
    private Long telegramChatId;
    @Column(name = "last_notified_time")
    private Date lastNotifiedTime;
    @Column(name = "last_checked_time")
    private Date lastCheckedTime;
    @Column(name = "bot_status")
    private int botStatus;

    public Long getTwitterUserId() {
        return twitterUserId;
    }

    public void setTwitterUserId(Long twitterUserId) {
        this.twitterUserId = twitterUserId;
    }

    public String getTwitterToken() {
        return twitterToken;
    }

    public void setTwitterToken(String twitterToken) {
        this.twitterToken = twitterToken;
    }

    public String getTwitterTokenSecret() {
        return twitterTokenSecret;
    }

    public void setTwitterTokenSecret(String twitterTokenSecret) {
        this.twitterTokenSecret = twitterTokenSecret;
    }

    public String getTwitterRequestToken() {
        return twitterRequestToken;
    }

    public void setTwitterRequestToken(String twitterRequestToken) {
        this.twitterRequestToken = twitterRequestToken;
    }

    public String getTwitterRequestTokenSecret() {
        return twitterRequestTokenSecret;
    }

    public void setTwitterRequestTokenSecret(String twitterRequestTokenSecret) {
        this.twitterRequestTokenSecret = twitterRequestTokenSecret;
    }

    public Integer getTelegramUserId() {
        return telegramUserId;
    }

    public void setTelegramUserId(Integer telegramUserId) {
        this.telegramUserId = telegramUserId;
    }

    public Long getTelegramChatId() {
        return telegramChatId;
    }

    public void setTelegramChatId(Long telegramChatId) {
        this.telegramChatId = telegramChatId;
    }

    public Date getLastNotifiedTime() {
        return lastNotifiedTime;
    }

    public void setLastNotifiedTime(Date lastNotifiedTime) {
        this.lastNotifiedTime = lastNotifiedTime;
    }

    public Date getLastCheckedTime() {
        return lastCheckedTime;
    }

    public void setLastCheckedTime(Date lastCheckedTime) {
        this.lastCheckedTime = lastCheckedTime;
    }

    public int getBotStatus() {
        return botStatus;
    }

    public void setBotStatus(int isBotActive) {
        this.botStatus = isBotActive;
    }
}
