package project;

import project.models.EMenuState;
import project.models.Hero;
import project.models.HeroTierlist;
import project.models.User;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Menu {

    private EMenuState state = EMenuState.SelectLoginRegister;
    private final Backend backend;
    private User loggedInUser;
    private boolean running = true;

    private final Scanner scanner = new Scanner(System.in);

    public Menu(Backend backend) {
        this.backend = backend;
    }

    public void display() {
        while (running) {
            switch (state) {
                case SelectLoginRegister -> showSelectLoginRegister();
                case Login               -> handleLogin();
                case Register            -> handleRegister();
                case Main                -> showMain();
                case HeroTierlist        -> showHeroTierlist();
                case GameImport          -> showGameImport();
            }
        }
        System.out.println("Goodbye!");
    }

    public void logout() {
        loggedInUser = null;
        state = EMenuState.SelectLoginRegister;
    }

    private void showSelectLoginRegister() {
        System.out.println("\n=== Welcome ===");
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
        System.out.println("\n=== Login ===");
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        User user = backend.login(username, password);
        if (user == null) {
            System.out.println("Invalid username or password.");
            state = EMenuState.SelectLoginRegister;
            return;
        }

        loggedInUser = user;
        state = EMenuState.Main;
    }

    private void handleRegister() {
        System.out.println("\n=== Register ===");
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        User user = backend.register(username, password);
        if (user == null) {
            System.out.println("Username already taken.");
            state = EMenuState.SelectLoginRegister;
            return;
        }

        loggedInUser = user;
        state = EMenuState.Main;
    }

    private void showMain() {
        System.out.println("\n=== Main Menu === " + loggedInUser.username
                + (loggedInUser.getIsAdmin() ? " [ADMIN]" : ""));
        System.out.println("1. Hero Tierlist");
        System.out.println("2. Import Sample Games");
        System.out.println("3. Update Hero Tierlist");
        System.out.println("0. Logout");

        switch (readChoice()) {
            case 1 -> state = EMenuState.HeroTierlist;
            case 2 -> state = EMenuState.GameImport;
            case 3 -> backend.updateHeroTierlist();
            case 0 -> logout();
            default -> System.out.println("Invalid option.");
        }
    }

    private void showHeroTierlist() {
        System.out.println("\n=== Hero Tierlist ===");

        HeroTierlist tierlist = backend.getHeroTierlist();
        if (tierlist.getRanks().isEmpty()) {
            System.out.println("No data available yet.");
        } else {
            for (Map.Entry<String, List<Hero>> entry : tierlist.getRanks().entrySet()) {
                System.out.println(entry.getKey() + ":");
                for (Hero hero : entry.getValue()) {
                    System.out.println("  - " + hero.name
                            + " (" + hero.getWins() + "/" + hero.getTotalGames() + ")");
                }
            }
        }

        System.out.println("\n0. Back");
        if (readChoice() == 0) {
            state = EMenuState.Main;
        }
    }

    private void showGameImport() {
        System.out.println("\n=== Import Sample Games ===");
        System.out.print("How many sample games to import? ");
        String input = scanner.nextLine().trim();

        int count;
        try {
            count = Integer.parseInt(input);
            if (count < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            System.out.println("Enter a valid non-negative number.");
            state = EMenuState.Main;
            return;
        }

        backend.importSampleGames(count);
        System.out.println("Imported " + count + " sample game(s).");
        state = EMenuState.Main;
    }

    private int readChoice() {
        System.out.print("> ");
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return Integer.MIN_VALUE;
        }
    }
}