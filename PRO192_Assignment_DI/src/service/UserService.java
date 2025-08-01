package service;

import dal.UserDataSource;
import model.Credential;
import model.User;
import util.diutils.AfterCreation;
import util.diutils.Component;
import util.diutils.Injected;

import java.util.Map;

@Component
public class UserService {
    @Injected
    private UserDataSource userDataSource;
    private Map<String, Credential> users;

//DI Manager dung default constructor, o day ko co constructor co tham so
// thi ko can vi JVM tu tao cho

    @AfterCreation
//    can cai nay vi mot so cai not injected trong constructor ko bo dc
    public void init() {
        users = userDataSource.loadUsers();
    }

    public User userLogin(String username, String password) {
        Credential credential = users.get(username);
        if (credential != null && credential.getPassword().equals(password)) {
            return new User(username, credential.getRole());
        }
        return null;
    }

}
