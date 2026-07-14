package com.shatteredpixel.shatteredpixeldungeon.items.DandelionOwner;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;

import java.util.ArrayList;

public abstract class SkillItem extends Item {
    {
        unique = true;
        defaultAction = AC_SKILL;
    }
    @Override
    public ArrayList<String> actions(Hero hero ){
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_SKILL);
        return actions;
    }
    @Override
    public void execute( Hero hero, String action ) {
        super.execute(hero, action);
        if (action.equals(AC_SKILL)){
            if (coolDownLeft > 0)
                GLog.w(Messages.get(SkillItem.class, "CoolDown"));
            else
                onSkill( hero );
        }
    }
    public abstract void onSkill( Hero hero );
    private static final String CoolDownLeft_SkillItem = "CoolDownLeft_SkillItem";
    @Override
    public void restoreFromBundle( Bundle bundle ){
        super.restoreFromBundle(bundle);
        coolDownLeft    = bundle.getInt(CoolDownLeft_SkillItem);
    }
    @Override
    public void storeInBundle( Bundle bundle ){
        super.storeInBundle(bundle);
        bundle.put(CoolDownLeft_SkillItem, coolDownLeft);
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
    public class CoolDownTracker extends Buff {
        @Override
        public boolean act() {
            if (coolDownLeft > 0)
                coolDownLeft--;
            spend(TICK);
            return true;
        }
    }
    @Override
    public boolean isUpgradable() {
        return false;
    }
    @Override
    public boolean isIdentified() {
        return true;
    }
}
