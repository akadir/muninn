package io.github.akadir.muninn.model;

import io.github.akadir.muninn.enumeration.ChangeType;
import io.github.akadir.muninn.enumeration.TwitterAccountStatus;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author akadir
 * Date: 4.05.2020
 * Time: 22:07
 */
@Entity
@Table(name = "change_set")
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

    public static ChangeSet changeStatus(Friend f, TwitterAccountStatus oldStatus, TwitterAccountStatus newStatus) {
        ChangeSet changeSet = from(f.getId(), "" + oldStatus.getCode(), "" + newStatus.getCode());

        changeSet.setChangeType(ChangeType.ACCOUNT_STATUS.getCode());

        return changeSet;
    }

    public static ChangeSet change(Friend f, String oldData, String newData, ChangeType changeType) {
        ChangeSet changeSet = from(f.getId(), oldData, newData);

        changeSet.setChangeType(changeType.getCode());

        return changeSet;
    }

    public Long getFriendId() {
        return friendId;
    }

    public void setFriendId(Long friendDataId) {
        this.friendId = friendDataId;
    }

    public int getChangeType() {
        return changeType;
    }

    public void setChangeType(int changeType) {
        this.changeType = changeType;
    }

    public String getOldData() {
        return oldData;
    }

    public void setOldData(String oldData) {
        this.oldData = oldData;
    }

    public String getNewData() {
        return newData;
    }

    public void setNewData(String newData) {
        this.newData = newData;
    }
}
