package com.shatteredpixel.shatteredpixeldungeon.actors.buffs.DandelionOwner;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.watabou.utils.Bundle;

public abstract class Increases extends FlavourBuff {
    {
        this.type = buffType.POSITIVE;
    }
    protected float percent;
    protected int level;
    protected int maxLvl = 1;
    @SuppressWarnings("unchecked")
    public <T extends Increases> T upgrade(){
        if (level < maxLvl)
            level++;
        return (T) this;
    }
    public float percent() {
        if (maxLvl == 1)
            return percent;
        return percent * level;
    }
    public void notDetach(){
        deActivate();
    }
    public abstract float increase( float base );
    public abstract float otherGet( float base );
    private static final String LEVEL   = "LEVEL";
    @Override
    public void storeInBundle( Bundle bundle ) {
        super.storeInBundle(bundle);
        bundle.put(LEVEL, level);
    }

    @Override
    public void restoreFromBundle( Bundle bundle ) {
        super.restoreFromBundle(bundle);
        level = bundle.getInt(LEVEL);
    }
}
