package com.shatteredpixel.shatteredpixeldungeon.actors.buffs.DandelionOwner;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.utils.Bundle;

public abstract class CleanSpeedBuff extends Buff {
    @Override
    public boolean act() {
        float t = TICK();
        if (working()) {
            activeTime -= t;
            if (effectAble())
                target.spendKeepType(speed() * t);
        } else
            activeTime = 0;
        spend(t);
        return true;
    }
    private float TICK(){
        if (speed() >= 0.1F && activeTime >= 0.1F)
            return 0.1F;
        if (speed() >= 0.01F && activeTime >= 0.01F)
            return 0.01F;
        return 0.1F;
    }
    @Override
    public int icon() {
        return working()
                ? icons()
                : BuffIndicator.NONE;
    }
    public int icons(){
        return BuffIndicator.INVISIBLE;
    }
    private boolean effectAble(){
        return affectType() == Char.actionType.ALL
                || target.actionType() == Char.actionType.ALL
                || target.actionType() == affectType();
    }
    public abstract Char.actionType affectType();
    public abstract float speed();
    private float activeTime;
    final public void addActiveTime(float time){
        activeTime += time;
    }
    final public void setActiveTime(float time){
        activeTime = time;
    }
    final public boolean working(){
        return activeTime > 0;
    }
    private static final String ACTIVE_TIME = "ACTIVE_TIME";
    @Override
    public void storeInBundle( Bundle bundle ){
        super.storeInBundle(bundle);
        bundle.put(ACTIVE_TIME, activeTime);
    }
    @Override
    public void restoreFromBundle( Bundle bundle ){
        super.restoreFromBundle(bundle);
        activeTime = bundle.getFloat(ACTIVE_TIME);
    }
}
