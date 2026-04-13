package com.shatteredpixel.shatteredpixeldungeon.items.fairyitems;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ArtifactRecharge;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hunger;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Recharging;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.InventoryScroll;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRemoveCurse;

public class Letter extends FairyItems {

    @Override
    public void effect(Hero hero) {
        Hunger hunger = hero.buff(Hunger.class);
        if (hunger != null)
            hunger.satisfy(300F);
        Buff.affect(hero, Recharging.class, 15F);
        Buff.affect(hero, ArtifactRecharge.class).prolong(10F).ignoreHornOfPlenty = false;
    }

}
