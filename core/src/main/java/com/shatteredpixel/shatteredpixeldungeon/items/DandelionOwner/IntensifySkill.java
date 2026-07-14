package com.shatteredpixel.shatteredpixeldungeon.items.DandelionOwner;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.DandelionShield;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;

import java.util.ArrayList;

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
