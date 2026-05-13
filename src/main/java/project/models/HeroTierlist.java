package project.models;

import project.repositories.IGamesRepository;
import project.repositories.IHeroRepository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HeroTierlist {
    private final Map<String, List<Hero>> ranks = new LinkedHashMap<>();
    private boolean kdaMode = false;

    public Map<String, List<Hero>> getRanks() {
        return ranks;
    }

    public boolean isKdaMode() {
        return kdaMode;
    }

    public void setKdaMode(boolean kdaMode, IHeroRepository heroRepo) {
        this.kdaMode = kdaMode;
        buildRanks(heroRepo);
    }

    public void updateFromWinrates(IGamesRepository gamesRepo, IHeroRepository heroRepo) {
        Map<String, Integer> winsByHero = new LinkedHashMap<>();
        Map<String, Integer> totalGamesByHero = new LinkedHashMap<>();
        Map<String, Integer> killsByHero = new LinkedHashMap<>();
        Map<String, Integer> deathsByHero = new LinkedHashMap<>();
        Map<String, Integer> assistsByHero = new LinkedHashMap<>();

        for (Game game : gamesRepo.getGames()) {
            for (Game.PlayerStats playerStats : game.players.values()) {
                String heroName = playerStats.heroName;
                totalGamesByHero.put(heroName, totalGamesByHero.getOrDefault(heroName, 0) + 1);
                killsByHero.put(heroName, killsByHero.getOrDefault(heroName, 0) + playerStats.kills);
                deathsByHero.put(heroName, deathsByHero.getOrDefault(heroName, 0) + playerStats.deaths);
                assistsByHero.put(heroName, assistsByHero.getOrDefault(heroName, 0) + playerStats.assists);

                if (game.winningTeam != ETeam.None && playerStats.team == game.winningTeam) {
                    winsByHero.put(heroName, winsByHero.getOrDefault(heroName, 0) + 1);
                }
            }
        }

        for (String heroName : totalGamesByHero.keySet()) {
            Hero hero = heroRepo.getHeroes().computeIfAbsent(heroName, Hero::new);
            hero.setWins(winsByHero.getOrDefault(heroName, 0));
            hero.setTotalGames(totalGamesByHero.get(heroName));
            hero.setTotalKills(killsByHero.getOrDefault(heroName, 0));
            hero.setTotalDeaths(deathsByHero.getOrDefault(heroName, 0));
            hero.setTotalAssists(assistsByHero.getOrDefault(heroName, 0));
            heroRepo.updateHero(hero);
        }

        buildRanks(heroRepo);
    }

    public void buildRanks(IHeroRepository heroRepo) {
        ranks.clear();
        ranks.put("S", new ArrayList<>());
        ranks.put("A", new ArrayList<>());
        ranks.put("B", new ArrayList<>());
        ranks.put("C", new ArrayList<>());

        for (Hero hero : heroRepo.getHeroes().values()) {
            double metric;
            if (kdaMode) {
                metric = hero.getKdaRatio();
            } else {
                metric = hero.getTotalGames() == 0 ? 0 : (double) hero.getWins() / hero.getTotalGames();
            }
            ranks.get(rankFor(metric)).add(hero);
        }
    }

    private String rankFor(double metric) {
        if (kdaMode) {
            if (metric >= 4.0) return "S";
            if (metric >= 3.0) return "A";
            if (metric >= 2.0) return "B";
            return "C";
        } else {
            if (metric >= 0.575) return "S";
            if (metric >= 0.525) return "A";
            if (metric >= 0.475) return "B";
            return "C";
        }
    }
}