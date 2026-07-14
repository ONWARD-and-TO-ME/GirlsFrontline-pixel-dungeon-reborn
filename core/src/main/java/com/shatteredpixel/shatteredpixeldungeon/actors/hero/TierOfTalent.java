package com.shatteredpixel.shatteredpixeldungeon.actors.hero;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;

import java.util.LinkedHashMap;

public class TierOfTalent {
    public static final LinkedHashMap<Talent, Integer> One = new LinkedHashMap<>();
    public static final LinkedHashMap<Talent, Integer> Two = new LinkedHashMap<>();
    public static final LinkedHashMap<Talent, Integer> Three = new LinkedHashMap<>();
    public static final LinkedHashMap<Talent, Integer> ClassOnly_One = new LinkedHashMap<>();
    public static final LinkedHashMap<Talent, Integer> ClassOnly_Two = new LinkedHashMap<>();
    public static final LinkedHashMap<Talent, Integer> ClassOnly_Three = new LinkedHashMap<>();
    public static final LinkedHashMap<Talent, Integer> Four = new LinkedHashMap<>();
    static {
        Hero hero = new Hero();
        for(HeroClass heroClass : HeroClass.values()){
            hero.heroClass = heroClass;
            Talent.initClassTalents(hero);
            for (ArmorAbility ability : heroClass.armorAbilities()){
                hero.armorAbility = ability;
                Talent.initArmorTalents(hero);
            }
        }

        for (Talent talent : hero.talents.get(0).keySet())
            ClassOnly_One.put(talent, 0);
        for (Talent talent : hero.talents.get(1).keySet())
            ClassOnly_Two.put(talent, 0);
        for (Talent talent : hero.talents.get(2).keySet())
            ClassOnly_Three.put(talent, 0);

        for (HeroSubClass subClass : HeroSubClass.values()){
            hero.subClass = subClass;
            Talent.initSubclassTalents(hero);
        }

        for (Talent talent : hero.talents.get(0).keySet())
            One.put(talent, 0);
        for (Talent talent : hero.talents.get(1).keySet())
            Two.put(talent, 0);
        for (Talent talent : hero.talents.get(2).keySet())
            Three.put(talent, 0);
        for (Talent talent : hero.talents.get(3).keySet())
            Four.put(talent, 0);
    }

    public static Talent[] TierTalent( int tier ){
        if (tier == 0)
            return ClassOnly_One.keySet().toArray(new Talent[0]);
        if (tier == 1)
            return ClassOnly_Two.keySet().toArray(new Talent[0]);
        if (tier == 2)
            return ClassOnly_Three.keySet().toArray(new Talent[0]);
        return new Talent[0];
    }
    public static int Tier(Talent talent){
        if (One.containsKey(talent)){
            return 1;
        }else if (Two.containsKey(talent)){
            return 2;
        }else if (Three.containsKey(talent)){
            return 3;
        }else if (Four.containsKey(talent)){
            return 4;
        }
        return 1;
    }
}
