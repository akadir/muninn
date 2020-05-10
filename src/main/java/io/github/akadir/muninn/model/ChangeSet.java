package io.github.akadir.muninn.model;

import io.github.akadir.muninn.enumeration.ChangeType;
import io.github.akadir.muninn.enumeration.TwitterAccountState;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author akadir
 * Date: 4.05.2020
 * Time: 22:07
 */
@Data
@Entity
@Table(name = "change_set")
@EqualsAndHashCode(callSuper = true)
public class ChangeSet extends BaseModel {
    @Column(name = "friend_id")
    private Long friendId;
    @Column(name = "change_type")
    private int changeType;
    @Column(name = "old_data")
    private String oldData;
    @Column(name = "new_data")
    private String newData;

    private static ChangeSet from(Long friendId, String oldData, String newData) {
        ChangeSet changeSet = new ChangeSet();

        changeSet.setFriendId(friendId);
        changeSet.setOldData(oldData);
        changeSet.setNewData(newData);

        return changeSet;
    }

    public static ChangeSet changeStatus(Friend f, TwitterAccountState oldStatus, TwitterAccountState newStatus) {
        ChangeSet changeSet = from(f.getId(), "" + oldStatus.getCode(), "" + newStatus.getCode());

        changeSet.setChangeType(ChangeType.ACCOUNT_STATUS.getCode());

        return changeSet;
    }

    public static ChangeSet change(Friend f, String oldData, String newData, ChangeType changeType) {
        ChangeSet changeSet = from(f.getId(), oldData, newData);

        changeSet.setChangeType(changeType.getCode());

        return changeSet;
    }

}
