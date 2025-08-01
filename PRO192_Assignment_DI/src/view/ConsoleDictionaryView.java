package view;

import controller.DictionaryController;
import util.diutils.AfterCreation;
import util.diutils.Component;
import util.diutils.Injected;

import java.util.Scanner;

@Component
public class ConsoleDictionaryView implements DictionaryView {
    @Injected
    private DictionaryController controller;
    private final Scanner sc = new Scanner(System.in, "UTF-8");

    @AfterCreation
//    can cai nay vi mot so cai not injected trong constructor ko bo dc
    public void init() {
        controller.setView(this);
    }

//    public ConsoleDictionaryView() {
        // empty constructor cho DI Manager
        // de the nay neu co constructor co tham so o duoi
        // ko thi ko can vi JVM tu tao default constructor
//    }

    @Override
    public void show(String message){
        System.out.println(message);
    }

    @Override
    public void start(){
        System.out.println("Welcome to Anh-Viet Dictionary!");

        while(!controller.isLoggedIn()){
            System.out.print("Username: ");
            String username = sc.nextLine();
            System.out.print("Password: ");
            String password = sc.nextLine();
            if(controller.handleLogin(username, password)){
                System.out.println("Logged in, your role: " + controller.getCurrentUserRole());
                boolean continueProgram = menuLoop();
                if(continueProgram){
                    System.out.println("Program exiting...");
                    break;
                } else {
                    System.out.println("Logging you out...");
                    controller.handleLogout();
                }
            }
            else
                System.out.println("Invalid credentials. Try again!");
        }
    }

    private boolean menuLoop(){
        String word, meaning, choice, input;
        char option;

        while(true){
            printMenu();
            System.out.print("Enter here: ");
            choice = sc.nextLine().trim();
            switch (choice) {
                case "0":
                    return true; //exit program

                case "1":
                    if (!controller.getCurrentUserRole().matches("admin|editor")) {
                        show("Permission denied: You cannot add words.");
                        break;
                    }
                    option = 'y';
                    while (option != 'n') {
                        if (option == 'y') {
                            System.out.print("Enter a word: ");
                            word = sc.nextLine();
                            System.out.print("Enter its meaning: ");
                            meaning = sc.nextLine();
                            controller.handleAdd(word, meaning);
                        }
                        while (true) {
                            System.out.print("Continue adding (y/n)? ");
                            input = sc.nextLine().trim().toLowerCase();
                            if (input.isEmpty()) {
                                System.out.println("Input cannot be empty!");
                                continue;
                            }
                            option = input.charAt(0);
                            if (option == 'y' || option == 'n') break;
                            System.out.println("Invalid choice. Please enter only 'y' or 'n'.");
                        }
                    }
                    break;

                case "x":
                case "X":
                    if (!controller.getCurrentUserRole().matches("admin")) {
                        show("Permission denied: Debugging is for admin.");
                        break;
                    }
                    System.out.println("Here is the list:");
                    controller.handleDebugDisplayAll();
                    break;

                case "2":
                    System.out.print("Enter word to search: ");
                    word = sc.nextLine();
                    System.out.print("Result: ");
                    controller.handleSearch(word);
                    break;

                case "3":
                    if (!controller.getCurrentUserRole().matches("admin|editor")) {
                        show("Permission denied: You cannot remove words.");
                        break;
                    }
                    System.out.print("Enter word to remove: ");
                    word = sc.nextLine();
                    controller.handleRemove(word);
                    break;

                case "4":
                    if (!controller.getCurrentUserRole().matches("admin|editor")) {
                        show("Permission denied: You cannot modify words.");
                        break;
                    }
                    System.out.print("Enter word to modify: ");
                    word = sc.nextLine();
                    System.out.print("Enter new meaning: ");
                    String newMeaning = sc.nextLine();
                    controller.handleModify(word, newMeaning);
                    break;

                case "5":
                    if (!controller.getCurrentUserRole().matches("admin")) {
                        show("Permission denied: DevMode is for admin.");
                        break;
                    }
                    System.out.print("Enter password (2nd layer): ");
                    input = sc.nextLine();
                    if (!input.equals("he200164")) {
                        System.out.println("Wrong password! Access denied!");
                        break;
                    }
                    while (true) {
                        System.out.println("Press 0. Return.");
                        System.out.println("Press 1. Clean index file.");
                        System.out.println("Press 2. Clean data file.");
                        System.out.print("Enter here: ");
                        input = sc.nextLine().trim();
                        switch (input) {
                            case "0":
                                System.out.println("Returned!");
                                break;
                            case "1":
                                controller.handleCleanIndex();
                                break;
                            case "2":
                                controller.handleCleanData();
                                break;
                            default:
                                System.out.println("Invalid input!");
                        }
                        if (input.equals("0")) break;
                    }
                    break;

                case "6":
                    if (!controller.getCurrentUserRole().matches("admin|editor")) {
                        show("Permission denied: You cannot backup data.");
                        break;
                    }
                    controller.handleBackup();
                    break;

                case "7":
                    if (!controller.getCurrentUserRole().matches("admin|editor")) {
                        show("Permission denied: You cannot restore data.");
                        break;
                    }
                    controller.handleRestore();
                    break;

                case "8":
                    return false; //logout!!!

                default:
                    System.out.println("Invalid input!");
            }
        }
    }
    private void printMenu() {
        System.out.println("");
        System.out.println("Press key to begin:");
        System.out.println("  Press 0. Exit.");
        System.out.println("  Press 1. Add a new word.");
        System.out.println("  Press 2. Search a word.");
        System.out.println("  Press 3. Remove a word.");
        System.out.println("  Press 4. Modify meaning.");
        System.out.println("  Press 5. Developer mode.");
        System.out.println("  Press 6. Backup data.");
        System.out.println("  Press 7. Restore data.");
        System.out.println("  Press 8. Logout.");
        System.out.println("  Press X. Display All (for debug).");
    }

}
