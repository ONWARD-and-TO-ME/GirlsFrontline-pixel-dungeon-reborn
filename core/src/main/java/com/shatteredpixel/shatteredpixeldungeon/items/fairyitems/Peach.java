package com.shatteredpixel.shatteredpixeldungeon.items.fairyitems;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.BasicBuffs;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hunger;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.watabou.utils.Random;

public class Peach extends FairyItems {

    @Override
    public void effect(Hero hero) {
        Hunger hunger = hero.buff(Hunger.class);
        if (hunger != null)
            hunger.satisfy(100F);
        switch (Random.Int(5)){
            case 0: Buff.affect(hero, BasicBuffs.Increase.class, 50).set(1.20F);break;
            case 1: Buff.affect(hero, BasicBuffs.Velocity.class, 50).set(1.50F);break;
            case 2: Buff.affect(hero, BasicBuffs.Accuracy.class, 50).set(2.00F);break;
            case 3: Buff.affect(hero, BasicBuffs.Evasion.class,  50).set(1.50F);break;
            case 4: Buff.affect(hero, BasicBuffs.Reduce.class,   50).set(0.75F);break;
        }
        Talent.onFoodEaten(hero, 0, this);

        Statistics.foodEaten++;
        Badges.validateFoodEaten();
    }

}
