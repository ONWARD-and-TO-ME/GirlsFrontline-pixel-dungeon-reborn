package com.shatteredpixel.shatteredpixeldungeon.actors.buffs.DandelionOwner;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;

public abstract class CleanSpeed extends ActiveBuff {
    //这个是减速Buff，而后再以行动类型细分为降低攻速/移速
    //此减速逻辑是只对生效期间生效，由生效转变为不生效的过程中，不应当产生遗留影响，且尽量贴合原有变速逻辑。

    @Override
    public float TICK(){
        if (!working() || activeTime >= 0.1F)
            return 0.1F;
        return activeTime;
    }
    @Override
    protected void onWorking(float time){
        super.onWorking(time);
        if (effectAble())
            target.spendKeepType(values() * time / 100F);
    }
    private boolean effectAble(){
        //Buff在act的时候，target总是已经act过了。
        return affectType() == Char.actionType.ALL
                || target.actionType() == Char.actionType.ALL
                || target.actionType() == affectType();
    }
    public abstract Char.actionType affectType();
}
