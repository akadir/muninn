package io.github.akadir.muninn.converter;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

/**
 * @author akadir
 * Date: 4.05.2020
 * Time: 02:08
 */
public class LogMessageConverter extends ClassicConverter {

    @Override
    public String convert(ILoggingEvent event) {
        String formattedMessage = event.getFormattedMessage();
        if (event.getLevel().levelInt <= Level.INFO.levelInt) {
            formattedMessage = formattedMessage.replaceAll("\n", "\\\\n");
        }
        return formattedMessage;
    }
}
