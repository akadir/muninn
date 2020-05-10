package io.github.akadir.muninn.model;

import io.github.akadir.muninn.enumeration.ChangeType;
import io.github.akadir.muninn.model.projections.FriendChangeSet;

/**
 * @author akadir
 * Date: 8.05.2020
 * Time: 00:11
 */
public class Change {
    private int type;
    private String oldData;
    private String newData;

    public static Change from(FriendChangeSet friendChangeSet) {
        Change change = new Change();

        change.type = friendChangeSet.getChangeType();
        change.oldData = friendChangeSet.getOldData();
        change.newData = friendChangeSet.getNewData();

        return change;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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

    @Override
    public String toString() {
        ChangeType changeType = ChangeType.of(type);

        String trimmedOldData = oldData;//.replaceAll("\\.", "\\\\.");
        String trimmedNewData = newData;//.replaceAll("\\.", "\\\\.");

        if (ChangeType.BIO == changeType) {
            return "bio from " + trimmedOldData + " to " + trimmedNewData;
        } else if (ChangeType.ACCOUNT_STATUS == changeType) {
            return "account from " + trimmedOldData + " to " + trimmedNewData;
        } else if (ChangeType.USERNAME == changeType) {
            return "username from " + trimmedOldData + " to " + trimmedNewData;
        } else if (ChangeType.NAME == changeType) {
            return "name from " + trimmedOldData + " to " + trimmedNewData;
        } else if (ChangeType.PP == changeType) {
            return "profile pic from [old](" + trimmedOldData + ") to [new](" + trimmedNewData + ")";
        }

        return "";
    }
}
