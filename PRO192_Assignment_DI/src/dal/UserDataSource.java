package dal;

import model.Credential;
import util.LoggerUtil;
import util.diutils.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import static dal.FileConstants.USER_FILE;

@Component
public class UserDataSource {
    private static final Logger logger = LoggerUtil.getLogger(UserDataSource.class);

    //DI Manager dung default constructor, o day ko co constructor co tham so
    // thi ko can vi JVM tu tao cho

    public Map<String, Credential> loadUsers() {
        Map<String, Credential> users = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(USER_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if(parts.length != 3){
                    logger.warning("Corrupted user loading!");
                }
                users.put(parts[0].trim(), new Credential(parts[1].trim(), parts[2].trim()));
            }
        } catch(IOException e){
            logger.warning("Error loading users: " + e.getMessage());
        }
        return users;
    }
}
