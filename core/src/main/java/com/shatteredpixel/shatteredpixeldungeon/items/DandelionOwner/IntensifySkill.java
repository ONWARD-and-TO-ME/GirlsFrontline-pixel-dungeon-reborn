package com.shatteredpixel.shatteredpixeldungeon.items.DandelionOwner;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.DandelionShield;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.watabou.utils.Bundle;

import java.util.ArrayList;

public class IntensifySkill extends Item {
    {
        unique = true;
        defaultAction = Intensify;
    }
    public int coolDownLeft;
    private static final String Intensify = "Intensify";
    @Override
    public ArrayList<String> actions(Hero hero ){
        ArrayList<String> actions = super.actions(hero);
        actions.add(Intensify);
        return actions;
    }
    @Override
    public void execute( Hero hero, String action ) {
        super.execute(hero, action);
        if (action.equals(Intensify)){
            Buff.affect(hero, DandelionShield.class).incShield((int) (Math.ceil(Dungeon.curDepth() / 5F) * 3));
            Buff.affect(hero, Intensify.class, 10F);
            coolDownLeft = 50;
            updateQuickslot();
        }
    }

    protected CoolDownTracker coolDownTracker;
    @Override
    public void Tracker( Char owner ){
        super.Tracker(owner);
        if (coolDownTracker == null){
            coolDownTracker = new CoolDownTracker();
            coolDownTracker.attachTo(owner);
        }
    }
    @Override
    public void stopTrack(){
        super.stopTrack();
        if (coolDownTracker != null){
            coolDownTracker.detach();
            coolDownTracker = null;
        }
    }
    private static final String CoolDownLeft_Intensify = "CoolDownLeft_Intensify";
    @Override
    public void restoreFromBundle( Bundle bundle ){
        super.restoreFromBundle(bundle);
        coolDownLeft    = bundle.getInt(CoolDownLeft_Intensify);
    }
    @Override
    public void storeInBundle( Bundle bundle ){
        super.storeInBundle(bundle);
        bundle.put(CoolDownLeft_Intensify, coolDownLeft);
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }
    @Override
    public boolean isIdentified() {
        return true;
    }
    public class CoolDownTracker extends Buff {
        {
            revivePersists = true;
        }
        @Override
        public boolean act() {
            if (coolDownLeft > 0)
                coolDownLeft--;
            spend(TICK);
            return true;
        }
    }
    public static class Intensify extends FlavourBuff {

    }
}
