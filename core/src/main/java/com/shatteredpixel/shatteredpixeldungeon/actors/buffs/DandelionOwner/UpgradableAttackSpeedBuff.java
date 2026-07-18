package com.shatteredpixel.shatteredpixeldungeon.actors.buffs.DandelionOwner;


import com.watabou.utils.Bundle;

public abstract class UpgradableAttackSpeedBuff extends AttackSpeedBuff {
    private static final String UpgradeTimes = "UpgradeTimes";
    protected int Times;
    @Override
    public void storeInBundle( Bundle bundle ){
        super.storeInBundle(bundle);
        bundle.put(UpgradeTimes, Times);
    }
    @Override
    public void restoreFromBundle( Bundle bundle ){
        super.restoreFromBundle(bundle);
        Times = bundle.getInt(UpgradeTimes);
    }
    protected abstract int maxTimes();
    protected abstract float singleSpeed();
    @Override
    public float speed(){
        return singleSpeed() * Math.min(Times, maxTimes());
    }
}
