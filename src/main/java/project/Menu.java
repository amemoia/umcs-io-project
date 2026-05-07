package project;

import project.models.User;
import java.util.Scanner;

public class Menu {

    private EMenuState state = EMenuState.SelectLoginRegister;
    private Backend backend;
    private User loggedInUser;

    private final Scanner scanner = new Scanner(System.in);

    public Menu(Backend backend) {
        this.backend = backend;
    }

    public void display() {
        boolean running = true;
        while (running) {
            switch (state) {
                case SelectLoginRegister -> running = showSelectLoginRegister();
                case Login               -> showLogin();
                case Register            -> showRegister();
                case Main                -> showMain();
                case HeroTierlist        -> showHeroTierlist();
                case GameImport          -> showGameImport();
            }
        }
        System.out.println("Goodbye!");
    }

    private boolean showSelectLoginRegister() {
        printHeader("Welcome");
        System.out.println("  [1] Login");
        System.out.println("  [2] Register");
        System.out.println("  [0] Exit");
        printSeparator();

        switch (prompt()) {
            case "1" -> state = EMenuState.Login;
            case "2" -> state = EMenuState.Register;
            case "0" -> { return false; }
            default  -> printError("Invalid option.");
        }
        return true;
    }

    private void showLogin() {
        printHeader("Login");
        System.out.print("  Username : ");
        String username = scanner.nextLine().trim();
        System.out.print("  Password : ");
        String password = scanner.nextLine().trim();

        try {
            loggedInUser = backend.login(username, password);
            state = EMenuState.Main;
        } catch (Exception e) {
            printError("Login failed: " + e.getMessage());
            state = EMenuState.SelectLoginRegister;
        }
    }

    private void showRegister() {
        printHeader("Register");
        System.out.print("  Username : ");
        String username = scanner.nextLine().trim();
        System.out.print("  Password : ");
        String password = scanner.nextLine().trim();

        try {
            backend.register(username, password);
        } catch (Exception e) {
            printError("Registration failed: " + e.getMessage());
        }
        state = EMenuState.SelectLoginRegister;
    }

    private void showMain() {
        printHeader("Main Menu  |  " + loggedInUser.username
                + (loggedInUser.getIsAdmin() ? "  [ADMIN]" : ""));

        System.out.println("  [1] Hero Tierlist");
        System.out.println("  [2] Import Games");
        System.out.println("  [3] Update Hero Tierlist");

        if (loggedInUser.getIsAdmin()) {
            System.out.println("  [4] Admin panel");
        }

        System.out.println("  [0] Logout");
        printSeparator();

        switch (prompt()) {
            case "1" -> state = EMenuState.HeroTierlist;
            case "2" -> state = EMenuState.GameImport;
            case "3" -> {
                try {
                    backend.updateHeroTierlist();
                } catch (Exception e) {
                    printError("Update failed: " + e.getMessage());
                }
            }
            case "4" -> {
                if (loggedInUser.getIsAdmin()) {
                    System.out.println("  [Admin panel not implemented yet]");
                } else {
                    printError("Invalid option.");
                }
            }
            case "0" -> logout();
            default  -> printError("Invalid option.");
        }
    }

    private void showHeroTierlist() {
        printHeader("Hero Tierlist");
        try {
            backend.getHeroTierlist();
        } catch (Exception e) {
            printError("Could not load tierlist: " + e.getMessage());
        }
        System.out.println("  [0] Back");
        printSeparator();
        if (prompt().equals("0")) {
            state = EMenuState.Main;
        }
    }

    private void showGameImport() {
        printHeader("Import Games");
        System.out.println("  Enter path to games JSON file:");
        System.out.print("  > ");
        String path = scanner.nextLine().trim();

        if (path.isEmpty()) {
            printError("No path entered. Import cancelled.");
            state = EMenuState.Main;
            return;
        }

        try {
            backend.importGames(path);
        } catch (Exception e) {
            printError("Import failed: " + e.getMessage());
        }
        state = EMenuState.Main;
    }

    public void logout() {
        loggedInUser = null;
        state = EMenuState.SelectLoginRegister;
    }

    private String prompt() {
        System.out.print("  > ");
        return scanner.nextLine().trim();
    }

    private void printHeader(String title) {
        System.out.println();
        System.out.println("╔══════════════════════════════════════╗");
        System.out.printf( "║  %-36s║%n", title);
        System.out.println("╠══════════════════════════════════════╣");
    }

    private void printSeparator() {
        System.out.println("╚══════════════════════════════════════╝");
    }

    private void printError(String msg) {
        System.out.println("\n  [!] " + msg);
    }
}