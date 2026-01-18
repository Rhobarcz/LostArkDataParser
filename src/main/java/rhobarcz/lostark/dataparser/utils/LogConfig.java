package rhobarcz.lostark.dataparser.utils;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public final class LogConfig {

    private static final DateTimeFormatter dtFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    private static volatile boolean initialized = false;

    private LogConfig() {
    }

    public static synchronized void init() throws IOException {
        if (initialized) {
            return;
        }

        final Formatter formatter = new Formatter() {
            @Override
            public String format(LogRecord record) {
                final String recordTime = Instant
                        .ofEpochMilli(record.getMillis())
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime()
                        .format(dtFormatter);

                return String.format(
                        "%s [%s] %s - %s%n",
                        recordTime,
                        record.getLevel(),
                        record.getSourceClassName(),
                        record.getMessage()
                );
            }
        };

        new File("logs").mkdirs();

        final FileHandler fileHandler = new FileHandler("logs/app.%g.log", 1_000_000, 4, true);
        fileHandler.setLevel(Level.ALL);
        fileHandler.setFormatter(formatter);

        final Logger logger = Logger.getLogger("");
        logger.setLevel(Level.ALL);

        for (Handler h : logger.getHandlers()) {
            logger.removeHandler(h);
        }

        logger.addHandler(fileHandler);

        initialized = true;
    }
}
