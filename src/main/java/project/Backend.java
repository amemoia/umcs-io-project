package project;

import java.util.ArrayList;
import java.util.List;

import project.models.Game;
import project.models.Hero;
import project.models.HeroTierlist;
import project.models.User;
import project.repositories.*;

public class Backend {
    public static class PlayerStatistics {
        public int wins;
        public int losses;
        public int kills;
        public int deaths;
        public int assists;

        public double getWinRatio() {
            if (wins + losses == 0) return 0.0;
            return (double) wins / (wins + losses) * 100.0;
        }

        public double getKdaRatio() {
            if (deaths == 0) return kills + assists;
            return (double) (kills + assists) / deaths;
        }
    }

    private final IUserRepository userRepo;
    private final IGamesRepository gameRepo;
    private final IHeroRepository heroRepo;
    private final HeroTierlist heroTierlist;

    public Backend() {
        this(
                new UserRepositoryJson("data/users.json"),
                new GamesRepositoryJson("data/games.json"),
                new HeroRepositoryJson("data/heroes.json")
        );
    }

    public Backend(IUserRepository userRepo, IGamesRepository gameRepo, IHeroRepository heroRepo) {
        this.userRepo = userRepo;
        this.gameRepo = gameRepo;
        this.heroRepo = heroRepo;
        this.heroTierlist = new HeroTierlist();
        this.heroTierlist.buildRanks(heroRepo);
    }

    public User login(String username, String password) {
        return userRepo.authenticate(username, password);
    }

    public User register(String username, String password) {
        userRepo.addUser(username, password);
        return userRepo.authenticate(username, password);
    }

    public void importGames(List<Game> games) {
        gameRepo.importGames(games);
        updateHeroTierlist();
    }

    public void importExampleGames() {
        GamesRepositoryJson exampleRepo = new GamesRepositoryJson("data/games_example.json");
        gameRepo.clear();
        gameRepo.importGames(exampleRepo.getGames());
        updateHeroTierlist();
    }

    public void addHeroComment(String heroName, String comment) {
        Hero hero = heroRepo.getHeroes().computeIfAbsent(heroName, Hero::new);
        hero.adminComment = comment;
        heroRepo.updateHero(hero);
    }

    public void clearGameDatabase() {
        gameRepo.clear();
        for (Hero hero : heroRepo.getHeroes().values()) {
            hero.setWins(0);
            hero.setTotalGames(0);
            hero.setTotalKills(0);
            hero.setTotalDeaths(0);
            hero.setTotalAssists(0);
            heroRepo.updateHero(hero);
        }
        heroTierlist.buildRanks(heroRepo);
    }

    public void setTierlistKdaMode(boolean kdaMode) {
        heroTierlist.setKdaMode(kdaMode, heroRepo);
    }

    public HeroTierlist getHeroTierlist() {
        return heroTierlist;
    }

    public void updateHeroTierlist() {
        heroTierlist.updateFromWinrates(gameRepo, heroRepo);
    }

    public List<Hero> getHeroes() {
        return new ArrayList<>(heroRepo.getHeroes().values());
    }

    public PlayerStatistics getPlayerStats(String username) {
        PlayerStatistics stats = new PlayerStatistics();
        for (Game game : gameRepo.getGames()) {
            if (game.players.containsKey(username)) {
                Game.PlayerStats pStats = game.players.get(username);
                stats.kills += pStats.kills;
                stats.deaths += pStats.deaths;
                stats.assists += pStats.assists;
                if (game.winningTeam != project.models.ETeam.None) {
                    if (game.winningTeam == pStats.team) {
                        stats.wins++;
                    } else {
                        stats.losses++;
                    }
                }
            }
        }
        return stats;
    }

    public boolean toggleUserAdmin(String username) {
        User user = userRepo.getUser(username);
        if (user != null) {
            user.setIsAdmin(!user.getIsAdmin());
            userRepo.updateUser(user);
            return true;
        }
        return false;
    }
}