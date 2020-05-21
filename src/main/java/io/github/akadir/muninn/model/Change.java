package io.github.akadir.muninn.model;

import io.github.akadir.muninn.enumeration.ChangeType;
import io.github.akadir.muninn.enumeration.TwitterAccountState;
import io.github.akadir.muninn.helper.DiffHelper;
import io.github.akadir.muninn.model.projections.FriendChangeSet;
import lombok.Data;

/**
 * @author akadir
 * Date: 8.05.2020
 * Time: 00:11
 */
@Data
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

    public static Change from(Change oldest, Change newer) {
        Change change = new Change();

        change.type = oldest.getType();
        change.oldData = oldest.getOldData();
        change.newData = newer.getNewData();

        return change;
    }

    @Override
    public String toString() {
        ChangeType changeType = ChangeType.of(type);

        if (ChangeType.BIO == changeType || ChangeType.USERNAME == changeType || ChangeType.NAME == changeType) {
            return DiffHelper.generateDiffGoogle(oldData, newData);
        } else if (ChangeType.ACCOUNT_STATUS == changeType) {
            TwitterAccountState newState = TwitterAccountState.of(Integer.parseInt(newData));
            TwitterAccountState oldState = TwitterAccountState.of(Integer.parseInt(oldData));
            return "<b>" + oldState.name() + "</b> ⟶ <b>" + newState.name() + "</b>";
        } else if (ChangeType.PROFILE_PIC == changeType) {
            return "<a href=\"" + oldData +
                    "\"><i>old</i></a> ⟶ <a href=\"" + newData + "\"><i>new</i></a>";
        }

        return "";
    }
}
