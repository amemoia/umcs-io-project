package project;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

import project.models.EMenuState;
import project.models.Hero;
import project.models.HeroTierlist;
import project.models.User;

public class Menu {
    private final Scanner scanner;
    private EMenuState state = EMenuState.SelectLoginRegister;
    private final Backend backend;
    private User loggedInUser;
    private boolean running = true;

    public Menu(Backend backend) {
        this.backend = backend;
        this.scanner = new Scanner(System.in);
    }

    public void display() {
        while (running) {
            switch (state) {
                case SelectLoginRegister -> showWelcomeMenu();
                case Login -> handleLogin();
                case Register -> handleRegister();
                case Main -> showMainMenu();
                case HeroTierlist -> showHeroTierlist();
                case GameImport -> importGames();
            }
        }
    }

    public void logout() {
        loggedInUser = null;
        state = EMenuState.SelectLoginRegister;
    }

    private void showWelcomeMenu() {
        System.out.println();
        System.out.println("=== Welcome ===");
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.println("0. Exit");

        switch (readChoice()) {
            case 1 -> state = EMenuState.Login;
            case 2 -> state = EMenuState.Register;
            case 0 -> running = false;
            default -> System.out.println("Invalid option.");
        }
    }

    private void handleLogin() {
        System.out.println();
        System.out.println("=== Login ===");
        String username = prompt("Username: ");
        String password = prompt("Password: ");

        User user = backend.login(username, password);
        if (user == null) {
            System.out.println("Invalid username or password.");
            state = EMenuState.SelectLoginRegister;
            return;
        }

        loggedInUser = user;
        state = EMenuState.Main;
        System.out.println("Logged in as " + user.getUsername() + ".");
    }

    private void handleRegister() {
        System.out.println();
        System.out.println("=== Register ===");
        String username = prompt("Username: ");
        String password = prompt("Password: ");

        User user = backend.register(username, password);
        if (user == null) {
            System.out.println("User already exists.");
            state = EMenuState.SelectLoginRegister;
            return;
        }

        loggedInUser = user;
        state = EMenuState.Main;
        System.out.println("Account created. Logged in as " + user.getUsername() + ".");
    }

    private void showMainMenu() {
        System.out.println();
        System.out.println("=== Main Menu ===");
        System.out.println("User: " + loggedInUser.getUsername());
        System.out.println("1. Show hero tierlist");
        System.out.println("2. Import sample games");
        System.out.println("3. Logout");
        System.out.println("0. Exit");

        switch (readChoice()) {
            case 1 -> state = EMenuState.HeroTierlist;
            case 2 -> state = EMenuState.GameImport;
            case 3 -> logout();
            case 0 -> running = false;
            default -> System.out.println("Invalid option.");
        }
    }

    private void showHeroTierlist() {
        HeroTierlist tierlist = backend.getHeroTierlist();

        System.out.println();
        System.out.println("=== Hero Tierlist ===");
        if (tierlist.getRanks().isEmpty()) {
            System.out.println("No hero data available yet.");
        } else {
            for (Map.Entry<String, List<Hero>> entry : tierlist.getRanks().entrySet()) {
                System.out.println(entry.getKey() + ":");
                for (Hero hero : entry.getValue()) {
                    System.out.println("  - " + hero.getName() + " (" + hero.getWins() + "/" + hero.getTotalGames() + ")");
                }
            }
        }

        prompt("Press Enter to return...");
        state = EMenuState.Main;
    }

    private void importGames() {
        System.out.println();
        System.out.println("=== Import Sample Games ===");
        int count = readPositiveInt("How many sample games to import? ");
        backend.importSampleGames(count);
        System.out.println("Imported " + count + " sample game(s).");
        state = EMenuState.Main;
    }

    private int readChoice() {
        System.out.print("> ");
        String input = scanner.nextLine().trim();
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException ex) {
            return Integer.MIN_VALUE;
        }
    }

    private int readPositiveInt(String label) {
        while (true) {
            String input = prompt(label);
            try {
                int value = Integer.parseInt(input);
                if (value >= 0) {
                    return value;
                }
            } catch (NumberFormatException ignored) {
            }
            System.out.println("Enter a non-negative number.");
        }
    }

    private String prompt(String label) {
        System.out.print(label);
        return scanner.nextLine().trim();
    }
}
