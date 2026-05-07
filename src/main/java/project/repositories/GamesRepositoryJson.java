package project.repositories;

import java.util.ArrayList;
import java.util.List;

import project.models.Game;

public class GamesRepositoryJson implements IGamesRepository {
    private final List<Game> games = new ArrayList<>();

    @Override
    public List<Game> getGames() {
        return new ArrayList<>(games);
    }

    @Override
    public void importGames(List<Game> games) {
        this.games.addAll(games);
    }

    @Override
    public int getGameCount() {
        return games.size();
    }
}
