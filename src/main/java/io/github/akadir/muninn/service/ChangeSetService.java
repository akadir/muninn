package io.github.akadir.muninn.service;

import io.github.akadir.muninn.model.AuthenticatedUser;
import io.github.akadir.muninn.model.ChangeSet;
import io.github.akadir.muninn.repository.ChangeSetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author akadir
 * Date: 6.05.2020
 * Time: 18:17
 */
@Service
public class ChangeSetService {
    private final Logger logger = LoggerFactory.getLogger(ChangeSetService.class);

    private final ChangeSetRepository changeSetRepository;

    @Autowired
    public ChangeSetService(ChangeSetRepository changeSetRepository) {
        this.changeSetRepository = changeSetRepository;
    }

    public List<ChangeSet> saveAll(AuthenticatedUser user, List<ChangeSet> changeSets) {
        if (changeSets.isEmpty()) {
            return changeSets;
        }
        changeSets = changeSetRepository.saveAll(changeSets);
        logger.info("{} ChangeSets saved for user friends: {}", changeSets.size(), user.getId());
        return changeSets;
    }

    public List<ChangeSet> fetchAllChangeSetForUserSinceLastNotifiedTime(Long authenticatedUserId, Date lastNotifiedTime) {
        return changeSetRepository.findAllChangeSetForUserSinceLastNotifiedTime(authenticatedUserId, lastNotifiedTime);
    }
}
