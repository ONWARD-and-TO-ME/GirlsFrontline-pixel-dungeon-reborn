package com.shatteredpixel.shatteredpixeldungeon.items.DandelionOwner;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.DandelionShield;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;

public class IntensifySkill extends SkillItem {
    @Override
    public void onSkill( Hero hero ){
        Buff.affect(hero, DandelionShield.class).incShield((int) (Math.ceil(Dungeon.curDepth() / 5F) * 3));
        Buff.affect(hero, Intensify.class, 10F);
        hero.spendAndNext(Actor.TICK);
        coolDownLeft = 50;
        updateQuickslot();
    }
    public static class Intensify extends FlavourBuff {

    }
}
