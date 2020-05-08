package io.github.akadir.muninn.model;

import io.github.akadir.muninn.enumeration.ChangeType;
import io.github.akadir.muninn.helper.DiffHelper;
import io.github.akadir.muninn.model.projections.FriendChangeSet;

import static io.github.akadir.muninn.helper.Constants.NEW_TAG;
import static io.github.akadir.muninn.helper.Constants.OLD_TAG;

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

        if (ChangeType.BIO == changeType) {
            String change = DiffHelper.generateDiff(oldData, newData);
            return "<b>Bio:</b> " + change;
        } else if (ChangeType.ACCOUNT_STATUS == changeType) {
            return "<b>Status:</b>\n" + OLD_TAG + oldData + "\n" + NEW_TAG + newData;
        } else if (ChangeType.USERNAME == changeType) {
            String change = DiffHelper.generateDiff(oldData, newData);
            return "<b>Username:</b>  " + change;
        } else if (ChangeType.NAME == changeType) {
            String change = DiffHelper.generateDiff(oldData, newData);
            return "<b>Name:</b> " + change;
        } else if (ChangeType.PP == changeType) {
            return "<b>Profile Pic:</b> from <a href=\"" + oldData +
                    "\"><i>old</i></a> to <a href=\"" + newData + "\"><i>new</i></a>";
        }

        return "";
    }
}
