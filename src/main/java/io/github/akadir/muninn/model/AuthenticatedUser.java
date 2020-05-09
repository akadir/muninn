package io.github.akadir.muninn.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author akadir
 * Date: 3.05.2020
 * Time: 17:17
 */
@Data
@Entity
@Table(name = "authenticated_user")
@EqualsAndHashCode(callSuper = true)
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

}
