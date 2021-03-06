package io.github.akadir.muninn.service;

import io.github.akadir.muninn.config.ConfigParams;
import io.github.akadir.muninn.enumeration.TelegramBotStatus;
import io.github.akadir.muninn.model.AuthenticatedUser;
import io.github.akadir.muninn.repository.AuthenticatedUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author akadir
 * Date: 3.05.2020
 * Time: 19:36
 */
@Service
public class AuthenticatedUserService {
    private final AuthenticatedUserRepository authenticatedUserRepository;

    @Autowired
    public AuthenticatedUserService(AuthenticatedUserRepository authenticatedUserRepository) {
        this.authenticatedUserRepository = authenticatedUserRepository;
    }

    public AuthenticatedUser saveAuthenticatedUser(AuthenticatedUser authenticatedUser) {
        return authenticatedUserRepository.save(authenticatedUser);
    }

    public Optional<AuthenticatedUser> findUserBeingAuthenticated(Integer telegramUserId) {
        return authenticatedUserRepository.findByTelegramUserIdAndBotStatus(telegramUserId, TelegramBotStatus.AUTHENTICATING.getCode());
    }

    public Optional<AuthenticatedUser> findActiveByTelegramUserId(Integer telegramUserId) {
        return authenticatedUserRepository.findByTelegramUserIdAndBotStatus(telegramUserId, TelegramBotStatus.ACTIVE.getCode());
    }

    public AuthenticatedUser setUserNotActive(AuthenticatedUser authenticatedUser) {
        authenticatedUser.setBotStatus(TelegramBotStatus.NOT_ACTIVE.getCode());
        return authenticatedUserRepository.save(authenticatedUser);
    }

    public void deleteUser(AuthenticatedUser authenticatedUser) {
        authenticatedUserRepository.delete(authenticatedUser);
    }

    public List<AuthenticatedUser> getUsersToCheck() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, -ConfigParams.RECHECK_PERIOD);
        Date recheckedPeriodHoursBefore = calendar.getTime();
        return authenticatedUserRepository.findTop20ByBotStatusAndLastCheckedTimeLessThanEqual(TelegramBotStatus.ACTIVE.getCode(), recheckedPeriodHoursBefore);
    }

    public List<AuthenticatedUser> getActiveUsers() {
        return authenticatedUserRepository.findAllByBotStatus(TelegramBotStatus.ACTIVE.getCode());
    }

    public List<AuthenticatedUser> getUsersToNotify() {
        return authenticatedUserRepository.findAllByBotStatus(TelegramBotStatus.ACTIVE.getCode());
    }

    public AuthenticatedUser updateUser(AuthenticatedUser user) {
        return authenticatedUserRepository.save(user);
    }

    public AuthenticatedUser updateUserNotifiedTime(AuthenticatedUser user) {
        user.setLastNotifiedTime(new Date());
        return authenticatedUserRepository.save(user);
    }

    public AuthenticatedUser updateLastCheckedTime(AuthenticatedUser user) {
        user.setLastCheckedTime(new Date());
        return authenticatedUserRepository.save(user);
    }
}
