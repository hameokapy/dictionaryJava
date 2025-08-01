package util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.*;

public class LoggerUtil {
    public static Logger getLogger(Class<?> clazz) {
        Logger logger = Logger.getLogger(clazz.getName());

        // Tranh them handler nhieu lan
        if (logger.getHandlers().length == 0) {
            try {
                new File("logs").mkdirs();

                // Ghi log ra file log.txt ko bi rotate
                FileOutputStream fos = new FileOutputStream("logs/log.txt");
                StreamHandler sh = new StreamHandler(fos, new SimpleFormatter()) {
                    @Override
                    public synchronized void publish(LogRecord record) {
                        super.publish(record);
                        flush();  // bat buoc flush sau moi lan ghi de log hien ngay chu ko bi luu trong buffer
                    }
                };

                logger.addHandler(sh);
                logger.setUseParentHandlers(false); // de ko in ra console
                logger.setLevel(Level.ALL);

                logger.warning("testing!!");
            } catch (IOException e) {
                //not that consistent with logger usage :v
                System.err.println("Logger setup failed: " + e.getMessage());
            }
        }

        return logger;
    }
}
