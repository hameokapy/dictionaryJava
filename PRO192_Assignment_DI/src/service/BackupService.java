package service;

import dal.BackupDataSource;
import util.diutils.Component;
import util.diutils.Injected;

@Component
public class BackupService {
    @Injected
    private BackupDataSource backupDataSource;

    //DI Manager dung default constructor, o day ko co constructor co tham so
    // thi ko can vi JVM tu tao cho

    public String backup(){
        return backupDataSource.backup() ? "Backup successful!" : "Backup failed!";
    }

    public String restore(){
        return backupDataSource.restore() ? "Restore successful!" : "Restore failed!";
    }
}
