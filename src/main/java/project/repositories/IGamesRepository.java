package project.repositories;

import project.models.Game;
import java.util.List;

public interface IGamesRepository {
    List<Game> getGames();
    void importGames(List<Game> games);
    int getGameCount();
    void clear();
}
