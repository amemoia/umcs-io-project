package project;

import java.util.ArrayList;
import java.util.List;

import project.models.Game;
import project.models.Hero;
import project.models.HeroTierlist;
import project.models.User;

public class Backend {
    private final IUserRepository userRepo;
    private final IGamesRepository gameRepo;
    private final IHeroRepository heroRepo;
    private final HeroTierlist heroTierlist;

    public Backend() {
        this(new UserRepositoryJson(), new GamesRepositoryJson(), new HeroRepositoryJson());
    }

    public Backend(IUserRepository userRepo, IGamesRepository gameRepo, IHeroRepository heroRepo) {
        this.userRepo = userRepo;
        this.gameRepo = gameRepo;
        this.heroRepo = heroRepo;
        this.heroTierlist = new HeroTierlist();
    }

    public User login(String username, String password) {
        return userRepo.authenticate(username, password);
    }

    public User register(String username, String password) {
        return userRepo.addUser(username, password);
    }

    public void importGames(List<Game> games) {
        gameRepo.importGames(games);
        updateHeroTierlist();
    }

    public void importSampleGames(int count) {
        List<Game> sampleGames = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            sampleGames.add(Game.sample(i + gameRepo.getGameCount() + 1));
        }
        importGames(sampleGames);
    }

    public HeroTierlist getHeroTierlist() {
        updateHeroTierlist();
        return heroTierlist;
    }

    public void updateHeroTierlist() {
        heroTierlist.updateFromWinrates(gameRepo, heroRepo);
    }

    public List<Hero> getHeroes() {
        return new ArrayList<>(heroRepo.getHeroes().values());
    }
}
