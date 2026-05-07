package project;

import project.models.User;
import project.repositories.IUserRepository;
import project.repositories.IGamesRepository;
import project.repositories.IHeroRepository;
import project.models.HeroTierlist;

public class Backend {

    private IUserRepository userRepo;
    private IGamesRepository gameRepo;
    private IHeroRepository heroRepo;
    private HeroTierlist heroTierlist;

    public Backend(IUserRepository userRepo,
                   IGamesRepository gameRepo,
                   IHeroRepository heroRepo) {
        this.userRepo     = userRepo;
        this.gameRepo     = gameRepo;
        this.heroRepo     = heroRepo;
        this.heroTierlist = new HeroTierlist();
    }

    public User login(String username, String password) throws Exception {
        throw new UnsupportedOperationException("login() not implemented yet");
    }

    public void register(String username, String password) throws Exception {
        throw new UnsupportedOperationException("register() not implemented yet");
    }

    public void importGames(String filePath) throws Exception {
        throw new UnsupportedOperationException("importGames() not implemented yet");
    }

    public void getHeroTierlist() throws Exception {
        throw new UnsupportedOperationException("getHeroTierlist() not implemented yet");
    }

    public void updateHeroTierlist() throws Exception {
        throw new UnsupportedOperationException("updateHeroTierlist() not implemented yet");
    }
}