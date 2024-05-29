package Backend;

import java.util.logging.Logger;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;
import java.util.logging.Level;
import java.io.IOException;

public class AuditLogger {
    private static Logger logger = Logger.getLogger("AuditLogger");
    private static FileHandler fh;

    static {
        try {
            // Set up FileHandler to rotate logs
            fh = new FileHandler("AuditLog.log", 1024 * 1024, 10, true); // 1MB per file, up to 10 files
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
            logger.setLevel(Level.ALL);  // Log all levels
        } catch (SecurityException | IOException e) {
            logger.log(Level.SEVERE, "Error occurred in Logger setup", e);
        }
    }

    public static void log(String message) {
        logger.info(message);
    }
}
