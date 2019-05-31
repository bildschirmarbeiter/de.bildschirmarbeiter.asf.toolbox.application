package de.bildschirmarbeiter.asf.toolbox.application.message.system;

import de.bildschirmarbeiter.application.message.base.AbstractMessage;

public class LogMessage extends AbstractMessage {

    private final Level level;

    private final String message;

    public LogMessage(final Object source, final Level level, final String message) {
        super(source);
        this.level = level;
        this.message = message;
    }

    public Level getLevel() {
        return level;
    }

    public String getMessage() {
        return message;
    }

    public static LogMessage debug(final Object source, final String message) {
        return new LogMessage(source, Level.DEBUG, message);
    }

    public static LogMessage info(final Object source, final String message) {
        return new LogMessage(source, Level.INFO, message);
    }

    public static LogMessage warn(final Object source, final String message) {
        return new LogMessage(source, Level.WARN, message);
    }

    public static LogMessage error(final Object source, final String message) {
        return new LogMessage(source, Level.ERROR, message);
    }

    public enum Level {
        DEBUG,
        INFO,
        WARN,
        ERROR
    }

}
