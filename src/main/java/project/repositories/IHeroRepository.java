package project.repositories;

import java.util.Map;

import project.models.Hero;

public interface IHeroRepository {
    Map<String, Hero> getHeroes();

    void updateHero(Hero hero);
}
