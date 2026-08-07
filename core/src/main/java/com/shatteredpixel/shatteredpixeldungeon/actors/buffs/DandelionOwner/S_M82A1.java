package com.shatteredpixel.shatteredpixeldungeon.actors.buffs.DandelionOwner;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;

public class S_M82A1 extends CleanSpeed implements ActiveAbstract.NotDetach {
    @Override
    public float values() {
        return 90;
    }
    @Override
    public Char.actionType affectType() {
        return Char.actionType.ALL;
    }
}
