package io.github.akadir.muninn.exception;

import twitter4j.TwitterException;

/**
 * @author akadir
 * Date: 10.05.2020
 * Time: 16:40
 */
public class ProxyTwitterException extends RuntimeException {
    public ProxyTwitterException(TwitterException e) {
        super(e);
    }
}
