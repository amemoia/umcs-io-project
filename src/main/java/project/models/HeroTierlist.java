package project.models;

import project.repositories.IGamesRepository;
import project.repositories.IHeroRepository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HeroTierlist {
    private final Map<String, List<Hero>> ranks = new LinkedHashMap<>();

    public Map<String, List<Hero>> getRanks() {
        return ranks;
    }

    public void updateFromWinrates(IGamesRepository gamesRepo, IHeroRepository heroRepo) {
        Map<String, HeroStats> statsByHero = new LinkedHashMap<>();

        for (Game game : gamesRepo.getGames()) {
            for (Hero hero : game.players.values()) {
                HeroStats stats = statsByHero.computeIfAbsent(hero.name, ignored -> new HeroStats());
                stats.totalGames++;
                if (game.winningTeam == ETeam.HiddenKing) {
                    stats.wins++;
                }
            }
        }

        ranks.clear();
        ranks.put("S", new ArrayList<>());
        ranks.put("A", new ArrayList<>());
        ranks.put("B", new ArrayList<>());
        ranks.put("C", new ArrayList<>());

        for (Map.Entry<String, HeroStats> entry : statsByHero.entrySet()) {
            Hero hero = heroRepo.getHeroes().computeIfAbsent(entry.getKey(), Hero::new);
            HeroStats stats = entry.getValue();
            hero.updateStats(stats.wins, stats.totalGames - stats.wins);
            double winRate = stats.totalGames == 0 ? 0 : (double) stats.wins / stats.totalGames;
            ranks.get(rankFor(winRate)).add(hero);
            heroRepo.updateHero(hero);
        }
    }

    private String rankFor(double winRate) {
        if (winRate >= 0.75) return "S";
        if (winRate >= 0.60) return "A";
        if (winRate >= 0.45) return "B";
        return "C";
    }

    private static final class HeroStats {
        private int wins;
        private int totalGames;
    }
}