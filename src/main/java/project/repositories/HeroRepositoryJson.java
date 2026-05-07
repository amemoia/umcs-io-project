package project.repositories;

import java.util.LinkedHashMap;
import java.util.Map;

import project.models.Hero;

public class HeroRepositoryJson implements IHeroRepository {
    private final Map<String, Hero> heroes = new LinkedHashMap<>();

    @Override
    public Map<String, Hero> getHeroes() {
        return heroes;
    }

    @Override
    public void updateHero(Hero hero) {
        heroes.put(hero.getName(), hero);
    }
}
