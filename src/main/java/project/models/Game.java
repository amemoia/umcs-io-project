package project.models;

import java.util.LinkedHashMap;
import java.util.Map;

public class Game {
    private final int matchId;
    private final Map<String, Hero> players;
    private final ETeam winningTeam;

    public Game(int matchId, Map<String, Hero> players, ETeam winningTeam) {
        this.matchId = matchId;
        this.players = players;
        this.winningTeam = winningTeam;
    }

    public int getMatchId() {
        return matchId;
    }

    public Map<String, Hero> getPlayers() {
        return players;
    }

    public ETeam getWinningTeam() {
        return winningTeam;
    }

    public static Game sample(int matchId) {
        Map<String, Hero> players = new LinkedHashMap<>();
        players.put("player1", new Hero(matchId % 2 == 0 ? "Astra" : "Brutus"));
        players.put("player2", new Hero(matchId % 3 == 0 ? "Cyra" : "Doran"));
        return new Game(matchId, players, matchId % 2 == 0 ? ETeam.HiddenKing : ETeam.ArchMother);
    }
}
