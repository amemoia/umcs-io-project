package project;

import project.repositories.UserRepositoryJson;
import project.repositories.GamesRepositoryJson;
import project.repositories.HeroRepositoryJson;

public class Main {
    public static void main(String[] args) {
        UserRepositoryJson userRepo = new UserRepositoryJson("data/users.json");
        GamesRepositoryJson gameRepo = new GamesRepositoryJson("data/games.json");
        HeroRepositoryJson heroRepo = new HeroRepositoryJson("data/heroes.json");

        Backend backend = new Backend(userRepo, gameRepo, heroRepo);
        Menu menu = new Menu(backend);
        menu.display();
    }
}