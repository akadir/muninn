package io.github.akadir.muninn.model;

import twitter4j.IDs;
import twitter4j.RateLimitStatus;

/**
 * @author akadir
 * Date: 6.05.2020
 * Time: 19:08
 */
public class EmptyIDs implements IDs {
    @Override
    public long[] getIDs() {
        return new long[0];
    }

    @Override
    public boolean hasPrevious() {
        return false;
    }

    @Override
    public long getPreviousCursor() {
        return 0;
    }

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public long getNextCursor() {
        return 0;
    }

    @Override
    public RateLimitStatus getRateLimitStatus() {
        return null;
    }

    @Override
    public int getAccessLevel() {
        return 0;
    }
}
