package project.repositories;

import java.util.List;

import project.models.Game;

public interface IGamesRepository {
    List<Game> getGames();

    void importGames(List<Game> games);

    int getGameCount();
}
