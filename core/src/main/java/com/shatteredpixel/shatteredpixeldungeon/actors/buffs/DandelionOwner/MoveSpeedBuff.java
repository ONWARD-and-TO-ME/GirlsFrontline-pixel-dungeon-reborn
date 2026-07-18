package com.shatteredpixel.shatteredpixeldungeon.actors.buffs.DandelionOwner;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;

public abstract class MoveSpeedBuff extends CleanSpeedBuff {
    @Override
    final public Char.actionType affectType() {
        return Char.actionType.MOVE;
    }
}
