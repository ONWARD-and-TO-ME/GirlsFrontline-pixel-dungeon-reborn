package com.shatteredpixel.shatteredpixeldungeon.items.fairyitems;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ArtifactRecharge;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.CounterBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hunger;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Recharging;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.InventoryScroll;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRemoveCurse;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;

public class Commander extends FairyItems {

    @Override
    public void effect(Hero hero) {
        Dungeon.LimitedDrops.DropLevel.used();
        Buff.affect(hero, Command.class).countUp(3);
    }

    public static class Command extends CounterBuff{
        public int icon() {
            return BuffIndicator.INVISIBLE;
        }
        @Override
        public String desc(){
            return Messages.format("%d", (int)count());
        }
    }
}
