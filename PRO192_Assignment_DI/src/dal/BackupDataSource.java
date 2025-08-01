package dal;

import util.LoggerUtil;
import util.diutils.Component;

import java.io.IOException;
import java.nio.file.*;
import java.util.logging.Logger;

import static dal.FileConstants.*;

@Component
public class BackupDataSource {
    private static final Logger logger = LoggerUtil.getLogger(BackupDataSource.class);

    //DI Manager dung default constructor, o day ko co constructor co tham so
    // thi ko can vi JVM tu tao cho

    public boolean backup(){
        try{
            Files.createDirectories(Paths.get(BACKUP_FOLDER));
            if (!Files.exists(Paths.get(DATA_FILE)) || !Files.exists(Paths.get(INDEX_FILE))) {
                logger.warning("Source data/index file not found. Backup aborted.");
                return false;
            }

            Files.copy(Paths.get(DATA_FILE), Paths.get(BACKUP_FOLDER+"data.bak"), StandardCopyOption.REPLACE_EXISTING);
            Files.copy(Paths.get(INDEX_FILE), Paths.get(BACKUP_FOLDER+"index.bak"), StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch(IOException e){
            logger.warning("Backup failed: " + e.getMessage());
            return false;
        }
    }

    public boolean restore(){
        try{
            if (!Files.exists(Paths.get(BACKUP_FOLDER, "data.bak"))
                    || !Files.exists(Paths.get(BACKUP_FOLDER, "index.bak"))) {
                logger.warning("Backup files not found. Restore aborted.");
                return false;
            }

            Files.copy(Paths.get(BACKUP_FOLDER+"data.bak"), Paths.get(DATA_FILE), StandardCopyOption.REPLACE_EXISTING);
            Files.copy(Paths.get(BACKUP_FOLDER+"index.bak"), Paths.get(INDEX_FILE), StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch(IOException e){
            logger.warning("Restore failed: " + e.getMessage());
            return false;
        }
    }
}
