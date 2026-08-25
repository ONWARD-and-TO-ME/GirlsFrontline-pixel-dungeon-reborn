package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.DandelionOwner;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.watabou.utils.Bundle;

public abstract class ElitePuppet extends Puppet{
    private float next_skill_time;
    protected abstract boolean canSkill();
    protected abstract float skillCD();
    protected abstract void skill();
    @Override
    public boolean act(){
        if (curTime() >= next_skill_time && canSkill()){
            next_skill_time = (float) Math.floor(next_skill_time + skillCD());
            skill();
        }
        return super.act();
    }
    @Override
    public void dropCore(){
        Dungeon.level.drop(core.broken(), Dungeon.level.randomDestination( this ));
    }
    private static final String NEXT_SKILL_TIME = "NEXT_SKILL_TIME";
    @Override
    public void storeInBundle( Bundle bundle ) {
        super.storeInBundle( bundle );
        bundle.put(NEXT_SKILL_TIME, next_skill_time);
    }
    @Override
    public void restoreFromBundle( Bundle bundle ) {
        super.restoreFromBundle( bundle );
        next_skill_time = bundle.getFloat(NEXT_SKILL_TIME);
    }
}
