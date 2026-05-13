package project.repositories;

import project.models.Hero;
import java.util.Map;

public interface IHeroRepository {
    Map<String, Hero> getHeroes();
    void updateHero(Hero hero);
    void clear();
}
