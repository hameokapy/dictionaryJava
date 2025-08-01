package controller;

import model.User;
import service.BackupService;
import service.DictionaryService;
import service.UserService;
import util.diutils.Component;
import util.diutils.Injected;
import view.DictionaryView;

import java.util.List;

@Component
public class DictionaryController {
    @Injected
    private DictionaryService service;
    @Injected
    private UserService userService;
    @Injected
    private BackupService backupService;
    private DictionaryView view;
    private User currentUser;

//    public DictionaryController() {
        // empty constructor cho DI Manager
        // de the nay neu co constructor co tham so o duoi
        // ko thi ko can vi JVM tu tao default constructor
//    }

    //    DUNG CHO WITHOUT DI MANAGER!
//    public DictionaryController(DictionaryService dictionaryService, UserService userService,
//    BackupService backupService) {
//        this.service = dictionaryService;
//        this.userService = userService;
//        this.backupService = backupService;
//    }

    public void setView(DictionaryView dictionaryView) {
        this.view = dictionaryView;
    }

    public void handleLogout(){
        this.currentUser = null;
    }

    public boolean handleLogin(String username, String password){
        User user = userService.userLogin(username, password);
        if (user != null) {
            this.currentUser = user;
            return true;
        }
        return false;
    }
    public boolean isLoggedIn(){
    //them ham nay vi zero parameter dung cho view
        return this.currentUser != null;
    }
    public String getCurrentUserRole(){
        return currentUser!=null?currentUser.getRole():null;
    }
    private boolean hasRole(String...allowedRoles){
        if(currentUser==null) return false;
        for(String role : allowedRoles){
            if(currentUser.getRole().equals(role)) return true;
        }
        return false;
    }

    public void handleAdd(String word, String meaning){
        if (!hasRole("admin", "editor")) {
            view.show("Permission denied: You cannot add words.");
            return;
        }
        view.show(service.addWord(new model.Word(word, meaning)));
    }

    public void handleSearch(String word){
        view.show(service.searchMeaning(word));
    }

    public void handleModify(String word, String newMeaning){
        if (!hasRole("admin", "editor")) {
            view.show("Permission denied: You cannot modify words.");
            return;
        }
        view.show(service.modifyMeaning(word, newMeaning));
    }

    public void handleRemove(String word){
        if (!hasRole("admin", "editor")) {
            view.show("Permission denied: You cannot remove words.");
            return;
        }
        view.show(service.removeWord(word));
    }

    public void handleCleanIndex(){
        if (!hasRole("admin")) {
            view.show("Permission denied: DevMode is restricted to admin.");
            return;
        }
        view.show(service.cleanIndexFile());
    }

    public void handleCleanData(){
        if (!hasRole("admin")) {
            view.show("Permission denied: DevMode is restricted to admin.");
            return;
        }
        view.show(service.cleanDataFile());
    }

    public void handleDebugDisplayAll(){
        if (!hasRole("admin")) {
            view.show("Permission denied: Debugging is restricted to admin.");
            return;
        }
        List<String> lines = service.displayAllForDebugging();
        for (String line : lines) {
            view.show(line);
        }
    }

    public void handleBackup(){
        if (!hasRole("admin", "editor")) {
            view.show("Permission denied: You cannot backup data.");
            return;
        }
        view.show(backupService.backup());
    }

    public void handleRestore(){
        if (!hasRole("admin", "editor")) {
            view.show("Permission denied: You cannot restore data.");
            return;
        }
        view.show(backupService.restore());
    }
}
