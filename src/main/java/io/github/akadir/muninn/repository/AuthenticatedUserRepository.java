package io.github.akadir.muninn.repository;

import io.github.akadir.muninn.model.AuthenticatedUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author akadir
 * Date: 3.05.2020
 * Time: 19:35
 */
public interface AuthenticatedUserRepository extends JpaRepository<AuthenticatedUser, Long> {
    Optional<AuthenticatedUser> findByTelegramUserIdAndBotStatus(Integer telegramUserId, int botStatus);

    @Query("select u from AuthenticatedUser u where u.botStatus = ?1 " +
            "and (u.lastCheckedTime < ?2 or u.lastCheckedTime is null)")
    List<AuthenticatedUser> findUsersToCheckFriends(int isBotActive, Date date);

    List<AuthenticatedUser> findAllByBotStatus(int botStatus);
}
