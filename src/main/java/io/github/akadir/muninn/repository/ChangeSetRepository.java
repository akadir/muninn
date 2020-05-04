package io.github.akadir.muninn.repository;

import io.github.akadir.muninn.model.ChangeSet;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author akadir
 * Date: 6.05.2020
 * Time: 00:07
 */
public interface ChangeSetRepository extends JpaRepository<ChangeSet, Long> {
}
