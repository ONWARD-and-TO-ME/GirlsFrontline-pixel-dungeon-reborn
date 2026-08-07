package com.shatteredpixel.shatteredpixeldungeon.actors.buffs.DandelionOwner;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;

public abstract class AttackSpeed extends CleanSpeed{
    @Override
    final public Char.actionType affectType() {
        return Char.actionType.ATTACK;
    }

    public static class SA_MP_446 extends AttackSpeed implements ActiveAbstract.Upgradable {
        @Override
        public float singleValue() {
            return 20;
        }
        @Override
        public int maxTimes() {
            return 4;
        }
    }

    public static class SA_Spitfire extends AttackSpeed {
        @Override
        public float values() {
            return 50;
        }
    }

}