package tms.util.logging;

import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * A custom {@link Formatter} implementation that adds color coding to console log output.
 * This formatter enhances log readability by applying different colors based on log levels:
 * <ul>
 *   <li><span style="color:red">RED</span> for SEVERE level messages</li>
 *   <li><span style="color:yellow">YELLOW</span> for WARNING level messages</li>
 *   <li><span style="color:green">GREEN</span> for all other level messages</li>
 * </ul>
 *
 * <p>The formatted output includes:
 * <ol>
 *   <li>Timestamp in yyyy-MM-dd HH:mm:ss.SSS format</li>
 *   <li>Log level</li>
 *   <li>Logger name</li>
 *   <li>Actual log message</li>
 * </ol>
 *
 */
public class ColorConsoleFormatter extends Formatter {

    /**
     * ANSI escape code to reset text formatting
     */
    private static final String RESET = "\u001B[0m";

    /**
     * ANSI escape code for green text
     */
    private static final String GREEN = "\u001B[32m";

    /**
     * ANSI escape code for yellow text
     */
    private static final String YELLOW = "\u001B[33m";

    /**
     * ANSI escape code for red text
     */
    private static final String RED = "\u001B[31m";

    /**
     * Formats a log record with color coding based on log level.
     *
     * @param record the log record to be formatted
     * @return a formatted log string with appropriate color coding
     *
     * @see java.util.logging.Formatter#format(java.util.logging.LogRecord)
     */
    @Override
    public String format(LogRecord record) {
        String color;
        if (record.getLevel() == Level.SEVERE) {
            color = RED;
        } else if (record.getLevel() == Level.WARNING) {
            color = YELLOW;
        } else {
            color = GREEN;
        }

        return String.format("%s%s %s [%s] %s%s%n",
                color,
                new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
                        .format(new java.util.Date(record.getMillis())),
                record.getLevel().getName(),
                record.getLoggerName(),
                formatMessage(record),
                RESET
        );
    }
}
