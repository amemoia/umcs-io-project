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
                case ImportExampleGames  -> handleImportExampleGames();
                case AdminTools          -> showAdminTools();
                case ImportSingleGame     -> handleImportGames();
                case PlayerStats          -> showPlayerStats();
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
        System.out.println("2. Toggle Tierlist Mode (Current: " + (backend.getHeroTierlist().isKdaMode() ? "KDA" : "Winrate") + ")");
        System.out.println("3. Import Game(s)");
        System.out.println("4. View Player Stats");
        if (loggedInUser.getIsAdmin()) {
            System.out.println("5. Admin Tools");
        }
        System.out.println("0. Logout");

        switch (readChoice()) {
            case 1 -> state = EMenuState.HeroTierlist;
            case 2 -> backend.setTierlistKdaMode(!backend.getHeroTierlist().isKdaMode());
            case 3 -> state = EMenuState.ImportSingleGame;
            case 4 -> state = EMenuState.PlayerStats;
            case 5 -> {
                if (loggedInUser.getIsAdmin()) state = EMenuState.AdminTools;
                else System.out.println("Invalid option.");
            }
            case 0 -> logout();
            default -> System.out.println("Invalid option.");
        }
    }

    private void showHeroTierlist() {
        HeroTierlist tierlist = backend.getHeroTierlist();
        System.out.println("\n=== Hero Tierlist (" + (tierlist.isKdaMode() ? "KDA Ratio" : "Winrate") + ") ===");

        if (tierlist.getRanks().isEmpty()) {
            System.out.println("No data available yet.");
        } else {
            for (Map.Entry<String, List<Hero>> entry : tierlist.getRanks().entrySet()) {
                System.out.println(entry.getKey() + ":");
                for (Hero hero : entry.getValue()) {
                    if (tierlist.isKdaMode()) {
                        System.out.printf("  - %s (%.2f KDA) [%d/%d/%d]", hero.name, hero.getKdaRatio(),
                                hero.getTotalKills(), hero.getTotalDeaths(), hero.getTotalAssists());
                    } else {
                        double winRate = hero.getTotalGames() == 0 ? 0 : (double) hero.getWins() / hero.getTotalGames() * 100;
                        System.out.printf("  - %s (%.1f%% WR)", hero.name, winRate);
                    }
                    if (hero.adminComment != null && !hero.adminComment.isEmpty()) {
                        System.out.print("     [Admin Note: " + hero.adminComment + "]");
                    }
                    System.out.println();
                }
            }
        }

        System.out.println("\n0. Back");
        if (readChoice() == 0) {
            state = EMenuState.Main;
        }
    }

    private void showAdminTools() {
        System.out.println("\n=== Admin Tools ===");
        System.out.println("1. Import Example Games");
        System.out.println("2. Add Admin Comment to Hero");
        System.out.println("3. Clear Game Database");
        System.out.println("4. Toggle User Admin Status");
        System.out.println("0. Back");

        switch (readChoice()) {
            case 1 -> state = EMenuState.ImportExampleGames;
            case 2 -> handleAddAdminComment();
            case 3 -> handleClearDatabase();
            case 4 -> handleToggleUserAdmin();
            case 0 -> state = EMenuState.Main;
            default -> System.out.println("Invalid option.");
        }
    }

    private void handleToggleUserAdmin() {
        System.out.print("Username to toggle Admin: ");
        String username = scanner.nextLine().trim();
        if (backend.toggleUserAdmin(username)) {
            System.out.println("User " + username + " admin status toggled successfully.");
        } else {
            System.out.println("User " + username + " not found.");
        }
    }

    private void handleAddAdminComment() {
        System.out.print("Hero Name: ");
        String heroName = scanner.nextLine().trim();
        System.out.print("Comment: ");
        String comment = scanner.nextLine().trim();
        backend.addHeroComment(heroName, comment);
        System.out.println("Comment added.");
    }

    private void handleClearDatabase() {
        System.out.println("Are you sure you want to clear ALL game data? (y/n)");
        System.out.print("> ");
        if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
            backend.clearGameDatabase();
            System.out.println("Database cleared.");
        }
    }

    private void handleImportGames() {
        System.out.println("\n--- Import Games via JSON ---");
        System.out.println("Paste your JSON data below. Enter an empty line to finish:");
        
        StringBuilder jsonInput = new StringBuilder();
        while (true) {
            String line = scanner.nextLine();
            if (line.trim().isEmpty()) break;
            jsonInput.append(line);
        }

        if (jsonInput.isEmpty()) {
            System.out.println("No input received.");
            state = EMenuState.Main;
            return;
        }

        try {
            org.json.JSONObject root = new org.json.JSONObject(jsonInput.toString());
            List<project.models.Game> gamesToImport = new java.util.ArrayList<>();
            
            for (String matchIDKey : root.keySet()) {
                org.json.JSONObject obj = root.getJSONObject(matchIDKey);
                project.models.Game game = new project.models.Game();
                game.matchID = obj.getInt("matchID");
                game.winningTeam = project.models.ETeam.valueOf(obj.getString("winningTeam"));
                
                java.util.Map<String, project.models.Game.PlayerStats> players = new java.util.HashMap<>();
                if (obj.has("teams")) {
                    org.json.JSONObject teamsObj = obj.getJSONObject("teams");
                    for (String teamName : teamsObj.keySet()) {
                        project.models.ETeam team = project.models.ETeam.valueOf(teamName);
                        org.json.JSONObject teamPlayers = teamsObj.getJSONObject(teamName);
                        for (String username : teamPlayers.keySet()) {
                            org.json.JSONObject statsObj = teamPlayers.getJSONObject(username);
                            project.models.Game.PlayerStats stats = new project.models.Game.PlayerStats();
                            stats.heroName = statsObj.getString("heroName");
                            stats.kills = statsObj.getInt("kills");
                            stats.deaths = statsObj.getInt("deaths");
                            stats.assists = statsObj.getInt("assists");
                            stats.team = team;
                            players.put(username, stats);
                        }
                    }
                }
                game.players = players;
                gamesToImport.add(game);
            }

            if (!gamesToImport.isEmpty()) {
                backend.importGames(gamesToImport);
                System.out.println("Successfully imported " + gamesToImport.size() + " games.");
            } else {
                System.out.println("No valid games found in JSON.");
            }
        } catch (Exception e) {
            System.out.println("Error parsing JSON: " + e.getMessage());
            System.out.println("Ensure the JSON matches the required format (with 'teams' and 'matchID').");
        }
        
        state = EMenuState.Main;
    }

    private void handleImportExampleGames() {
        System.out.println("\n=== Import Example Games ===");
        System.out.println("Are you sure you want to import ~50 example games? (y/n)");
        System.out.print("> ");
        String confirm = scanner.nextLine().trim().toLowerCase();
        if (confirm.equals("y")) {
            backend.importExampleGames();
            System.out.println("Successfully imported example games.");
        } else {
            System.out.println("Import cancelled.");
        }
        state = EMenuState.Main;
    }


    private void showPlayerStats() {
        System.out.println("\n=== Player Stats ===");
        System.out.print("Enter username (leave blank for yourself: " + loggedInUser.username + "): ");
        String input = scanner.nextLine().trim();
        String targetUsername = input.isEmpty() ? loggedInUser.username : input;

        Backend.PlayerStatistics stats = backend.getPlayerStats(targetUsername);

        System.out.println("\nStats for " + targetUsername + ":");
        System.out.println("Total Wins: " + stats.wins);
        System.out.println("Total Losses: " + stats.losses);
        System.out.printf("Win Ratio: %.1f%%\n", stats.getWinRatio());
        System.out.println("Total Kills: " + stats.kills);
        System.out.println("Total Deaths: " + stats.deaths);
        System.out.println("Total Assists: " + stats.assists);
        System.out.printf("KDA Ratio: %.2f\n", stats.getKdaRatio());

        System.out.println("\n0. Back");
        readChoice();
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