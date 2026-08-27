package com.shatteredpixel.shatteredpixeldungeon.actors.buffs.DandelionOwner;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.CounterBuff;
import com.watabou.utils.Bundle;

public abstract class ActiveBuff extends CounterBuff implements ActiveAbstract {
    //此类Buff在不满足生效条件的情况下，若是可成长则不移除以保留成长值，若是固定值则直接移除

    {
        type = buffType.NEGATIVE;
    }
    @Override
    public boolean act() {
        float t = TICK();
        if (working())
            onWorking( t );
        else if (this instanceof NotDetach)
            activeTime = 0;
        else
            detach();
        spend(t);
        return true;
    }
    protected float TICK(){
        return 1F;
    }
    protected void onWorking(float time){
        activeTime -= time;
    }
    protected float activeTime;
    final public void addActiveTime(float time){
        activeTime += time;
    }
    final public void setActiveTime(float time){
        activeTime = time;
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
    final public ActiveBuff upgrade(){
        countUp(1F);
        return this;
    }
}
